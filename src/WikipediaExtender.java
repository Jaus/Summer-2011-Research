import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import edu.mit.jwi.*;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

import jsl.tools.JWITools;

import edu.jhu.nlp.wikipedia.*;

import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;

import Jama.Matrix;

// dat class...
public class WikipediaExtender {

	public static final String GRAMMAR_FILE = "/home/jaus/Documents/Java/stanford-parser-2011-06-19/grammar/englishFactored.ser.gz";
	
	public static final List<String> acronyms = Arrays.asList("Inc", "No", "ca", "lat");

	private edu.mit.jwi.Dictionary dict;
	private LexicalizedParser parser;
	private TreebankLanguagePack tlp;
	private GrammaticalStructureFactory gsf;
	private WikiXMLParser wxsp;
	
	POS[] posList;

	long added;
	int MAX_DEPTH = 16;
	
	public edu.mit.jwi.Dictionary dict() {return dict;}
	public LexicalizedParser parser() {return parser;}
	public GrammaticalStructureFactory gsFactory() {return gsf;}
	
	// left blank to be overridden
	public void handlePage(WikiPage page) {}
	
	public WikipediaExtender(String wordnetpath, String wikipediapath) {
		added = 0;
		posList = new POS[]{POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB};
		
		// prepare lexicalized parser
		parser = new LexicalizedParser(GRAMMAR_FILE);
		parser.setOptionFlags(new String[]{"-outputFormat", "typedDependencies"});
		tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
	
		// prepare WordNet
		URL url = null;
		try {url = new URL("file", null, wordnetpath);} 
		catch (MalformedURLException e) {e.printStackTrace();}
		if (url == null) return;
		
		dict = new edu.mit.jwi.Dictionary(url);
		try {dict.open();}
		catch (IOException e) {e.printStackTrace();}
		
		wxsp = WikiXMLParserFactory.getSAXParser(wikipediapath);
		
		try {

			wxsp.setPageCallback(new PageCallbackHandler() { 
				public void process(WikiPage page) {
					handlePage(page);
				}
			});
			
		} catch(Exception e) {
		
			e.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		wxsp.parse();
	}
	
	public ResultPair bestMatch(List<String> candidates, String compText) {
		ISynset best = null;
		double bestScore = -1.0;
		String bestCandidate = "";
		System.err.println("" + candidates.size() + " candidates");
		
		for (String word : candidates) {
			// take a word and find its index word in WordNet
			IIndexWord index = dict().getIndexWord(word, POS.NOUN);

			// find all the synsets associated with that index and score them
			System.err.println("" + index.getWordIDs().size() + " synsets");
			for (IWordID wordId : index.getWordIDs()) {
				ISynset concept = dict().getSynset(wordId.getSynsetID());
				double score = match(compText, concept.getGloss());
				System.err.println("Score: " + score);
				if (score > bestScore) {
					best = concept;
					bestScore = score;
					bestCandidate = word;
				}
			}
		}
		
		if (best == null) return null;
		return new ResultPair(best, bestCandidate);
	}
	
	public double match(String p, String s) {
	
		Set<ISynset> pConcepts = new HashSet<ISynset>();
		Set<ISynset> sConcepts = new HashSet<ISynset>();
		Set<ISynset> total = new HashSet<ISynset>();
		
		// ** get all concepts from the wikipedia page ** //
		Tokenizer<Word> tokenizer = PTBTokenizer.factory().getTokenizer(new StringReader(p));
		for (Word word : tokenizer.tokenize()) {
			IIndexWord index = null;
			int i = 0;
			/*while (index==null && i<posList.length)*/ index = dict.getIndexWord(word.word(), /*posList[i++]*/ POS.NOUN);
			if (index == null) continue;
			
			for (IWordID wordId : index.getWordIDs()) {
				ISynset concept = dict.getSynset(wordId.getSynsetID());
				if (concept == null) continue;
				pConcepts.add(concept);
				total.add(concept);
			}
		}
		
		// ** get all concepts from the synset gloss ** //
		tokenizer = PTBTokenizer.factory().getTokenizer(new StringReader(s));
		for (Word word : tokenizer.tokenize()) {
			IIndexWord index = null;
			int i = 0;
			/*while (index==null && i<posList.length)*/ index = dict.getIndexWord(word.word(),  /*posList[i++]*/ POS.NOUN);
			if (index == null) continue;
			
			for (IWordID wordId : index.getWordIDs()) {
				ISynset concept = dict.getSynset(wordId.getSynsetID());
				if (concept == null) continue;
				sConcepts.add(concept);
				total.add(concept);
			}
		}
		
		// ** build v1 and v2 vectors from p and s ** //
		int size = total.size();
		if (size == 0) return 0.0;
		
		double[] v1 = new double[size];
		double[] v2 = new double[size];
		List<ISynset> conceptList = new LinkedList<ISynset>();
		
		int count = 0;
		Iterator<ISynset> iter = total.iterator();
		while (iter.hasNext()) {
			ISynset synset = iter.next();
			
			if (pConcepts.contains(synset)) v1[count] = 1;
			else v1[count] = 0;
			
			if (sConcepts.contains(synset)) v2[count] = 1;
			else v2[count] = 0;
			
			conceptList.add(synset);
			++count;
		}
		
		// ** build semantic matrix ** //
		double[][] sm = new double[size][size];
		
		System.err.print("" + size + " terms");
		for (int i=0; i<size; ++i) {
			if (i%100==0) System.err.print(".");
			for (int j=0;j<size; ++j) {
				if (i==j) sm[i][j] = 1;
				else sm[i][j] = sim(conceptList.get(i), conceptList.get(j));
			}
		}
		
		// ** compute final similarity score ** //
		Matrix mv1 = new Matrix(v1, 1),
		       mv2 = new Matrix(v2, 1),
		       msm = new Matrix(sm);
		
		/*
		mv1.print(2, 0);
		mv2.print(2, 0);
		msm.print(2, 2);
		mv1.times(msm).times(mv2.transpose()).print(2, 2);
		System.err.println("" + mv1.times(msm).times(mv2.transpose()).get(0, 0) + "\n" + Math.sqrt(mv1.times(msm).times(mv1.transpose()).get(0, 0)) + "\n" + Math.sqrt(mv2.times(msm).times(mv2.transpose()).get(0, 0)));//*/
		       
		double score = mv1.times(msm).times(mv2.transpose()).get(0, 0) /
			(Math.sqrt(mv1.times(msm).times(mv1.transpose()).get(0, 0)) * 
			 Math.sqrt(mv2.times(msm).times(mv2.transpose()).get(0, 0)));
	
		return score;
	}
	
	public double sim(ISynset a, ISynset b) {
		return Math.log((double)(distance(a, b))/(depth(a)+depth(b))) / Math.log(1/(2*(MAX_DEPTH+1)));
	}
	
	public int depth(ISynset s) {
		return JWITools.getMaxDepth(dict, s);
	}
	
	public int distance(ISynset a, ISynset b) {	
		ISynset ccp = JWITools.findCCP(dict, a, b);
		return depth(a) + depth(b) - 2*depth(ccp);
	}
	
	public String getPlainText(String str) {
	
		// removes most of the infobox
		str = Pattern.compile("^\\|.*", Pattern.MULTILINE).matcher(str).replaceAll("");
		
		// remove html comments
		str = Pattern.compile("<!--.*?-->", Pattern.DOTALL).matcher(str).replaceAll("");
		
		// html patterns
		str = str.replaceAll("&gt;", ">");
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&nbsp;", " ");
		str = str.replaceAll("&[mn]dash;", "-");
		str = str.replaceAll("<ref[^>]*?>[^<]*?</ref>", "");
		str = str.replaceAll("</?.*?>", "");
		
		// wiki markup patterns
		str = str.replaceAll("\\{\\{.*?\\}\\}", "");
		str = str.replaceAll("\\[\\[([^:\\|]*?)\\]\\]", "$1");
		str = str.replaceAll("\\[\\[[^\\]]*?:.*?\\]\\]", "");
		str = str.replaceAll("\\[\\[[^\\|\\]]*?\\|([^\\]]*?)\\]\\]", "$1");
		str = str.replaceAll("=*.*?=*", "");
		str = str.replaceAll("\\[.*?\\]", "");
		str = str.replaceAll("\\'{2,}", "");
		str = str.replaceAll("\\(.*?\\)", "");
		
		// finish infobox
		str = Pattern.compile("\\{\\{.*?\\}\\}", Pattern.DOTALL).matcher(str).replaceAll("");
		
		// clean up stray and unmatched markup characters
		str = str.replaceAll("[\\{\\}\\[\\]\\|]", "");
		
		return str;
	}
	
	
	public boolean isInteger(String s) {
		try { Integer.valueOf(s); return true;}
		catch (NumberFormatException e) {return false;}
	}
	
	public String firstSentence(String text) {
		// bypass "redirects here" sentence near start of article
		int begin = text.indexOf(" redirects here.");
		if (begin < 0) begin = 0;
		else begin = begin + " redirects here.".length();
		
		StringBuilder word = new StringBuilder();
		StringBuilder sentence = new StringBuilder();
		boolean started = false;
		for (int i=begin; i<text.length(); ++i) {
			char c = text.charAt(i);
			if (c=='\n')
				if (started) break;
				else continue;
				
			sentence.append(c);
			if ((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9')) word.append(c);
			else if (c=='.' && (word.length()!=1 || isInteger(word.toString())) && !acronyms.contains(word.toString())) break;
			else word = new StringBuilder();
			
			if (word.length()>0) started = true;
		}
		
		return sentence.toString();
	}
	
	public String firstParagraph(String text) {
		// bypass "redirects here" sentence near start of article
		int begin = text.indexOf(" redirects here.");
		if (begin < 0) begin = 0;
		else begin = begin + " redirects here.".length();
		
		StringBuilder para = new StringBuilder();
		boolean started = false;
		for (int i=begin; i<text.length(); ++i) {
			char c = text.charAt(i);
			if (c=='\n')
				if (started) break;
				else continue;
				
			para.append(c);
			if ((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9')) started = true;
		}
		
		return para.toString();
	}
}
