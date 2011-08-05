import java.util.*;
import java.io.*;

public class ResultsEvaluator {

	private String prefix;
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: ResultsEvaluator <set name>");
			return;
		}
		else new ResultsEvaluator(args[0]).run();
	}
	
	public void run() throws IOException, FileNotFoundException {
    	
    	List<List<Integer>> lists = new ArrayList<List<Integer>>();
		int total = 0;
    	
    	for (int i=1; i<=2; ++i) {
    		lists.add(new ArrayList<Integer>());
    		BufferedReader input = new BufferedReader(new FileReader(new String(prefix + i + ".txt")));
    		
    		String line;
    		while ((line = input.readLine()) != null) {
    			int score = line.charAt(0) - '0';
    			lists.get(i-1).add(score);
    			if (i==1) ++total;
    		}
    		
    		input.close();
    	}
    	
		int[] counts = new int[5];
		
		for (int i=0; i<total; ++i) {
			double subtotal = 0;
			for (int j=0; j<lists.size(); ++j) subtotal += lists.get(j).get(i);
			subtotal /= lists.size();
			
			++counts[(int)Math.round(subtotal)-1];
		}
		
		double average = 0;
		for (int i=0; i<counts.length; ++i) {
			average += counts[i]*(i+1);
		}
		average /= total;
    	
    	System.out.println("Average score," + average);
    	System.out.println("Excellent,Good,Fair,Neutral,Bad");
		for (int i=counts.length-1; i>=0; --i) {
			System.out.print("" + counts[i] + ",");
		}
		System.out.println();
	}
	
	public ResultsEvaluator(String filename){
		prefix = filename;
	}
}
