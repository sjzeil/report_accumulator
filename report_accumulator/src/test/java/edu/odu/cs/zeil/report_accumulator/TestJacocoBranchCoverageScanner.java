package edu.odu.cs.zeil.report_accumulator;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestJacocoBranchCoverageScanner {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCheckStyle() {
		JacocoBranchCoverageScanner scanner = new JacocoBranchCoverageScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "checkstyle"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testFindbugs() {
		JacocoBranchCoverageScanner scanner = new JacocoBranchCoverageScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "findbugs"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testJUnit() {
		JacocoBranchCoverageScanner scanner = new JacocoBranchCoverageScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "junit"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testJacoco() {
		JacocoBranchCoverageScanner scanner = new JacocoBranchCoverageScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "jacoco"));
		assertEquals (2, scanner.getDescriptors().length);
		assertTrue (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (2, stats.length);
		assertEquals (75.0, stats[0], 0.0001);
		assertEquals (47.0, stats[1], 0.0001);
	}

    @Test
    public void testJacoco2() {
        JacocoBranchCoverageScanner scanner = new JacocoBranchCoverageScanner();
        scanner.setDirectory(Paths.get("src", "test", "data", "jacoco2"));
        assertEquals (2, scanner.getDescriptors().length);
        assertTrue (scanner.containsReport());
        double[] stats = scanner.extractStatistics();
        assertNotNull(stats);
        assertEquals (2, stats.length);
        assertEquals (197.0, stats[0], 0.0001);
        assertEquals (89.0, stats[1], 0.0001);
    }


public static void main(String args[]) 
{
    Result result= JUnitCore.runClasses(TestJacocoBranchCoverageScanner.class);
    for (Failure failure : result.getFailures())
        {
                System.out.println(failure.toString());
        }
}


}
