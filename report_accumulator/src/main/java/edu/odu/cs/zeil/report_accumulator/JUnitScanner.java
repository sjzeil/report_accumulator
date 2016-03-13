/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.io.File;
import java.nio.file.Path;

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
public class JUnitScanner implements ReportScanner {
	
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
						if (!title.getTextContent().contains("Test Summary")) {
							return null;
						}
						NodeList allDivs = doc.getElementsByTagName("div");
						int numTests = -1;
						int numFailures = -1;
						int numIgnored = -1;
						for (int i = 0; i < allDivs.getLength(); ++i) {
							Node nd = allDivs.item(i);
							Element div = (Element)nd;
							if (div.getAttribute("id").equals("tests")) {
								Element counterDiv = (Element) div.getElementsByTagName("div").item(0);
								numTests = Integer.parseInt(counterDiv.getTextContent());
							} else if (div.getAttribute("id").equals("failures")) {
								Element counterDiv = (Element) div.getElementsByTagName("div").item(0);
								numFailures = Integer.parseInt(counterDiv.getTextContent());
							} else if (div.getAttribute("id").equals("ignored")) {
								Element counterDiv = (Element) div.getElementsByTagName("div").item(0);
								numIgnored = Integer.parseInt(counterDiv.getTextContent());
							}
						}
						if (numTests < 0 || numFailures < 0 || numIgnored < 0) {
							return null;
						}
						
						double[] results = new double[2];
						results[1] = numFailures;
						results[0] = numTests - numFailures - numIgnored;
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
		result[1] = "Failed";
		result[0] = "Passed";
		return result;
	}

}
