/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Report scanner for JUnit test reports.
 * Accumulates statistics on number of tests failed and number passed.
 * 
 * 
 * @author zeil
 *
 */
public class JacocoBranchCoverageScanner implements ReportScanner {
	
	Path reportDirectory;
	Document doc;

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#setDirectory(java.nio.file.Path)
	 */
	@Override
	public void setDirectory(Path possibleReportDirectory) {
		reportDirectory = possibleReportDirectory;
		doc = null;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#containsReport()
	 */
	@Override
	public boolean containsReport() {
		double [] results = extractStatistics();
		return results != null && results.length > 0;
	}

	private boolean readDOM(File xmlFile) {
		if (doc == null) {
			doc = new DOMParser().readDOM(xmlFile);
			return (doc != null);
		} else {
			return true;
		}
	}

	
	private double[] statistics;
	
	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#extractStatistics()
	 */
	@Override
	public double[] extractStatistics() {
		statistics = new double[0];
		if (reportDirectory.toFile().exists()) {
			try {
				Files.walkFileTree(reportDirectory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path path,
							BasicFileAttributes attr) {
						if (attr.isRegularFile()) {
							File xmlFile = path.toFile();
							File parent = xmlFile.getParentFile();
							if (xmlFile.getName().endsWith(".xml")
							        || xmlFile.getName().endsWith(".html")) {
								double[] result = extractStatistics(xmlFile);
								if (result != null && result.length > 0) {
									statistics = result;
									return FileVisitResult.TERMINATE;
								}
							}
						}
						return FileVisitResult.CONTINUE;
					}

				});
			} catch (IOException e) {
				return statistics;
			}

		}
		return statistics;
	}


	
	private double[] extractStatistics(File xmlFile) {
		if (xmlFile.getName().equals("index.html")) {
		if (xmlFile.exists()) {
			if (readDOM(xmlFile)) {
				Element root = doc.getDocumentElement();
				if (root.getTagName().equals("report")) {
                    try {
                        NodeList counters = root.getChildNodes();
                        int numCovered = -1;
                        int numMissed = -1;
                        for (int i = 0; i < counters.getLength(); ++i) {
                            Node nd = counters.item(i);
                            if (nd instanceof Element) {
                                Element counter = (Element)nd;
                                if (counter.getTagName().equals("counter")
                                        && counter.getAttribute("type").equals("BRANCH")) {
                                    numCovered = Integer.parseInt(counter.getAttribute("covered"));
                                    numMissed = Integer.parseInt(counter.getAttribute("missed"));
                                    break;
                                }
                            }
                        }
                        if (numCovered < 0 || numMissed < 0) {
                            return null;
                        }
                        
                        double[] results = new double[2];
                        results[1] = numMissed;
                        results[0] = numCovered;
                        return results;
                    } catch (Exception e) {
                        return null;
                    }
				}
				else if (root.getTagName().equals("html")) {
					try {
						NodeList allTables = doc.getElementsByTagName("table");
						int numCovered = -1;
						int numMissed = -1;
						for (int i = 0; i < allTables.getLength(); ++i) {
							Node nd = allTables.item(i);
							Element table = (Element)nd;
							if (table.getAttribute("id").equals("coveragetable")) {
								Element foot = (Element) table.getElementsByTagName("tfoot").item(0);
								NodeList cols = foot.getElementsByTagName("td");
								String branchCoverage = cols.item(3).getTextContent(); // format: nn of NN
								try (Scanner in = new Scanner(branchCoverage)) {
									numMissed = in.nextInt();
									String token = in.next();
									if (!token.equals("of")) {
										continue;
									}
									int total = in.nextInt();
									numCovered = total - numMissed;
									break;
								}
							}
						}
						if (numCovered < 0 || numMissed < 0) {
							return null;
						}
						
						double[] results = new double[2];
						results[1] = numMissed;
						results[0] = numCovered;
						return results;
					} catch (Exception e) {
						return null;
					}
				}
			}
		}
	    }
		return null;
	}

	
	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#getDescriptors()
	 */
	@Override
	public String[] getDescriptors() {
		String[] result = new String[2];
		result[1] = "Missed";
		result[0] = "Covered";
		return result;
	}

}
