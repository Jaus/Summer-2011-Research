
import edu.mit.jwi.item.ISynset;

public class ResultPair {
	private ISynset synset;
	private String word;
	
	public ISynset synset() {return synset;}
	public String word() {return word;}
	
	public ResultPair(ISynset s, String w) {
		synset = s;
		word = w;
	}
}
