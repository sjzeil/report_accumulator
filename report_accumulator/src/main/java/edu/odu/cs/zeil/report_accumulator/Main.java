/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	 *     buildID websiteURL reportDir1 reportDir2 ...
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
	    String buildID = args[0];
		URL website = new URL(args[1]);
		Path reports = Paths.get(args[2]);
		Accumulator accum = new Accumulator (buildID, website, reports);
		
		// accum.register(...);
		accum.register(new JUnitScanner());
		accum.register(new CheckstyleScanner());
		accum.register(new FindBugsScanner());
		accum.register(new PMDScanner());
		accum.register(new JacocoBranchCoverageScanner());
		
		accum.accumulateStatistics();
	}

}
