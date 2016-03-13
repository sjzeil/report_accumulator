/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Launch the accumulator with known report types.
 * 
 * 
 * @author zeil
 *
 */
public class Main {

	/**
	 * Launch the accumulator with known report types.
	 * 
	 * @param args command line arguments
	 *     websiteURL reportDir1 reportDir2 ...
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		URL website = new URL(args[0]);
		ArrayList<Path> dirs = new ArrayList<>();
		for (int i = 1; i < args.length; ++i) {
			dirs.add (Paths.get(args[i]));
		}
		Accumulator accum = new Accumulator (website, dirs.toArray(new Path[0]));
		
		// accum.register(...);
		accum.register(new JUnitScanner());
		accum.register(new CheckstyleScanner());
		accum.register(new FindBugsScanner());
		
		accum.accumulateStatistics();
	}

}
