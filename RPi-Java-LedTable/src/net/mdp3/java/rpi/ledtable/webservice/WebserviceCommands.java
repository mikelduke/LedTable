/**
 * 
 */
package net.mdp3.java.rpi.ledtable.webservice;

import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Mikel
 *
 */
public class WebserviceCommands {
	
	public static final String WS_CMD = "cmd";
	public static enum WebserviceCommand implements DomXml {
		COMMAND_LIST,
		MODE,
		SAVE,
		CURRENT_SELECTION_INFO,
		EFFECT_INFO,
		EFFECT_LIST,
		PLAYLIST,
		PLAYLIST_INFO,
		FILE;

		public static final String NODE_NAME = "Command";
		public static final String NODE_CMD_NAME = "Name";
		
		@Override
		public Element toXml(Document doc) {
			Element wsCmdE = doc.createElement(WebserviceCommand.NODE_NAME);
			
			wsCmdE.appendChild(XmlHelper.newTextElement(doc, WebserviceCommand.NODE_CMD_NAME, this.name()));
			
			return wsCmdE;
		}

		@Override
		public void fromXml(Element e) throws Exception {
			//Not Implemented
			throw new Exception("Not Implemented");
		}
	}
	
	public static final String WS_MODE = "mode";
	public static final String WS_OUTPUT = "output";
}