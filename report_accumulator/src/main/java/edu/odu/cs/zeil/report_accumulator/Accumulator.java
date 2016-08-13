/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Accumulate development statistics for trend plotting.
 *  
 * Scans a set of report directories to locate common testing and analysis
 * reports, accumulating statistics where appropriate and adding those point
 * statistics for the current build to to csv files maintained on a project
 * web site.
 * 
 * @author zeil
 *
 */
public class Accumulator {

    /**
     * An ID string for the current build.
     */
    private String buildID;

    /**
     * URL of the project website, possibly already containing CSV files
     * holding statistics from prior builds.
     */
    private URL projectWebsite;

    /**
     * Directories in which to search for project reports.
     */
    private Path[] reportDirs;

    /**
     * Scanners customized for each known type of report.
     */
    private ArrayList<ReportScanner> scanners;

    /**
     * Create an accumulator for a given web site and local report directory.
     *  
     * @param aBuildID identifying string for this build.
     * @param aProjectWebsite URL for the project web site corresponding to the
     *           report directory
     * @param reportDirectories a set of directories that may contain newly
     *           generated reports. Each report must reside in a sub-directory
     *           within one of these directories.
     */
    public Accumulator (
            final String aBuildID,
            final URL aProjectWebsite,
            final Path[] reportDirectories) {
        this.buildID = aBuildID;
        this.projectWebsite = aProjectWebsite;
        this.reportDirs = reportDirectories;
        scanners = new ArrayList<>();
    }



    /**
     * Add a known type of report to the scanning process.
     * 
     * @param reportKind a scanner for a specific type of report.
     */
    public final void register (final ReportScanner reportKind) {
        scanners.add(reportKind);
    }


    /**
     * Scan the report directories for sub-directories containing common reports
     * If reportDir/subDir holds a report for which statistics are appropriate,
     * try to obtain a file projectWebSiteURL/reportDir/subDir.csv containing
     * prior statistics. If none exists, create a new such file. Add current
     * statistics to the end of this CSV structure and save in
     * reportDir/subDir.csv
     * 
     * The current reports and updated statistics can then be uploaded to the
     * web site, though doing so is outside the scope of this class.  It is
     * assumed that such uploading is handled as part of the normal project
     * build. 
     */
    public final void accumulateStatistics () {
        for (Path reportDir: reportDirs) {
            for (File dir: reportDir.toFile().listFiles()) {
                if (dir.isDirectory()) {
                    for (ReportScanner scanner: scanners) {
                        scanForStatistics (dir, scanner);
                    }
                }
            }
        }
    }


