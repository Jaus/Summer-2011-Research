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

import edu.jhu.nlp.wikipedia.*;

import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;

public class JiangMethod extends WikipediaExtender {

	public static void main(String[] args) throws Exception {
		new JiangMethod(args[0], Integer.valueOf(args[1])>0).run();
	}
	
	public JiangMethod(String wikiPath, boolean ne) {
		super(wikiPath, ne);
	}
	
	public JiangMethod(String wikipediapath, boolean ne, edu.mit.jwi.Dictionary d, LexicalizedParser p, GrammaticalStructureFactory g) {
		super(wikipediapath, ne, d, p, g);
	}
	
	public ResultPair determineHypernym(WikiPage page) {
	
		Map<String, String> headTerms = new HashMap<String, String>();
		boolean phase2 = false;
		
		ArrayList<String> candidates = new ArrayList<String>();
		
		// ** phase 1 ** //
		for (String category : page.getCategories()) {
			try {
				if (dict().getIndexWord(category, POS.NOUN) != null) candidates.add(category);
			} catch (IllegalArgumentException e) {e.printStackTrace();}
		}
		
		// ** phase 2 ** //
		if (candidates.size() == 0) {
			phase2 = true;

			for (String category : page.getCategories()) {
				Tree np = parser().apply(category+" are cool.");
				
				GrammaticalStructure gs = gsFactory().newGrammaticalStructure(np);
				Collection<TypedDependency> tds = gs.typedDependenciesCollapsed();
				Iterator<TypedDependency> iter = tds.iterator();
				
				while (iter.hasNext()) {
					TypedDependency t = iter.next();
					String head = Morphology.lemmatizeStatic(new WordTag(t.dep().value())).lemma();
					if (t.gov().value().equals("cool") && !head.equals("are") && dict().getIndexWord(head, POS.NOUN) != null) {
						headTerms.put(head, category);
						candidates.add(head);
					}
				}
			}
		}
		
		if (candidates.size() == 0) {
			System.err.println("Skipping " + page.getTitle() + " because no legal candidates were found.");
			return null;
		}
		
		// ** determine best concept match ** //
		String compText = firstParagraph(page.getText());
		ResultPair result = bestMatch(candidates, compText);
		
		if (result == null) {
			System.err.println("Skipping " + page.getTitle() + " because there's no text in the body.");
			return null;
		}
		
		if (phase2) return new ResultPair(result.synset(), headTerms.get(result.word()));
		else return new ResultPair(result.synset(), null);
	}
}
