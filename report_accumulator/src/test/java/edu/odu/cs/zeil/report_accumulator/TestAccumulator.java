/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zeil
 *
 */
public class TestAccumulator {
	
	
	private class ReportStub implements ReportScanner {
		
		private String name;
		private Path dir;
		
		public ReportStub(String name) {
			this.name = name;
		}
		
		/**
		 * Set the directory that will be examined for a possible
		 * set of report statistics.
		 * 
		 * @param possibleReportDirectory directory to examine
		 */
		public void setDirectory (Path possibleReportDirectory) {
			dir = possibleReportDirectory;
		}
		
		/**
		 * Check to see if the previously set directory contains a recognizable report.
		 * 
		 * @return true if this is recognized as a report.
		 */
		public boolean containsReport() {
			return dir.getFileName().toString().contains(name);
		}

		/**
		 * Extract statistics from a report.
		 * @return an array of (typically 1 or 2) numbers representing the point
		 *    statistics for this report, or an array of 0 numbers 
		 *    if !containsReport() 
		 */
		public double[] extractStatistics() {
			double[] d = {21.0, 32.0};
			return d;
		}
		
		/**
		 * Describe the statistics derived by this report.
		 * @return An array of strings of length extractStatistics().length
		 *     that describes the contents of the extracted statistics.
		 */
		public String[] getDescriptors()
		{
			String[] s = {name + ":A", name + ":B"};
			return s;
		}
		

	}
	
	
	String reportDir1 = "testing";
	String reportDir2 = "analysis";
	String reportDir3 = "foobar";
	
	Path testArea = Paths.get("build", "test-data");
	
	URL reportURL;
	
	ReportScanner report1;
	ReportScanner report2;
	
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		reportURL = testArea.toUri().toURL();
		report1 = new ReportStub(reportDir1);
		report2 = new ReportStub(reportDir2);
		testArea.toFile().mkdirs();
		Path csvDir1 = testArea.resolve(reportDir1);
		csvDir1.toFile().mkdir();
		Path csvDir2 = testArea.resolve(reportDir2);
		csvDir2.toFile().mkdir();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void cleanUp() throws Exception {
		
		Files.walkFileTree(testArea, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	                throws IOException
	        {
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }


	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
	        {
	            if (exc == null)
	            {
	                Files.delete(dir);
	                return FileVisitResult.CONTINUE;
	            }
	            else
	            {
	                throw exc;
	            }
	        }
		});
	}

	
	/**
	 * Test method for {@link edu.odu.cs.zeil.report_accumulator.Accumulator#accumulateStatistics(java.net.URL, java.nio.file.Path[])}.
	 * @throws IOException 
	 */
	@Test
	public void testNewStatistics() throws IOException {
		Path[] paths = {testArea};
		Accumulator acc = new Accumulator(reportURL, paths);
		acc.register(report1);
		acc.register(report2);
		
		acc.accumulateStatistics();
		Path csv1 = testArea.resolve(reportDir1 + ".csv");
		Path csv2 = testArea.resolve(reportDir2 + ".csv");
		Path csv3 = testArea.resolve(reportDir3 + ".csv");
		assertTrue (csv1.toFile().exists());
		assertTrue (csv2.toFile().exists());
		assertFalse (csv3.toFile().exists());
		
		String csvContent1 = readCSV(csv1);
		assertTrue (csvContent1.contains(reportDir1));
		assertTrue (csvContent1.contains("32"));
		
		String csvContent2 = readCSV(csv2);
		assertTrue (csvContent2.contains(reportDir2));
		assertTrue (csvContent2.contains("32"));
	}

	
	/**
	 * Test method for {@link edu.odu.cs.zeil.report_accumulator.Accumulator#accumulateStatistics(java.net.URL, java.nio.file.Path[])}.
	 * @throws IOException 
	 */
	@Test
	public void testOldStatistics() throws IOException {
		Path[] paths = {testArea};
		Accumulator acc = new Accumulator(reportURL, paths);
		acc.register(report1);
		acc.register(report2);
		
		Path csv1 = testArea.resolve(reportDir1 + ".csv");
		Path csv2 = testArea.resolve(reportDir2 + ".csv");
		BufferedWriter out = new BufferedWriter (new FileWriter(csv1.toFile()));
		out.write ("a,b,c\nd,44,55\n");
		out.close();
		out = new BufferedWriter (new FileWriter(csv2.toFile()));
		out.write ("a,b,c\nd,44,55\n");
		out.close();
		
		
		acc.accumulateStatistics();
		Path csv3 = testArea.resolve(reportDir3 + ".csv");
		assertTrue (csv1.toFile().exists());
		assertTrue (csv2.toFile().exists());
		assertFalse (csv3.toFile().exists());
		
		String csvContent1 = readCSV(csv1);
		assertFalse (csvContent1.contains(reportDir1));
		assertTrue (csvContent1.contains("44"));
		assertTrue (csvContent1.contains("32"));
		
		String csvContent2 = readCSV(csv2);
		assertFalse (csvContent2.contains(reportDir2));
		assertTrue (csvContent2.contains("32"));
		assertTrue (csvContent2.contains("55"));
	}

	
	
	
	private String readCSV(Path csvFile) throws IOException {
		StringBuffer buf = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(csvFile.toFile()));
		String line;
		while ((line = in.readLine()) != null) {
			buf.append(line);
			buf.append("\n");
		}
		in.close();
		return buf.toString();
		
	}

}