    /**
     * Attempt to apply a single report scanner to a directory.
     * @param dir  possible report directory
     * @param scanner a scanner for a specific type of report
     */
    private void scanForStatistics(
            final File dir, 
            final ReportScanner scanner) {
        scanner.setDirectory(dir.toPath());
        if (scanner.containsReport()) {
            double[] pointStatistics = scanner.extractStatistics();
            if (pointStatistics != null && pointStatistics.length > 0) {
                String reportName = dir.getName() + ".csv";
                String existingContent;
                int lineCount = 0;
                try {
                    URL existingReportURL = new URL(projectWebsite.toString() 
                            + "/" + reportName);
                    try (BufferedReader in = 
                            new BufferedReader(
                                    new InputStreamReader(
                                            existingReportURL.openStream()))) {
                        StringBuffer buf = new StringBuffer();
                        String line;
                        while ((line = in.readLine()) != null) {
                            buf.append(line);
                            buf.append("\n");
                            ++lineCount;
                        }
                        existingContent = buf.toString();
                    } catch (IOException e) {
                        existingContent = "";
                    }
                } catch (MalformedURLException e) {
                    existingContent = "";
                }
    /**
     * An ID string for the current build.
     */
    private String buildID;

    /**
     * URL of the project website, possibly already containing CSV files
     * holding statistics from prior builds.
     */
    private URL projectWebsite;

    /**
     * Directories in which to search for project reports.
     */
    private Path reportDir;

    /**
     * Scanners customized for each known type of report.
     */
    private ArrayList<ReportScanner> scanners;

    /**
     * Create an accumulator for a given web site and local report directory.
     *  
     * @param aBuildID identifying string for this build.
     * @param aProjectWebsite URL for the project web site corresponding to the
     *           report directory
     * @param reportDirectory A directory containing newly generated reports,
     *           each report in a separate subdirectory.
     */
    public Accumulator (
            final String aBuildID,
            final URL aProjectWebsite,
            final Path reportDirectory) {
        this.buildID = aBuildID;
        this.projectWebsite = aProjectWebsite;
        this.reportDir = reportDirectory;
        scanners = new ArrayList<>();
    }



    /**
     * Add a known type of report to the scanning process.
     * 
     * @param reportKind a scanner for a specific type of report.
     */
    public final void register (final ReportScanner reportKind) {
        scanners.add(reportKind);
    }


    /**
     * Scan the report directory for sub-directories containing common reports
     * If reportDir/subDir holds a report for which statistics are appropriate,
     * try to obtain a file projectWebSiteURL/reportDir/subDir.csv containing
     * prior statistics. If none exists, create a new such file. Add current
     * statistics to the end of this CSV structure and save in
     * reportDir/subDir.csv
     * 
     * The current reports and updated statistics can then be uploaded to the
     * web site, though doing so is outside the scope of this class.  It is
     * assumed that such uploading is handled as part of the normal project
     * build. 
     */
    public final void accumulateStatistics () {
    	for (File dir: reportDir.toFile().listFiles()) {
    		if (dir.isDirectory()) {
    			for (ReportScanner scanner: scanners) {
    				scanForStatistics (dir, scanner);
    			}
    		}
    	}
    }


    /**
     * Attempt to apply a single report scanner to a directory.
     * @param dir  possible report directory
     * @param scanner a scanner for a specific type of report
     */
    private void scanForStatistics(
            final File dir, 
            final ReportScanner scanner) {
        scanner.setDirectory(dir.toPath());
        if (scanner.containsReport()) {
            double[] pointStatistics = scanner.extractStatistics();
            if (pointStatistics != null && pointStatistics.length > 0) {
                String reportName = dir.getName() + ".csv";
                String existingContent;
                int lineCount = 0;
                try {
                    URL existingReportURL = new URL(projectWebsite.toString() 
                            + "/" + reportName);
                    try (BufferedReader in = 
                            new BufferedReader(
                                    new InputStreamReader(
                                            existingReportURL.openStream()))) {
                        StringBuffer buf = new StringBuffer();
                        String line;
                        while ((line = in.readLine()) != null) {
                            buf.append(line);
                            buf.append("\n");
                            ++lineCount;
                        }
                        existingContent = buf.toString();
                    } catch (IOException e) {
                        existingContent = "";
                    }
                } catch (MalformedURLException e) {
                    existingContent = "";
                }

                if (buildID == null || buildID.trim().length() == 0) {
                    buildID = Integer.toString(lineCount + 1);
                } else {
                    final int maxBuildLabelWidth = 16;
                    if (buildID.length() > maxBuildLabelWidth) {
                        buildID = buildID.substring(0,  maxBuildLabelWidth);
                    }
                }

                try (BufferedWriter out = 
                        new BufferedWriter(
                                new FileWriter(
                                        new File(dir.getParentFile(), 
                                                reportName)))) {
                    if (existingContent.length() > 0) {
                        out.write(existingContent);
                    } else {
                        out.write(dir.getName());
                        for (String s: scanner.getDescriptors()) {
                            out.write(",");
                            out.write(s);
                        }
                        out.newLine();
                    }
                    out.write(buildID);
                    for (double stat: pointStatistics) {
                        out.write(",");
                        out.write(new Double(stat).toString());
                    }
                    out.newLine();
                } catch (IOException e) {
                    System.err.println("Problem writing out statistics to " 
                                       + reportName + ": " + e);
                    e.printStackTrace();
                }
            }

        }
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
    public Path getReportDirectory() {
        return reportDir;
    }


}
