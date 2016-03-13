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
import java.nio.file.Path;

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
public class FindBugsScanner implements ReportScanner {
	
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

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#extractStatistics()
	 */
	@Override
	public double[] extractStatistics() {
		if (reportDirectory.toFile().exists()) {
			for (File xmlFile: reportDirectory.toFile().listFiles()) {
				if (xmlFile.getName().endsWith(".html")) {
					double[] result = extractStatistics(xmlFile);
					if (result != null && result.length > 0) {
						return result;
					}
				}
			}
		}
		return new double[0];
	}


	
	private double[] extractStatistics(File xmlFile) {
		if (xmlFile.exists()) {
			if (readDOM(xmlFile)) {
				Element root = doc.getDocumentElement();
				if (root.getTagName().equals("html")) {
					try {
						Element head = (Element) root.getElementsByTagName("head").item(0);
						Element title = (Element) head.getElementsByTagName("title").item(0);
						if (!title.getTextContent().contains("FindBugs Report")) {
							return null;
						}
						NodeList allTableRows = doc.getElementsByTagName("tr");
						int numHighPriority = -1;
						int numMedPriority = -1;
						for (int i = 0; i < allTableRows.getLength(); ++i) {
							Node nd = allTableRows.item(i);
							Element tr = (Element)nd;
							NodeList cols = tr.getElementsByTagName("td");
							if (cols.getLength() == 3) {
								String rowHeading = cols.item(0).getTextContent();
								String value = cols.item(1).getTextContent();
								if (rowHeading.equals("High Priority Warnings")) {
									if (value.length() > 0) {
										try {
											numHighPriority = Integer.parseInt(value);
										} catch (NumberFormatException e) {
											numHighPriority = 0;
										}
									}
								}
								if (rowHeading.equals("Medium Priority Warnings")) {
									if (value.length() > 0) {
										try {
											numMedPriority = Integer.parseInt(value);
										} catch (NumberFormatException e) {
											numMedPriority = 0;
										}
									}
								}
							}
							if (numHighPriority >= 0 && numMedPriority >= 0) {
								break;
							}
						}
						if (numHighPriority < 0 || numMedPriority < 0) {
							return null;
						}
						
						double[] results = new double[2];
						results[1] = numHighPriority;
						results[0] = numMedPriority;
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
		result[1] = "High Priority";
		result[0] = "Medium Priority";
		return result;
	}

}
