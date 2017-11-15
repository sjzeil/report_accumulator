package edu.odu.cs.zeil.report_accumulator;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class TestSpotBugsScanner {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCheckStyle() {
		SpotBugsScanner scanner = new SpotBugsScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "checkstyle"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testJUnit() {
		SpotBugsScanner scanner = new SpotBugsScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "junit"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testSpotBugs() {
		SpotBugsScanner scanner = new SpotBugsScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "spotbugs"));
		assertEquals (2, scanner.getDescriptors().length);
		assertTrue (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (2, stats.length);
		assertEquals (8.0, stats[0], 0.0001);
		assertEquals (2.0, stats[1], 0.0001);
	}

	@Test
	public void testFindBugs2() {
		SpotBugsScanner scanner = new SpotBugsScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "findbugs2"));
        assertEquals (2, scanner.getDescriptors().length);
        assertFalse (scanner.containsReport());
        double[] stats = scanner.extractStatistics();
        assertNotNull(stats);
        assertEquals (0, stats.length);
	}

    @Test
    public void testFindBugs() {
        SpotBugsScanner scanner = new SpotBugsScanner();
        scanner.setDirectory(Paths.get("src", "test", "data", "findbugs"));
        assertEquals (2, scanner.getDescriptors().length);
        assertFalse (scanner.containsReport());
        double[] stats = scanner.extractStatistics();
        assertNotNull(stats);
        assertEquals (0, stats.length);
    }
	
	
}
