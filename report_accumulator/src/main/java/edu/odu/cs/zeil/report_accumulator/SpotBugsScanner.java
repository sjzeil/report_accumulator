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
 * Report scanner for SpotBugs  reports.
 * Accumulates statistics on number of high and medium priority warnings.
 * 
 * 
 * @author zeil
 *
 */
public class SpotBugsScanner implements ReportScanner {
	
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
						if (!title.getTextContent().contains("SpotBugs Report")) {
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
									} else {
										numHighPriority = 0;
									}
								}
								if (rowHeading.equals("Medium Priority Warnings")) {
									if (value.length() > 0) {
										try {
											numMedPriority = Integer.parseInt(value);
										} catch (NumberFormatException e) {
											numMedPriority = 0;
										}
									} else {
										numMedPriority = 0;
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
