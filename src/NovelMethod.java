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

public class NovelMethod extends WikipediaExtender {

	public static void main(String[] args) throws Exception {
		new NovelMethod("/usr/local/WordNet-3.0/dict", args[0]).run();
	}
	
	public NovelMethod(String wordnetPath, String wikiPath) {
		super(wordnetPath, wikiPath);
	}
	
	private TreeGraphNode getPOS(TreeGraphNode parent, String pos) {
		TreeGraphNode[] children = parent.children();
		for (int i=0; i<children.length; ++i)
			if (children[i].value().equals(pos)) {
				return children[i];
			}
			
		return null;
	}
	
	private String getAttribute(TreeGraphNode node, String attr) {
		String map = node.label().toString("{map}");
		String key = attr + "=";
		
		int start = map.indexOf(key) + key.length();
		int end = map.indexOf(',', start);
		if (end == -1) end = map.length()-1;
		
		return map.substring(start, end);
	}
	
	private TreeGraphNode getHypernymNode(TreeGraphNode node) {
		TreeGraphNode pre = node;
		
		// the main path is S (-> S) -> VP -> NP
		
		// go through the root (or quit if this was a bad parse)
		pre = getPOS(node, "S");
		if (pre == null) {
			System.err.println("Skipping because the article does not begin with a sentence.");
			return null;
		} else node = pre;
		
		// sometimes there are two S's in a row at the top
		pre = getPOS(node, "S");
		if (pre != null) node = pre;
		
		// get the predicate
		pre = getPOS(node, "VP");
		if (pre == null) {
			System.err.println("Skipping because the sentence does not have a predicate.");
			return null;
		} else node = pre;
		
		// get the object phrase
		pre = getPOS(node, "NP");
		if (pre == null) {
			pre = getPOS(node, "VP"); // try S -> VP -> VP -> NP
			
			
			if (pre == null) {
				pre = getPOS(node, "S"); // try S -> VP -> S -> VP -> NP
				
				if (pre == null) {
					System.err.println("Skipping because the sentence's predicate does not have an object.");
					return null;
				} else node = pre;
				
				pre = getPOS(node, "VP");
				if (pre == null) {
					System.err.println("Skipping because the sentence does not have a deeper predicate.");
					return null;
				} else node = pre;
				
			} else node = pre;
			
			
			pre = getPOS(node, "NP");
			if (pre == null) {
				System.err.println("Skipping because the sentence's deeper predicate does not have an object.");
				return null;
			} else node = pre;
				
		} else node = pre;
		
		// now we have the NP
		// check if there is an 'of' preposition

		pre = getPOS(node, "PP");
		while (pre != null && getAttribute(pre, "TextAnnotation").equals("of")) {
		
			// the sentence is written as "[title] is a [order, class, series, etc] of [real hypernym]..."
			// so we need to go deeper
		
			node = pre;
			pre = getPOS(node, "NP");
			if (pre == null) {
				System.err.println("Skipping because the prepositional phrase has no object.");
				return null;
			} else node = pre;
			
			pre = getPOS(node, "PP");
		}
		return node;
	}
	
	public void handlePage(WikiPage page) {
		
		if (page.isDisambiguationPage() || page.isRedirect()) return;
		
		String title = page.getTitle();
		title = title.substring(0, title.indexOf('\n'));
		System.err.println(title);
		if (dict.getIndexWord(title, POS.NOUN) != null) {
			System.err.println("Skipping because it is already in WordNet.");
			return;
		}
		++added;
		
		String text = getPlainText(page.getWikiText());
		String sent = firstSentence(text);
		System.out.println("\n" + title);
		
		Tree tree = parser.apply(sent);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		TreeGraphNode node = getHypernymNode(gs.root());
		
		if (node == null) {
			System.err.println(sent);
			inspect(gs);
			return;
		}
		
		String word = getAttribute(node, "HeadWordAnnotation");
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
		
		System.out.println('\n');
		System.out.println(sent);
		inspect(gs);
		
		if (added%50==0) System.err.println(added);
	}
	
	public void inspect(GrammaticalStructure gs) {
		System.err.println(gs);
		TreeGraphNode node = gs.root();
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
