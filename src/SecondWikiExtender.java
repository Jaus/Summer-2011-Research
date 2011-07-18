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
public class NovelMethod extends WikipediaExtender {

	public static void main(String[] args) throws Exception {
		new NovelMethod("/usr/local/WordNet-3.0/dict", args[0]).run();
	}
	
	public NovelMethod(String wordnetPath, String wikiPath) {
		super(wordnetPath, wikiPath);
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
	
	public void handlePage(WikiPage page) {
		
		if (page.isDisambiguationPage() || page.isRedirect()) return;
		
		String title = page.getTitle();
		if (dict.getIndexWord(title, POS.NOUN) != null) {
			//System.err.println("Skipping " + page.getTitle() + " because it is already in WordNet.");
			return;
		}
		++added;
		
		String text = getPlainText(page.getWikiText());
		//Iterator<List<HasWord>> sentences = new DocumentPreprocessor(new StringReader(page.getText())).iterator();
		//sentences.next();
		//List<HasWord> sentence = sentences.next();
		System.out.print("\n*" + title);
		
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
		String sent = sentence.toString();
		System.out.println(sent);
		
		///*
		Tree np = parser.apply(sent);
		
		GrammaticalStructure gs = gsf.newGrammaticalStructure(np);
		System.out.println(gs);
		/*
		Collection<TypedDependency> tds = gs.typedDependenciesCollapsed();
		Iterator<TypedDependency> iter = tds.iterator();
		
		while (iter.hasNext()) {
			TypedDependency t = iter.next();
			System.out.println(t);
			//String head = Morphology.lemmatizeStatic(new WordTag(t.dep().value())).lemma();
		}
		//*/
		
		System.out.println();
		if (added%50==0) System.err.println(added);
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
}
