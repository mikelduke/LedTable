package net.mdp3.java.rpi.ledtable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.mdp3.java.rpi.ledtable.effects.EffectMode;
import net.mdp3.java.util.file.FileNameFormatter;
import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

/**
 * 
 * @author Mikel
 *
 * Class to hold the selection mode and parameter map
 */

public class LedTable_Selection implements DomXml {
	private final static Logger LOG = Logger.getLogger(LedTable_Selection.class.getName());
	private final static String name = "LedTable_Selection";
	
	public final static String NODE_NAME = "selection";
	public final static String DOC_ROOT = "LedTable_Selection";
	
	private Map<String,String> params;
	
	private EffectMode selection_mode = EffectMode.PULSE1;
	
	public LedTable_Selection() {
		this(EffectMode.OFF);
	}
	
	public LedTable_Selection(EffectMode s_mode) {
		this(s_mode, null);
	}

	public LedTable_Selection(EffectMode s_mode, Map<String,String> params) {
		this.setMode(s_mode, params);
	}
	
	public LedTable_Selection(String wsCmd) {
		this.params = new HashMap<String, String>();
		
		if (wsCmd != null && wsCmd.length() > 0) {
			if (wsCmd.startsWith("?")) wsCmd = wsCmd.substring(1, wsCmd.length());
			
			String[] pairs = wsCmd.split("&");
			for (int i = 0; i < pairs.length; i++) {
				String pair = pairs[i];
				
				int divider = pair.indexOf("=");
				if (divider > 0 && divider < pair.length() - 1) {
					String key = pair.substring(0, divider);
					String value = pair.substring(divider + 1, pair.length());
					
					LOG.info("Key: " + key + " Value: " + value);
					params.put(key, value);
					
					if (key.equals("mode")) {
						this.selection_mode = EffectMode.valueOf(value.toUpperCase());
						LOG.info("Mode Set to: " + value.toUpperCase());
					}
				}
			}
		}
	}
	
	public void setMode(EffectMode mode) {
		this.setMode(mode, null);
	}
	
	/**
	 * Sets the selection mode and parameters
	 * 
	 * @param s_mode Selected mode from the Mode enum
	 * @param params Parameters for the mode, used by the Effect class
	 */
	public void setMode(EffectMode s_mode, Map<String,String> params) {
		LOG.entering(name, "setMode");
		
		if (params == null) params = new HashMap<String,String>();
		
		this.selection_mode = s_mode;
		this.params = params;
		
		LOG.finest("setMode: " + this.toString());
		
		LOG.exiting(name, "setMode");
	}
	
	public EffectMode getMode() {
		return this.selection_mode;
	}
	
	public Map<String, String> getParams() {
		return this.params;
	}
	
	/**
	 * Autogenerates a new save file name in the default location and saves
	 * 
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void save() throws IOException, TransformerFactoryConfigurationError, TransformerException {
		LOG.entering(name, "save");
		
		String fileName = FileNameFormatter.getDatedFileName(LedTable_Settings.defaultFilePrefix, "xml");
		
		this.save(LedTable_Settings.defaultSaveFolder + "/selections/" + fileName);
		
		LOG.info("Selection saved to: " + fileName);
		
		LOG.exiting(name, "save", "fileName:" + fileName);
	}
	
	/**
	 * Saves to the specified filename
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void save(String fileName) throws IOException, TransformerFactoryConfigurationError, TransformerException {
		LOG.entering(name, "save", "fileName: " + fileName);
		
		File file = new File(fileName);
		if (file.createNewFile()) this.save(file);
		
		LOG.exiting(name, "save");
	}
	
	/**
	 * Saves the selection to the specified file object
	 * 
	 * @param f
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void save(File f) throws TransformerFactoryConfigurationError, TransformerException {
		LOG.entering(name, "save", "File:" + f);
		
		String wsCall = LedTable_Settings.wsName + "?mode=" + getMode().toString();
		for (Map.Entry<String, String> entry: getParams().entrySet()) {
			if (!entry.getKey().toLowerCase().equals("mode"))
				wsCall += "&" + entry.getKey() + "=" + entry.getValue();
		}
		LOG.fine("wsCall: " + wsCall);
		
		//Create Doc
		Document doc = XmlHelper.getNewDoc();
		Element selectionE = this.toXml(doc);
		
		//Add elements
		Element rootElement = doc.createElement(LedTable_Selection.DOC_ROOT);
		doc.appendChild(rootElement);
		rootElement.appendChild(selectionE);
		
		LOG.finer("XML:\n" + XmlHelper.elementToString(rootElement));
		
		//Save
		XmlHelper.saveDocument(doc, f);
		
		LOG.exiting(name, "save", "File:"+f);
	}
	
	/**
	 * Loads the selection from an xml file
	 * @param fileName
	 */
	public void load(String fileName) {
		LOG.entering(name, "load", "fileName: " + fileName);
		
		File f = new File(fileName);
		this.load(f);
		
		LOG.exiting(name, "load");
	}
	
