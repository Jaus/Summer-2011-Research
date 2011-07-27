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

	long seen = 0;

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

		TreeGraphNode backup = node;
		pre = getPOS(node, "PP");
		while (pre != null && getAttribute(pre, "TextAnnotation").equals("of")) {
		
			// the sentence is written as "[title] is a [order, class, series, etc] of [real hypernym]..."
			// so we need to go deeper
		
			node = pre;
			pre = getPOS(node, "NP");
			if (pre == null) {
				// bail to the original node in this situation
				node = backup;
				break;
			} else node = pre;
			
			pre = getPOS(node, "PP");
		}
		return node;
	}
	
	public String parseTreeHypernym(String sentence) {
		Tree tree = parser().apply(sentence);
		GrammaticalStructure gs = gsFactory().newGrammaticalStructure(tree);
		TreeGraphNode node = getHypernymNode(gs.root());
		
		if (node == null) {
			return null;
		}
		
		int wordIndex = 0;
		int place = 1;
		String word = getAttribute(node, "HeadWordAnnotation");
		System.err.println(word);
		while (word.length()>0 && word.charAt(word.length()-1) >= '0' && word.charAt(word.length()-1) <= '9') {
			wordIndex = wordIndex + (word.charAt(word.length()-1) - '0')*place;
			word = word.substring(0, word.length()-1);
			place *= 10;
		}
		if (word.charAt(word.length()-1) == '-') word = word.substring(0, word.length()-1);
		--wordIndex;
		
		// normalize word
		word = Morphology.lemmatizeStatic(new WordTag(word)).lemma();
		
		ArrayList wordList = tree.yieldWords();
		StringBuilder candidate = new StringBuilder(word);
		String hyp = candidate.toString();
		
		IIndexWord index = null;
		if (word.length() > 0) index = dict().getIndexWord(candidate.toString(), POS.NOUN);
		
		// find the longest phrase ending in the targeted hypernym that appears in WordNet
		while (index != null && wordIndex > 0) {
			hyp = candidate.toString();
			
			candidate.insert(0, " ");
			candidate.insert(0, ((CoreLabel)(wordList.get(--wordIndex))).word());
			index = dict().getIndexWord(candidate.toString(), POS.NOUN);
		}
		
		return hyp;
	}
	
	public void handlePage(WikiPage page) {
		++seen;
		
		if (page.isDisambiguationPage() || page.isRedirect()) return;
		
		String title = page.getTitle();
		title = title.substring(0, title.indexOf('\n'));
		System.err.println(title);
		if (dict().getIndexWord(title, POS.NOUN) != null) {
			System.err.println("Skipping because it is already in WordNet.");
			return;
		}
		++added;
		
		System.out.println("\n" + title);
		String text = getPlainText(page.getWikiText());
		String word = parseTreeHypernym(firstSentence(text));
		
		if (word == null) {
			System.err.println("No hypernym found.");
			return;
		}
		
		ResultPair result = bestMatch(Arrays.asList(word), firstParagraph(page.getText()));
			
		System.out.print("-> ");
		if (result != null) for (IWord iword : result.synset().getWords()) System.out.print(iword.getLemma() + ", ");
		else System.out.println("" + word + "(not found in WordNet)");
		
		System.out.println('\n');
		
		if (added%50==0) System.err.println(added);
		if (seen%100==0) System.err.println(seen);
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
