/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.net.URL;
import java.nio.file.Path;

/**
 * Accumulate development statistics for trend plotting.
 *  
 * Scans a set of report directories to locate common testing and analysis reports, 
 * accumulating statistics where appropriate and adding those point statistics for
 * the current build to to csv files maintained on a project website.
 * 
 * @author zeil
 *
 */
public class Accumulator {
	
	private URL projectWebsite;
	private Path[] reportDirectories;
	
	/**
	 * Create an accumulator for a given website and local report directory.
	 *  
	 * @param projectWebsite URL for the project website corresponding to the report directory
	 * @param reportDirectories a set of directories that may contain newly generated reports. Each
	 *     report must reside in a subdirectory within one of these directories.
	 */
	public Accumulator (
			URL projectWebsite,
			Path[] reportDirectories) {
		// TODO
	}

	
	
	/**
	 * Add a known type of report to the scanning process.
	 * 
	 * @param reportKind a scanner for a specific type of report.
	 */
	public void register (ReportScanner reportKind) {
		// TODO
	}
	
	
	/**
	 * Scan the report directories for subdirectories containing common reports
	 * If reportDir/subDir holds a report for which statistics are appropriate,
	 * try to obtain a file projectWebSiteURL/reportDir/subDir.csv contianing prior
	 * stats. If none exists, create a new such file. Add current statistics to the
	 * end of this CSV structure and save in reportDir/subDir.csv
	 * 
	 * The current reports and updated statistics can then be uploaded to the website,
	 * though doing so is outside the scope of this class.  It is assumed that such
	 * uploading is handled as part of the normal project build. 
	 */
	public void accumulateStatistics () {
		// TODO
	}


	/**
	 * 
	 * @return the project website URL.
	 */
	public URL getProjectWebsite() {
		return projectWebsite;
	}


	/**
	 * 
	 * @return the local report directories.
	 */
	public Path[] getReportDirectories() {
		return reportDirectories;
	}



}
