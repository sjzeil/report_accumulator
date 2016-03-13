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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A "quiet" DOM parser that does not waste time importing large DTDs.
 * 
 * @author zeil
 *
 */
public class DOMParser {

	/**
	 * Parse a file of possibly XML content
	 * @param xmlFile the file to parse
	 * @return a DOM document or null if the file could not be read and parsed.
	 */
	public Document readDOM(File xmlFile) {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			// Suppress loading of DTDs (because fetching the HTML DTDs
			// can take minutes at a time).
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
			
			// Suppress logging to standard error
			builder.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException e) throws SAXException {
					;
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					throw e;
				}

				@Override
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}
			});
			
			try (InputStream in = new BufferedInputStream(new FileInputStream(xmlFile))) {
				doc = builder.parse (in);
				return doc;
			} catch (FileNotFoundException e) {
				return null;
			} catch (IOException e) {
				return null;
			} catch (SAXException e) {
				return null;
			}
		} catch (ParserConfigurationException e) {
			System.err.println("Could not create an XML parser: " + e);
			e.printStackTrace();
		}
		return null;
	}

}
