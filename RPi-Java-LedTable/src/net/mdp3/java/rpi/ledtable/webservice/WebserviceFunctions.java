/**
 * 
 */
package net.mdp3.java.rpi.ledtable.webservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.mdp3.java.rpi.ledtable.LedTable;
import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.effects.EffectMode;
import net.mdp3.java.rpi.ledtable.webservice.WebserviceCommands.WebserviceCommand;
import net.mdp3.java.util.file.SimpleFileIO;
import net.mdp3.java.util.xml.XmlHelper;

/**
 * @author Mikel
 *
 */
public class WebserviceFunctions {
	private final static Logger LOG = Logger.getLogger(WebserviceFunctions.class.getName());
	private final static String name = "WebserviceFunctions";
	
	private static LedTable table = null;
	
	private final static String DEFAULT_WEB_FILE = "LedTable.html";
	private final static String WEB_FOLDER = "www";
	
	/**
	 * Set the ledTable for the methods to use, this must be called before 
	 * running the parseParams method
	 * 
	 * @param t
	 */
	protected static void setLedTable(LedTable t) {
		table = t;
	}
	
	/**
	 * Before calling this function, the LedTable must be set using the 
	 * setLedTable method
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	protected static String parseParams(Map<String,String> params) throws Exception {
		LOG.entering(name, "parseParams");
		
		if (table == null) throw new Exception("Table not Initialized");
		
		String ret = "";

		//read mode parameter
		WebserviceCommand wsCmd = loadCmd(params);
		
		if (wsCmd != null) {
			ret = processCmd(wsCmd, params);
		}
		
		LOG.exiting(name, "parseParams", "ret: " + ret);
		return ret;
	}
	
	private static WebserviceCommand loadCmd(Map<String, String> params) {
		//read mode parameter
		WebserviceCommand wsCmd = null;
		String cmdStr = params.get(WebserviceCommands.WS_CMD);
		if (cmdStr != null && cmdStr.length() > 0) {
			try {
				wsCmd = WebserviceCommand.valueOf(cmdStr.toUpperCase().trim());
			} catch (Exception e) {
				LOG.severe("Error Invalid Command: " + cmdStr + " " + e);

				return null;
			}
		}
		
		return wsCmd;
	}
	
	private static String processCmd(WebserviceCommand wsCmd, Map<String, String> params) {
		String ret = "";
		
		if (wsCmd == WebserviceCommand.MODE) {
			ret = processCmdMode(params);
		} else if (wsCmd == WebserviceCommand.SAVE) {
			ret = processCmdSave(params);
		} else if (wsCmd == WebserviceCommand.CURRENT_SELECTION_INFO) {
			ret = processCurSelectionInfo(params);
		} else if (wsCmd == WebserviceCommand.EFFECT_LIST) {
			ret = processCmdEffectList(params);
		} else if (wsCmd == WebserviceCommand.COMMAND_LIST) {
			ret = processCmdCommandList();
		} else if (wsCmd == WebserviceCommand.FILE) {
			ret = processCmdFile(params);
		} else if (wsCmd == WebserviceCommand.EFFECT_INFO) {
			ret = processEffectInfo(params);
		} else if (wsCmd == WebserviceCommand.PLAYLIST_INFO) {
			ret = processPlaylistInfo(params);
		}
		
		return ret;
	}
	
	private static String processCmdMode(Map<String, String> params) {
		String ret = "";
		
		try {
			EffectMode mode = EffectMode.valueOf(params.get(WebserviceCommands.WS_MODE));
			ret += table.newSelection(new LedTable_Selection(mode, params));
			ret = "OK";
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Invalid Mode\n" + e.getMessage());
			ret = "Invalid Mode\n" + e.getMessage();
		}
		
		return ret;
	}
	
	private static String processCmdSave(Map<String, String> params) {
		String ret = "";
		ret += "<a href='" + LedTable_Settings.wsName + "'>LedTable</a><br>\n";
		ret += "OK";
		
		try {
			table.saveSelection();
			ret = "Selection Saved";
		} catch (Exception e) {
			System.err.println("Error Saving Selection");
			ret = "Error Saving Selection\n" + e.getMessage();
			
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private static String processCurSelectionInfo(Map<String, String> params) {
		String ret = "";
		
		Document doc = XmlHelper.getNewDoc();
		try {
			ret += "\n" + XmlHelper.elementToString(table.getCurrentSelection().toXml(doc));
		} catch (TransformerFactoryConfigurationError
				| TransformerException e) {
			e.printStackTrace();
			LOG.severe("Error Converting current selection to XML: " + e);
		}

		ret = removeFormatting(ret);
		
		return ret;
	}
	
	private static String processEffectInfo(Map<String, String> params) {
		String ret = "";
		
		Document doc = XmlHelper.getNewDoc();
		try {
			ret += "\n" + XmlHelper.elementToString(table.getEffect().getEffectInfo().toXml(doc));
		} catch (TransformerFactoryConfigurationError
				| TransformerException e) {
			e.printStackTrace();
			LOG.severe("Error Converting current selection to XML: " + e);
		}

		ret = removeFormatting(ret);
		
		return ret;
	}
	
	private static String processCmdEffectList(Map<String, String> params) {
		String ret = "";
		
		Document doc = XmlHelper.getNewDoc();
		Element rootElement = doc.createElement("EffectList");
		doc.appendChild(rootElement);
		
		for (EffectMode em : EffectMode.values())
			rootElement.appendChild(em.toXml(doc));
		
		try {
			ret = XmlHelper.elementToString(rootElement);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
			LOG.severe("Error Creating Effect List: " + e);
		}
		
		ret = removeFormatting(ret);
		
		return ret;
	}
	
	private static String processCmdCommandList() {
		String ret = "";
		
		Document doc = XmlHelper.getNewDoc();
		Element rootElement = doc.createElement("CommandList");
		doc.appendChild(rootElement);
		
		for (WebserviceCommand ws : WebserviceCommand.values())
			rootElement.appendChild(ws.toXml(doc));
		
		try {
			ret = XmlHelper.elementToString(rootElement);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
			LOG.severe("Error Creating Command List: " + e);
		}
		
		return ret;
	}
	
	private static String processCmdFile(Map<String, String> params) {
		String fileName = "";
		
		if (params.get("fileName") != null)
			fileName = params.get("fileName").toString();
		else if (params.get("file") != null)
			fileName = params.get("file").toString();

		return serveFiles(fileName);
	}
	
	protected static String serveFiles() {
		return serveFiles("");
	}
	
	protected static String serveFiles(String fileName) {
		String ret = "";
		if (fileName == null || fileName == "") {
			try {
				//TODO improve landing page, maybe change html file to just header and try to generate content
				File f = new File(WEB_FOLDER + "/" + DEFAULT_WEB_FILE);
				ret = SimpleFileIO.loadFileToString(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			if (fileName.contains("..")) return "Invalid Filename Sequence: ..";
			try {
				File f = new File(WEB_FOLDER + "/" + fileName);
				ret = SimpleFileIO.loadFileToString(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				LOG.severe("File Not Found: " + e);
				
				ret = "Error File Not Found";
			}
		}
		
		return ret;
	}
	
	private static String removeFormatting(String s) {
		//Remove xml formatting
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\t", "");
		
		return s;
	}
	
	private static String processPlaylistInfo(Map<String, String> params) {
		String ret = "";
		
		if (table.getCurrentPlaylist().isPlaying()) {
			ret = "PLAYING";
		} else ret = "STOPPED";
		
		return ret;
	}
}
