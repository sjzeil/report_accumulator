package edu.odu.cs.zeil.report_accumulator;

import java.nio.file.Path;

/**
 * Process a specific kind of report.
 * 
 * @author zeil
 *
 */
public interface ReportScanner {
	
	/**
	 * Set the directory that will be examined for a possible
	 * set of report statistics.
	 * 
	 * @param possibleReportDirectory directory to examine
	 */
	public void setDirectory (Path possibleReportDirectory);
	
	/**
	 * Check to see if the previously set directory contains a recognizable report.
	 * 
	 * @return true if this is recognized as a report.
	 */
	public boolean containsReport();

	/**
	 * Extract statistics from a report.
	 * @return an array of (typically 1 or 2) numbers representing the point
	 *    statistics for this report, or an array of 0 numbers 
	 *    if !containsReport() 
	 */
	public double[] extractStatistics();
	
	/**
	 * Describe the statistics derived by this report.
	 * @return An array of strings of length extractStatistics().length
	 *     that describes the contents of the extracted statistics.
	 */
	public String[] getDescriptors();
	
	
}
