import java.util.*;
import java.io.*;

public class RandomSelection {

	private int max;
	private int[] lines = new int[300];
	private BufferedReader input;
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: RandomSelection <filename>");
			return;
		}
		else new RandomSelection(args[0]).run();
	}
	
	public void run() throws IOException {
    	int n = 0;
    	for (int i=0; i<lines.length; ++i) {
    		while (n < lines[i]) {
    			String line = input.readLine();
    			++n;
    		}
    		System.out.println(input.readLine());
    		++n;	
    	}
	}
	
	public RandomSelection(String filename) throws IOException, FileNotFoundException {
		max = numLines(filename);
		
		Random r = new Random();
		for (int i=0; i<lines.length; ++i) {
			do {lines[i] = r.nextInt(max);} while (lines[i]%2==1);
		}
		Arrays.sort(lines);
		
		for (int i=1; i<lines.length; ++i)
			if (lines[i] == lines[i-1]) ++lines[i];
		
		input = new BufferedReader(new FileReader(filename));
	}
	
	private int numLines(String filename) throws IOException {
    	InputStream is = new BufferedInputStream(new FileInputStream(filename));
    	try {
    	    byte[] c = new byte[1024];
    	    int count = 0;
    	    int readChars = 0;
    	    while ((readChars = is.read(c)) != -1) {
    	        for (int i = 0; i < readChars; ++i) {
    	            if (c[i] == '\n')
    	                ++count;
    	        }
    	    }
    	    return count;
    	} finally {
    	    is.close();
    	}
    }
}
