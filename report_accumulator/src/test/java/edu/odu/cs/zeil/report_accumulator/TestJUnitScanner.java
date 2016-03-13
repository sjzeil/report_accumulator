package edu.odu.cs.zeil.report_accumulator;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class TestJUnitScanner {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCheckStyle() {
		JUnitScanner scanner = new JUnitScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "checkstyle"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testFindbugs() {
		JUnitScanner scanner = new JUnitScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "findbugs"));
		assertEquals (2, scanner.getDescriptors().length);
		assertFalse (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (0, stats.length);
	}

	@Test
	public void testJUnit() {
		JUnitScanner scanner = new JUnitScanner();
		scanner.setDirectory(Paths.get("src", "test", "data", "junit"));
		assertEquals (2, scanner.getDescriptors().length);
		assertTrue (scanner.containsReport());
		double[] stats = scanner.extractStatistics();
		assertNotNull(stats);
		assertEquals (2, stats.length);
		assertEquals (4.0, stats[0], 0.0001);
		assertEquals (2.0, stats[1], 0.0001);
	}

}
