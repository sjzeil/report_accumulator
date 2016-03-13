/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				factory.setValidating(false);
				DocumentBuilder builder = factory.newDocumentBuilder();
				builder.setEntityResolver(new EntityResolver() {
			        @Override
			        public InputSource resolveEntity(String publicId, String systemId)
			                throws SAXException, IOException {
			            if (systemId.contains(".dtd")) {
			                return new InputSource(new StringReader(""));
			            } else {
			                return null;
			            }
			        }
			    });
				try (InputStream in = new BufferedInputStream(new FileInputStream(xmlFile))) {
					doc = builder.parse (in);
					return (doc != null);
				} catch (FileNotFoundException e) {
					return false;
				} catch (IOException e) {
					return false;
				} catch (SAXException e) {
					return false;
				}
			} catch (ParserConfigurationException e) {
				System.err.println("Could not create an XML parser: " + e);
				e.printStackTrace();
			}
		} else {
			return true;
		}
		return false;
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
							if (xmlFile.getName().endsWith(".html")) {
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
		if (xmlFile.exists()) {
			if (readDOM(xmlFile)) {
				Element root = doc.getDocumentElement();
				if (root.getTagName().equals("html")) {
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
								Scanner in = new Scanner(branchCoverage);
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
