package edu.odu.cs.zeil.report_accumulator;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class TestCheckstyleScanner {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCheckStyle() {
		CheckstyleScanner scanner = new CheckstyleScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "checkstyle"));
		assertEquals (1, scanner.getDescriptors().length);
		assertTrue (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (1, stats.length);
		assertEquals (51.0, stats[0], 0.0001);
	}

	@Test
	public void testJUnit() {
		CheckstyleScanner scanner = new CheckstyleScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "junit"));
		assertEquals (1, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

}
