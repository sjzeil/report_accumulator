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
 * Report scanner for Checkstyle reports. Accumulates data on number
 * of warnings issued.
 * 
 * @author zeil
 *
 */
public class CheckstyleScanner implements ReportScanner {

    /**
     * Directory containing the (possible) report.
     */
    private Path reportDirectory;
    
    /**
     * XML DOM document for entire report.
     */
    private Document doc;

    /* (non-Javadoc)
     * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#setDirectory(java.nio.file.Path)
     */
    @Override
    public final void setDirectory(Path possibleReportDirectory) {
        reportDirectory = possibleReportDirectory;
        doc = null;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.zeil.report_accumulator.ReportScanner#containsReport()
     */
    @Override
    public final boolean containsReport() {
        double[] results = extractStatistics();
        return results != null && results.length > 0;
    }

    /**
     * Read the report if we have not already done so.
     * @param xmlFile report file
     * @return DOM of the report
     */
    private boolean readDOM(final File xmlFile) {
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


    /**
     * Attempt to extract the statistics from a possible report file. 
     * @param xmlFile
     * @return statistics if found or an empty array if not found.
     */
    private double[] extractStatistics(final File xmlFile) {
        if (xmlFile.exists()) {
            if (readDOM(xmlFile)) {
                Element root = doc.getDocumentElement();
                if (root.getTagName().equals("checkstyle")) {
                    NodeList details = root.getElementsByTagName("error");
                    int count = details.getLength();
                    double[] result = new double[1];
                    result[0] = (double) count;
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
    public final String[] getDescriptors() {
        String[] result = new String[1];
        result[0] = "Warnings";
        return result;
    }

}
