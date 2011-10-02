import edu.jhu.nlp.wikipedia.*;

public class HybridMethod extends WikipediaExtender {

	private NovelMethod novelMethod;
	private JiangMethod jiangMethod;

	public static void main(String[] args) throws Exception {
		new HybridMethod(args[0], Integer.valueOf(args[1])>0).run();
	}
	
	public HybridMethod(String wikiPath, boolean ne) {
		super(wikiPath, ne);
		
		novelMethod = new NovelMethod(null, ne, dict(), parser(), gsFactory());
		jiangMethod = new JiangMethod(null, ne, dict(), parser(), gsFactory());
	}
	
	public ResultPair determineHypernym(WikiPage page) {
		ResultPair novel = novelMethod.determineHypernym(page);
		ResultPair jiang = jiangMethod.determineHypernym(page);
		
		if (novel == null || novel.synset() == null) return jiang;
		else if (jiang == null) return novel;
		else {
			String compText = firstParagraph(page.getText());
			double nScore = match(novel.synset().getGloss(), compText);
			double jScore = match(jiang.synset().getGloss(), compText);
			
			if (jScore > nScore) return jiang;
			else return novel;
		}
	}
}
