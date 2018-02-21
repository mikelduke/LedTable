/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects.playlist;

import java.util.logging.Logger;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mikel
 *
 */
public class PlaylistItem implements DomXml {
	private final static Logger LOG = Logger.getLogger(PlaylistItem.class.getName());
	private final static String name = "PlaylistItem";
	
	public final static String NODE_NAME = "playlistItem";
	public final static String NODE_PLAYTIME = "playTime";
	
	private int playTime = 10;
	public static final int DEFAULT_PLAYTIME = 10;
	
	private LedTable_Selection selection = null;
	
	public PlaylistItem() {
		this(new LedTable_Selection());
	}
	
	public PlaylistItem(LedTable_Selection selection) {
		this(DEFAULT_PLAYTIME, selection);
	}
	
	public PlaylistItem(int playTime, LedTable_Selection selection) {
		this.setPlayTime(playTime);
		this.setSelection(selection);
	}

	public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public LedTable_Selection getSelection() {
		return selection;
	}

	public void setSelection(LedTable_Selection selection) {
		this.selection = selection;
	}
	
	@Override
	public boolean equals(Object o) {		
		if (this.getClass() != o.getClass()) return false;
		
		final PlaylistItem oItem = (PlaylistItem)o;
		if (this.getPlayTime() != oItem.getPlayTime()) return false;
		
		if (!this.getSelection().equals(oItem.getSelection())) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 2 * hash + (this.getPlayTime());
		hash = 2 * hash + (this.getSelection().hashCode());
		return hash;
	}
	
	@Override
	public String toString() {
		String ret = "";
		ret += "PlaylistItem: ";
		ret += " PlayTime: " + this.getPlayTime();
		ret += " Selection: " + this.getSelection();
		return ret;
	}

	@Override
	public Element toXml(Document doc) {
		LOG.entering(name, "toXml", "doc:"+doc);
		
		Element selectionE = doc.createElement(PlaylistItem.NODE_NAME);
		
		selectionE.appendChild(XmlHelper.newTextElement(doc, PlaylistItem.NODE_PLAYTIME, this.getPlayTime()));
		selectionE.appendChild(this.getSelection().toXml(doc));
		
		LOG.exiting(name, "toXml");
		return selectionE;
	}

	@Override
	public void fromXml(Element e) throws Exception {
		LOG.entering(name, "fromXml", "e:"+e);
		
		int time = PlaylistItem.DEFAULT_PLAYTIME;
		LedTable_Selection s = new LedTable_Selection();
		
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String key = n.getNodeName();
				String value = n.getTextContent();
				
				LOG.finer("fromXml Key: " + key + " Value: " + value);
				
				if (key.equals(PlaylistItem.NODE_PLAYTIME)) time = Integer.valueOf(value);
				else if (key.equals(LedTable_Selection.NODE_NAME)) s.fromXml((Element)n); 
			}
		}
		
		this.setPlayTime(time);
		this.setSelection(s);
		
		LOG.exiting(name, "fromXml");
	}
}
