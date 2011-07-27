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

public class InfoboxBrowser extends WikipediaExtender {
	
	public static void main(String[] args) throws Exception {
		new InfoboxBrowser("/usr/local/WordNet-3.0/dict", args[0]).run();
	}
	
	public InfoboxBrowser(String wordnetPath, String wikiPath) {
		super(wordnetPath, wikiPath);
	}
	
	public void handlePage(WikiPage p) {
		InfoBox info = p.getInfoBox();
		System.out.println("\n" + p.getTitle());
		if (info==null) System.out.println("No infobox.");
		else System.out.println(info.dumpRaw());
		
		new Scanner(System.in).nextLine();
	}
}
