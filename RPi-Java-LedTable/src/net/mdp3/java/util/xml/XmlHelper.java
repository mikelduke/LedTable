/**
 * 
 */
package net.mdp3.java.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Mikel
 *
 */
public class XmlHelper {
	
	/**
	 * Method to make adding repetitive text nodes to an element easier.
	 * 
	 * @param doc Java DOM Doc which is the items get added to
	 * @param name Name of the new element
	 * @param val Text Value of the new element
	 * @return Element containing a value ready to append to another Element
	 */
	public static Element newTextElement(Document doc, String name, Object val) {
		Element e = doc.createElement(name);
		e.appendChild(doc.createTextNode(val.toString()));
		return e;
	}
	
	/**
	 * Converts a Java DOM Xml Element to a String for simple output to 
	 * console, logs, or files.
	 * 
	 * @param e Element to convert
	 * @return String representation of element
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String elementToString(Element e) throws TransformerFactoryConfigurationError, TransformerException {
		String ret = "";
		Transformer transformer;

		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(e);
		transformer.transform(source, result);

		ret = result.getWriter().toString();
		
		return ret;
	}
	
	/**
	 * Returns a new Java DOM Doc object for use with elements. This doc should 
	 * not be used for saving it is just used for internal copy/move/load type 
	 * functions. 
	 * 
	 * @return
	 */
	public static Document getNewDoc() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		
		try {
			docBuilder = docFactory.newDocumentBuilder();
			
			// root elements
			Document doc = docBuilder.newDocument();
			//Element rootElement = doc.createElement(rootElementName);
			//doc.appendChild(rootElement);
			
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void saveDocument(Document doc, File file) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		
 		transformer.transform(source, result);
	}
	
	/**
	 * Reads in an xml file and returns the root element
	 * 
	 * @param fileLocation
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Element readXmlFile(String fileLocation) throws ParserConfigurationException, SAXException, IOException {
		File f = new File(fileLocation);
		return XmlHelper.readXmlFile(f);
	}
	
	/**
	 * Reads in an xml file and returns the root Element
	 * 
	 * @param f
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Element readXmlFile(File f) throws ParserConfigurationException, SAXException, IOException {
		Element retE = null;
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
	 
		doc.getDocumentElement().normalize();
		
		retE = doc.getDocumentElement();
		
		return retE;
	}
}
