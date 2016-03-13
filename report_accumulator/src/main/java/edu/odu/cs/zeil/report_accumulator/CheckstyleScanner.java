/**
 * 
 */
package edu.odu.cs.zeil.report_accumulator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author zeil
 *
 */
public class CheckstyleScanner implements ReportScanner {
	
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
		Path xmlPath = reportDirectory.resolve("main.xml");
		if (xmlPath.toFile().exists()) {
			if (readDOM(xmlPath)) {
				Element root = doc.getDocumentElement();
				if (root.getTagName().equals("checkstyle")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean readDOM(Path xmlPath) {
		if (doc == null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				try (InputStream in = new BufferedInputStream(new FileInputStream(xmlPath.toFile()))) {
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
		Path xmlPath = reportDirectory.resolve("main.xml");
		if (xmlPath.toFile().exists()) {
			if (readDOM(xmlPath)) {
				Element root = doc.getDocumentElement();
				if (root.getTagName().equals("checkstyle")) {
					NodeList details = root.getElementsByTagName("error");
					int count = details.getLength();
					double[] result = new double[1];
					result[0] = (double)count;
					return result;
				}
			}
		}
		return new double[0];
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#getDescriptors()
	 */
	@Override
	public String[] getDescriptors() {
		String[] result = new String[1];
		result[0] = "Warnings";
		return result;
	}

}
