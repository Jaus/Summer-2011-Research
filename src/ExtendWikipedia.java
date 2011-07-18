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
public class ExtendWikipedia {

	final String GRAMMAR_FILE = "/home/jaus/Documents/Java/stanford-parser-2011-06-19/grammar/englishFactored.ser.gz";

	edu.mit.jwi.Dictionary dict;
	LexicalizedParser parser;
	TreebankLanguagePack tlp;
	GrammaticalStructureFactory gsf;
	
	POS[] posList;

	long added;
	int MAX_DEPTH = 16;

	public static void main(String[] args) throws Exception {
		new ExtendWikipedia().run("/usr/local/WordNet-3.0/dict", args[0]);
	}
	
	public void run(String wordnetpath, String wikipediapath) {
	
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
		
		// loop through Wikipedia
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(wikipediapath);
		try {

			wxsp.setPageCallback(new PageCallbackHandler() { 
				public void process(WikiPage page) {
					handlePage(page);
				}
			});
			wxsp.parse();
			
		} catch(Exception e) {
		
			e.printStackTrace();
		}
	}
	
	public void handlePage(WikiPage page) {
		if (page.getCategories().size()==0) {
			System.err.println("Skipping " + page.getTitle() + " because it has no categories.");
			return;
		}
	
		Map<String, String> headTerms = new HashMap<String, String>();
		boolean phase2 = false;
		
		String title = page.getTitle();
		if (dict.getIndexWord(title, POS.NOUN) != null) {
			System.err.println("Skipping " + page.getTitle() + " because it is already in WordNet.");
			return;
		}
		++added;
		
		ArrayList<String> candidates = new ArrayList<String>();
		
		// ** phase 1 ** //
		for (String category : page.getCategories()) {
			try {
				if (dict.getIndexWord(category, POS.NOUN) != null) candidates.add(category);
			} catch (IllegalArgumentException e) {e.printStackTrace();}
		}
		
		// ** phase 2 ** //
		if (candidates.size() == 0) {
			phase2 = true;

			for (String category : page.getCategories()) {
				Tree np = parser.apply(category+" are cool.");
				//System.out.println(category+" are cool.");
				
				GrammaticalStructure gs = gsf.newGrammaticalStructure(np);
				Collection<TypedDependency> tds = gs.typedDependenciesCollapsed();
				Iterator<TypedDependency> iter = tds.iterator();
				//System.out.println(tds);
				
				while (iter.hasNext()) {
					TypedDependency t = iter.next();
					String head = Morphology.lemmatizeStatic(new WordTag(t.dep().value())).lemma();
					if (t.gov().value().equals("cool") && !head.equals("are") && dict.getIndexWord(head, POS.NOUN) != null) {
						headTerms.put(head, category);
						candidates.add(head);
						//System.out.println(t.reln().getShortName() + "(" + t.gov().value() + ", " + head + ")");
						//break;
					}
				}
				//System.out.println();
			}
		}
		
		//System.out.println(page.getCategories());
		if (candidates.size() == 0) {
			System.err.println("Skipping " + page.getTitle() + " because no legal candidates were found.");
			return;
		}
		// ** determine best concept match ** //
		ISynset best = null;
		double bestScore = -1.0;
		String bestCandidate = "";
		System.err.println("" + candidates.size() + " candidates");
		for (String word : candidates) {
			// take a word and find its index word in WordNet
			IIndexWord index = dict.getIndexWord(word, POS.NOUN);

			// find all the synsets associated with that index and score them
			System.err.println("" + index.getWordIDs().size() + " synsets");
			for (IWordID wordId : index.getWordIDs()) {
				ISynset concept = dict.getSynset(wordId.getSynsetID());
				double score = match(page.getText(), concept.getGloss());
				System.err.println("Score: " + score);
				if (score > bestScore) {
					best = concept;
					bestScore = score;
					bestCandidate = word;
				}
			}
		}
		
		// ** report results ** //
		System.out.print(page.getTitle() + "-> ");
		if (phase2) System.out.print(headTerms.get(bestCandidate) + " -> ");
		
		for (IWord word : best.getWords()) System.out.print(word.getLemma() + ", ");
		System.out.println("\n");
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
	/*
		int d = 0;
		List<ISynsetID> hypernyms;
		
		while (!s.getWord(1).getLemma().equals("entity")) {
			hypernyms = s.getRelatedSynsets(Pointer.HYPERNYM);
			if (hypernyms.size() == 0) break;
			
			s = dict.getSynset(hypernyms.get(0));
			++d;
		}
			
		return d;//*/
		
		return JWITools.getMaxDepth(dict, s);
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
	
	public int distance(ISynset a, ISynset b) {
		
		ISynset ccp = JWITools.findCCP(dict, a, b);
		
		/*System.err.println(depth(a));
		System.err.println(depth(b));
		System.err.println(depth(ccp));
		System.err.println();//*/
		return depth(a) + depth(b) - 2*depth(ccp);
		
		/*
		List<ISynsetID> hypernyms;
		
		// build the chain of a's hypernyms
		ArrayList<ISynset> aList = new ArrayList<ISynset>();
		aList.add(a);
			
		while (!a.getWord(1).getLemma().equals("entity")) {
			hypernyms = a.getRelatedSynsets(Pointer.HYPERNYM);
			if (hypernyms.size() == 0) break;
			
			a = dict.getSynset(hypernyms.get(0));
			aList.add(a);
		}
		
		// build the chain of b's hypernyms
		ArrayList<ISynset> bList = new ArrayList<ISynset>();
		bList.add(b);
		
		while (!b.getWord(1).getLemma().equals("entity")) {
			hypernyms = b.getRelatedSynsets(Pointer.HYPERNYM);
			if (hypernyms.size() == 0) break;
			
			b = dict.getSynset(hypernyms.get(0));
			bList.add(b);
		}
		
		//traverse each list backward, noting where the terms first differ
		int offset = 0;
		int min = Math.min(aList.size(), bList.size());
		
		while (offset+1 < min && aList.get(aList.size()-offset-1).equals(bList.get(bList.size()-offset-1)))
			++offset;
			
		return (aList.size()-offset) + (bList.size()-offset);//*/
	}
}
