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
public class JiangMethod extends WikipediaExtender {

	public static void main(String[] args) throws Exception {
		new JiangMethod("/usr/local/WordNet-3.0/dict", args[0]).run();
	}
	
	public JiangMethod(String wordnetPath, String wikiPath) {
		super(wordnetPath, wikiPath);
	}
	
	public void handlePage(WikiPage page) {
		if (page.getCategories().size()==0) {
			System.err.println("Skipping " + page.getTitle() + " because it has no categories.");
			return;
		}
	
		Map<String, String> headTerms = new HashMap<String, String>();
		boolean phase2 = false;
		
		String title = page.getTitle();
		if (dict().getIndexWord(title, POS.NOUN) != null) {
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
				Tree np = parser().apply(category+" are cool.");
				//System.out.println(category+" are cool.");
				
				GrammaticalStructure gs = gsFactory().newGrammaticalStructure(np);
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
			IIndexWord index = dict().getIndexWord(word, POS.NOUN);

			// find all the synsets associated with that index and score them
			System.err.println("" + index.getWordIDs().size() + " synsets");
			for (IWordID wordId : index.getWordIDs()) {
				ISynset concept = dict().getSynset(wordId.getSynsetID());
				double score = match(firstParagraph(page.getText()), concept.getGloss());
				System.err.println("Score: " + score);
				if (score > bestScore) {
					best = concept;
					bestScore = score;
					bestCandidate = word;
				}
			}
		}
		
		if (best == null) {
			System.err.println("Skipping " + page.getTitle() + " because there's no text in the body.");
			return;
		}
		// ** report results ** //
		System.out.print(page.getTitle() + "-> ");
		if (phase2) System.out.print(headTerms.get(bestCandidate) + " -> ");
		
		for (IWord word : best.getWords()) System.out.print(word.getLemma() + ", ");
		System.out.println("\n");
	}
}
