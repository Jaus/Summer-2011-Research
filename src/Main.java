import java.net.*;
import java.io.*;
import java.util.*;

import edu.mit.jwi.*;
import edu.mit.jwi.item.*;
/*
import de.tudarmstadt.ukp.wiktionary.api.*;
import com.sleepycat.je.*;

import edu.jhu.nlp.wikipedia.*;*/

public class Main {
	public static void main(String[] args) throws Exception {
		switch (Integer.valueOf(args[0])) {
		case 1:
			new Main().run(args[1]); break;
		/*case 2:
			new Main().run2(args[1]); break;
		case 3:
			new Main().run3(args[1], args[2]); break;
		case 4:
			new Main().run4(args[1], args[2]); break;
		case 5:
			new Main().run5(args[1]); break;*/
		}
	}
	
	public void run(String iword) {
		// construct the URL to the Wordnet dictionary directory
		String path = "/usr/local/WordNet-3.0/dict";
		URL url = null;
		try{ url = new URL("file", null, path); } 
		catch(MalformedURLException e){ e.printStackTrace(); }
		if(url == null) return;

		// construct the dictionary object and open it
		IDictionary dict = new edu.mit.jwi.Dictionary(url);
		try {dict.open();}
		catch (IOException e) {e.printStackTrace();}

		// look up each sense of the word "dog"
		IIndexWord idxWord = dict.getIndexWord(iword, POS.NOUN);
		for (IWordID wordID : idxWord.getWordIDs()) {
			IWord word = dict.getWord(wordID);
			System.out.println();
			System.out.println("Id = " + wordID);
			System.out.println("Lemma = " + word.getLemma());
			System.out.println("Gloss = " + word.getSynset().getGloss());
			System.out.println("#Hypernyms = " + word.getSynset().getRelatedSynsets(Pointer.HYPERNYM).size());
		}
	}
	/*
	public void run2(String path) throws FileNotFoundException, IOException {
		File f = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Scanner in = new Scanner(System.in);
		
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.print(line);
			in.nextLine();
		}
	}
	
	public void run3(String inpath, String outpath) {
		Wiktionary.parseWiktionaryDump(inpath, outpath, Locale.ENGLISH.getLanguage(), true);
	}
	
	public void run4(String wikpath, String word) {
		Wiktionary wkt = new Wiktionary(wikpath);
		List<WordEntry> entries = wkt.getWordEntries(word, Language.ENGLISH);
		System.out.println(Wiktionary.getEntryInformation(entries));
		wkt.close();
	}
	
	public long pages;
	public int subpages;
	
	public void run5(String path) {
		WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(path);
		try {

			wxsp.setPageCallback(new PageCallbackHandler() { 
				public void process(WikiPage page) {
					if ((++subpages)%1000==0) {
						subpages = 0;
						System.out.println(""+(++pages)+"000 pages read");
					}
				}
			});
			wxsp.parse();
			
		} catch(Exception e) {
		
			e.printStackTrace();
		}
	}*/
}
