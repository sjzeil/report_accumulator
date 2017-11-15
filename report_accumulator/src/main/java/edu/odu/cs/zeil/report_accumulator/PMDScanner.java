/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.io.File;
import java.nio.file.Path;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Report scanner for Checkstyle reports. Accumulates data on number of warnings issued.
 * 
 * @author zeil
 *
 */
public class PMDScanner implements ReportScanner {
	
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
				if (xmlFile.getName().endsWith(".xml")) {
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
				if (root.getTagName().equals("pmd")) {
					NodeList details = root.getElementsByTagName("violation");
					int count = details.getLength();
					double[] result = new double[1];
					result[0] = (double)count;
					return result;
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
		String[] result = new String[1];
		result[0] = "Violations";
		return result;
	}

}