	/**
	 * Loads this selection object mode and parameter list from an xml file
	 * 
	 * @param f
	 */
	public void load(File f) {
		LOG.entering(name, "load", "file: " + f);
		
		Element selectionE;
		try {
			selectionE = XmlHelper.readXmlFile(f);
			
			if (selectionE.getNodeName() == LedTable_Selection.DOC_ROOT) {
				selectionE = (Element) selectionE.getFirstChild();
				this.fromXml(selectionE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Error Loading Selection File: " + e);
		}
		
		LOG.exiting(name, "load");
	}
	
	public String toString() {
		LOG.entering(name, "toString");
		
		String ret = "";
		
		ret += "LedTable_Selection ";
		ret += " Mode: " + this.getMode().toString() + " ";
		
		if (this.getParams().size() > 0) ret += " Params: ";
		for (Map.Entry<String, String> entry: getParams().entrySet()) {
			if (!entry.getKey().toLowerCase().equals("mode"))
				ret += " " + entry.getKey() + ": " + entry.getValue() + " ";
		}
		
		LOG.exiting(name, "toString", ret);
		return ret;
	}
	
	/**
	 * Converts the selection object to a command suitable to be passes to the webservice
	 * 
	 * @return Webservice command to be appended to http://ip:port/name
	 */
	public String toWSCmd() {
		LOG.entering(name, "toWSCmd");
		
		String ret = "";
		
		ret += "?mode=" + this.getMode().toString();
		for (Map.Entry<String, String> entry: getParams().entrySet()) {
			if (!entry.getKey().toLowerCase().equals("mode"))
				ret += "&" + entry.getKey() + "=" + entry.getValue();
		}
		
		LOG.exiting(name, "toWSCmd", ret);
		return ret;
	}
	
	/**
	 * Loads selection mode and params from XML then calls setMode
	 * 
	 * @param e
	 */
	public void fromXml(Element e) {
		LOG.entering(name, "fromXml", "e:"+e);
		
		String mode = "";
		HashMap<String,String> xmlParams = new HashMap<String,String>();
		
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String key = n.getNodeName();
				String value = n.getTextContent();
				
				LOG.finer("fromXml Key: " + key + " Value: " + value);
				
				xmlParams.put(key, value);
				if (key.toLowerCase().equals("mode")) mode = value;
			}
		}
		
		this.setMode(EffectMode.valueOf(mode), xmlParams);
		
		LOG.exiting(name, "fromXml");
	}

	/**
	 * Converts this object to xml elements for saving to file
	 * 
	 * @param doc
	 * @return
	 */
	@Override
	public Element toXml(Document doc) {
		LOG.entering(name, "toXml", "doc:"+doc);
		
		Element selectionE = doc.createElement(LedTable_Selection.NODE_NAME);
		
		selectionE.appendChild(XmlHelper.newTextElement(doc, "mode", this.getMode().toString()));
		
		for (Map.Entry<String, String> entry: getParams().entrySet()) {
			if (!entry.getKey().toLowerCase().equals("mode"))
				selectionE.appendChild(XmlHelper.newTextElement(doc, entry.getKey(), entry.getValue()));
		}
		
		selectionE.appendChild(this.getMode().toXml(doc));
		
		return selectionE;
	}
	
	@Override
	public boolean equals(Object o) {
		LOG.entering(name, "equals", "this: " + this.toString() + " o: " + o.toString());
		
		if (this.getClass() != o.getClass()) return false;
		
		final LedTable_Selection oSelection = (LedTable_Selection)o;
		if (this.getMode() != oSelection.getMode()) return false;
		
		if (!this.params.equals(oSelection.getParams())) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 2 * hash + (this.getParams().hashCode());
		return hash;
	}
}
