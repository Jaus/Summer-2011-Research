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
	
	public void handlePage(WikiPage page) {
		
		if (page.isDisambiguationPage() || page.isRedirect()) return;
		
		String title = page.getTitle();
		if (dict.getIndexWord(title, POS.NOUN) != null) {
			System.err.println("Skipping " + page.getTitle() + " because it is already in WordNet.");
			return;
		}
		++added;
		
		String text = getPlainText(page.getWikiText());
		String sent = firstSentence(text);
		System.out.print("\n" + title);
		//System.out.println(sent);
		
		///*
		Tree np = parser.apply(sent);
		
		GrammaticalStructure gs = gsf.newGrammaticalStructure(np);
		System.out.println(gs);
		inspect(gs.root());
		return;/*
		boolean success = false;
		TreeGraphNode node = gs.root();
		TreeGraphNode[] children = node.children();
		for (int i=0; i<children.length && !success; ++i)
			if (children[i].value().equals("S")) {
				node = children[i];
				success = true;
			}
			
		if (!success) {
			System.err.println("Skipping " + page.getTitle() + " because it does not begin with a sentence.");
			return;
		}
		success = false;
		children = node.children();
		
		for (int i=0; i<children.length && !success; ++i)
			if (children[i].value().equals("VP")) {
				node = children[i];
				success = true;
			}
			
		if (!success) {
			System.err.println("Skipping " + page.getTitle() + " because it does not have a verb phrase.");
			return;
		}
		success = false;
		children = node.children();
		
		for (int i=0; i<children.length && !success; ++i)
			if (children[i].value().equals("NP")) {
				node = children[i];
				success = true;
			}
			
		if (!success) {
			System.err.println("Skipping " + page.getTitle() + " because it does not have an object of the verb phrase in its first sentence.");
			return;
		}
		
		String map = node.label().toString("{map}");
		String key = "HeadWordAnnotation=";
		int start = map.indexOf(key) + key.length();
		int end = map.indexOf(',', start);
		if (end == -1) end = map.length()-1;
					
		String word = map.substring(start, end);
		while (word.length()>0 && ((word.charAt(word.length()-1) >= '0' && word.charAt(word.length()-1) <= '9') ||
				word.charAt(word.length()-1) == '-'))
			word = word.substring(0, word.length()-1);
			
		ISynset best = null;
		double bestScore = -1.0;
		String bestCandidate = "";
		
		// take a word and find its index word in WordNet
		IIndexWord index = null;
		if (word.length() > 0) index = dict.getIndexWord(word, POS.NOUN);
		if (index != null) {

			// find all the synsets associated with that index and score them
			System.err.println("" + index.getWordIDs().size() + " synsets");
			for (IWordID wordId : index.getWordIDs()) {
				ISynset concept = dict.getSynset(wordId.getSynsetID());
				double score = match(firstParagraph(page.getText()), concept.getGloss());
				System.err.println("Score: " + score);
				if (score > bestScore) {
					best = concept;
					bestScore = score;
					bestCandidate = word;
				}
			}
		}
			
		System.out.print("-> ");
		if (best != null) for (IWord iword : best.getWords()) System.out.print(iword.getLemma() + ", ");
		else System.out.println("" + word + "(not found in WordNet)");
		
		System.out.println();
		if (added%50==0) System.err.println(added);*/
	}
	
	public void inspect(TreeGraphNode node) {
		Scanner in = new Scanner(System.in);
		boolean stop = false;
		
		while (!stop) {
			System.err.println("\n"+node);
			System.err.print("> ");
			char command = in.nextLine().charAt(0);
			switch (command) {
				case 'q': stop = true; break;
				case 'c':
					try {
						TreeGraphNode[] children = node.children();
						for (int i=0; i< children.length; ++i)
							System.err.println("" + i + ") " + children[i]);
					} catch (Exception e) {
						System.err.println("No children");
					}
					break;
				case 'r':
					node = (TreeGraphNode)(node.parent());
					break;
				case 'i':
					System.err.println(node.value()+"\n"+node.label());
					break;
				case 't':
					for (TaggedWord word : node.taggedYield())
						System.err.println(word);
					break;
				case 'h':
					String map = node.label().toString("{map}");
					String key = "HeadWordAnnotation=";
					int start = map.indexOf(key) + key.length();
					int end = map.indexOf(',', start);
					if (end == -1) end = map.length()-1;
					
					System.err.println(map.substring(start, end));
					break;
				default:
					node = node.children()[command-'0'];
			}
		}
	}
}
