/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects.playlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.effects.Effect;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEvent.PlaylistEventName;
import net.mdp3.java.util.file.FileNameFormatter;
import net.mdp3.java.util.xml.DomXml;
import net.mdp3.java.util.xml.XmlHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mikel
 *
 * Effects playlist class to save/load a set of effects and their presets
 */
public class Playlist implements DomXml {
	//TODO Implement this, will be threaded control loop using a XML playlist of saved selections with timing info
	private final static Logger LOG = Logger.getLogger(Playlist.class.getName());
	private final static String name = "Playlist";
	
	private String playlistName = "";
	private ArrayList<PlaylistItem> effectList = new ArrayList<PlaylistItem>();
	
	private Random random = new Random();
	
	private int index = 0;
	
	private final static String DEFAULT_NAME = "Unnamed";
	
	private boolean playing = false;
	
	public static enum PlaylistMode {
		NORMAL,
		RANDOM
	}
	private PlaylistMode playbackMode = PlaylistMode.NORMAL;
	
	public final static String NODE_NAME = "playlist";
	public final static String DOC_ROOT = "LedTable_Playlist";
	
	public final static String NODE_PLAYLIST_NAME = "name";
	public final static String NODE_PLAYLIST_LIST = "playlistItemsList";
	
	private ArrayList<PlaylistEventListener> playlistEventListeners = new ArrayList<PlaylistEventListener>();
	
	private PlaylistTimer timer;
	
	/**
	 * Creates a new playlist object with the default name
	 */
	public Playlist() {
		this(Playlist.DEFAULT_NAME);
	}
	
	/**
	 * Creates a new playlist object with the specified name
	 * 
	 * @param name
	 */
	public Playlist(String name) {
		this.setPlaylistName(name);
		
		this.timer = new PlaylistTimer(PlaylistItem.DEFAULT_PLAYTIME, this);
		this.timer.start();
	}
	
	/**
	 * Sets the playlist name so that it can be changed
	 * 
	 * @param name
	 */
	public void setPlaylistName(String name) {
		this.playlistName = name;
	}
	
	/**
	 * Returns the playlist name
	 * 
	 * @return
	 */
	public String getPlaylistName() {
		return this.playlistName;
	}
	
	/**
	 * Clears the playlist and resets the name
	 */
	public void clear() {
		this.index = 0;
		this.getEffectList().clear();
		this.setPlaylistName(Playlist.DEFAULT_NAME);
	}
	
	/**
	 * Automatically gets the selection info from an effect object and adds 
	 * it to the list, if not already there
	 * 
	 * @param e
	 */
	public boolean addEffectToPlaylist(Effect e) {
		LedTable_Selection s = e.getSelection();
		
		if (!this.getEffectList().contains(s))
			return this.getEffectList().add(new PlaylistItem(s));
		else return false;
	}
	
	/**
	 * Adds the selection to the list if it is not already in it, and sets
	 * the play time. Auto-increments the counter to be the last item
	 * 
	 * @param playTime
	 * @param s
	 * @return
	 */
	public boolean addSelectionToPlaylist(int playTime, LedTable_Selection s) {
		PlaylistItem pi = new PlaylistItem(playTime, s);
		
		boolean addedToList = false;
		if (!this.getEffectList().contains(pi)) {
			addedToList = this.getEffectList().add(pi);
			
			if (addedToList) {
				this.index = this.getEffectList().size() - 1;
			}
		}
		else addedToList = false;
		
		return addedToList;
	}
	
	/**
	 * Adds the selection to the list if it is not already in it with default
	 * play time
	 * 
	 * @param s
	 * @return
	 */
	public boolean addSelectionToPlaylist(LedTable_Selection s) {
		return this.addSelectionToPlaylist(PlaylistItem.DEFAULT_PLAYTIME, s);
	}
	
	/**
	 * Returns the ArrayList of selections in the playlist
	 * 
	 * @return
	 */
	public ArrayList<PlaylistItem> getEffectList() {
		return this.effectList;
	}
	
	/**
	 * Returns a random playlistItem from the playlist
	 * 
	 * @return
	 */
	private PlaylistItem getRandomNext() {
		PlaylistItem pi = null;
		
		int i = random.nextInt(this.getEffectList().size());
		
		pi = this.getEffectList().get(i);
		
		return pi;
	}
	
	public void play() {
		this.playing = true;
		this.startTimer();
		
		this.notifyPEL(new PlaylistEvent(PlaylistEventName.PLAYBACK_PLAY, this.getEffectList().get(getCurrentIndex())));
	}
	
	public void stop() {
		this.playing = false;
		this.timer.stopTimer();
		
		this.notifyPEL(new PlaylistEvent(PlaylistEventName.PLAYBACK_STOP));
	}
	
	/**
	 * Changes to next playlist item in the list, will either increment by 1 
	 * or pick the next item randomly
	 * 
	 * Returns null if the list is empty
	 * 
	 * @return PlaylistItem or null if list is empty
	 */
	public synchronized PlaylistItem next() {
		if (this.getEffectList().size() == 0) return null;
		
		PlaylistItem ret = null;
		
		if (this.getPlaybackMode() == PlaylistMode.NORMAL)
			ret =this.getEffectList().get(this.nextIndex());
		else ret = this.getRandomNext();
		
		this.notifyPEL(new PlaylistEvent(PlaylistEventName.INDEX_CHANGED, ret));
		
		if (this.isPlaying()) this.startTimer();
		
		return ret;
	}
	
	/**
	 * If the playlist mode is NORMAL then it will return the previous item 
	 * in the list by index, if its random it just returns the current item
	 * 
	 * @return
	 */
	public PlaylistItem prev() {
		if (this.getPlaybackMode() == PlaylistMode.NORMAL)
			return this.getEffectList().get(this.prevIndex());
		else return this.getEffectList().get(this.index);
	}
	
	/**
	 * Increments the current index counter, bounds checks, and resets to 0 if over
	 * 
	 * @return
	 */
	private int nextIndex() {
		this.index++;
		
		if (this.index >= this.getEffectList().size()) this.index = 0;
		if (this.index < 0) this.index = 0; //this condition should not be met
		
		return this.index;
	}
	
	/**
	 * Returns the index - 1 and bounds checks, rolls over if necessary
	 * 
	 * @return
	 */
	private int prevIndex() {
		this.index--;
		
		if (this.index >= this.getEffectList().size()) this.index = 0; //this condition should not get met
		if (this.index < 0) this.index = this.getEffectList().size() - 1;
		
		return this.index;
	}
	
	/**
	 * Returns the current playlist index
	 * 
	 * @return
	 */
	public int getCurrentIndex() {
		return this.index;
	}
	
	@Override
	public String toString() {
		String ret = "";
		ret += this.playlistName;
		
		int itemNum = 1;
		for (PlaylistItem s : this.getEffectList()) {
			ret += "\n" + itemNum + ": " + s.toString();
			itemNum++;
		}
		
		return ret;
	}

	public PlaylistMode getPlaybackMode() {
		return playbackMode;
	}

	/**
	 * Sets the playback mode to the specified type and notifies event 
	 * listeners
	 * 
	 * @param playbackMode
	 */
	public void setPlaybackMode(PlaylistMode playbackMode) {
		this.playbackMode = playbackMode;
		
		this.notifyPEL(new PlaylistEvent(PlaylistEventName.PLAYBACK_MODE_CHANGED, this.playbackMode.toString()));
	}
	
	public boolean isPlaying() {
		return this.playing;
	}

	@Override
	public Element toXml(Document doc) {
		LOG.entering(name, "toXml", "doc:"+doc);
		
		Element selectionE = doc.createElement(Playlist.NODE_NAME);
		Element playlistE = doc.createElement(Playlist.NODE_PLAYLIST_LIST);
		
		selectionE.appendChild(XmlHelper.newTextElement(doc, Playlist.NODE_PLAYLIST_NAME, this.getPlaylistName()));
		
		for (PlaylistItem pi: this.getEffectList()) {
			playlistE.appendChild(pi.toXml(doc));
		}
		selectionE.appendChild(playlistE);
		
		LOG.exiting(name, "toXml");
		return selectionE;
	}

	@Override
	public void fromXml(Element e) throws Exception {
		LOG.entering(name, "fromXml", "e:"+e);
		
		this.stop();
		this.clear();
		
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String key = n.getNodeName();
				String value = n.getTextContent();
				
				LOG.finer("fromXml Key: " + key + " Value: " + value);
				
				if (key.equals(Playlist.NODE_PLAYLIST_NAME)) {
					this.setPlaylistName(value);
				}
				else if (key.equals(Playlist.NODE_PLAYLIST_LIST)) {
					NodeList nPlaylistList = n.getChildNodes();
					
					for (int j = 0; j < nPlaylistList.getLength(); j++) {
						Node playlistItemN = nPlaylistList.item(j);
						
						if (playlistItemN.getNodeType() == Node.ELEMENT_NODE && playlistItemN.getNodeName() == PlaylistItem.NODE_NAME) {
							PlaylistItem pi = new PlaylistItem();
							pi.fromXml((Element)playlistItemN);
							
							this.getEffectList().add(pi);
						}
					}
				}
			}
		}
				
		LOG.exiting(name, "fromXml");
	}
	
	/**
	 * Quick save function to default folder as DefaultPrefix-Date.xml
	 * 
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public void save() throws IOException, TransformerFactoryConfigurationError, TransformerException {
		LOG.entering(name, "save");
		
		String fileName = FileNameFormatter.getDatedFileName(LedTable_Settings.defaultFilePrefix, "xml");
		
		this.save(LedTable_Settings.defaultSaveFolder + "/playlists/" + fileName);
		
		LOG.info("Playlist saved to: " + fileName);
		
		LOG.exiting(name, "save", "fileName:" + fileName);
	}
	
	/**
	 * Saves Playlist to xml with fileName
	 * 
	 * @param fileName
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public void save(String fileName) throws TransformerFactoryConfigurationError, TransformerException {
		this.save(new File(fileName));
	}
	
	/**
	 * Saves Playlist to specified file as xml
	 * 
	 * @param f
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public void save(File f) throws TransformerFactoryConfigurationError, TransformerException {
		LOG.entering(name, "save", "File:" + f);
		//TODO Copy save method to XmlHelper
		
		//Create Doc
		Document doc = XmlHelper.getNewDoc();
		Element selectionE = this.toXml(doc);
		
		//Add elements
		Element rootElement = doc.createElement(Playlist.DOC_ROOT);
		doc.appendChild(rootElement);
		rootElement.appendChild(selectionE);
		
		LOG.finer("XML:\n" + XmlHelper.elementToString(rootElement));
		
		//Save
		XmlHelper.saveDocument(doc, f);
		
		LOG.exiting(name, "save", "File:"+f);
	}
	
	/**
	 * Loads the playlist from an xml file
	 * 
	 * @param fileName
	 */
	public void load(String fileName) {
		LOG.entering(name, "load", "fileName: " + fileName);
		
		File f = new File(fileName);
		this.load(f);
		
		LOG.exiting(name, "load");
	}
	
	/**
	 * Loads this playlist object from an xml file
	 * 
	 * @param f
	 */
	public void load(File f) {
		LOG.entering(name, "load", "file: " + f);
		
		Element playlistE;
		try {
			playlistE = XmlHelper.readXmlFile(f);
			
			if (playlistE.getNodeName() == Playlist.DOC_ROOT) {
				playlistE = (Element) playlistE.getFirstChild();
				this.fromXml(playlistE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Error Loading Playlist File: " + e);
		}
		
		LOG.exiting(name, "load");
	}
	
	/**
	 * Adds another PlaylistEventListener to the ArrayList of listeners,
	 * they will be notified of changes through the callback with a 
	 * PlaylistEvent object
	 * 
	 * @param pel
	 * @return
	 */
	public boolean addPlaylistEventListener(PlaylistEventListener pel) {
		if (!this.playlistEventListeners.contains(pel)) {
			return this.playlistEventListeners.add(pel);
		} else return false;
	}
	
	/**
	 * Removes a PlaylistEventListener from the list
	 * 
	 * @param pel
	 * @return
	 */
	public boolean removePlaylistEventListener(PlaylistEventListener pel) {
		return this.playlistEventListeners.remove(pel);
	}
	
	/**
	 * Notifies all registered PlaylistEventListener's of some new event
	 * 
	 * @param pe
	 */
	private void notifyPEL(PlaylistEvent pe) {
		for (PlaylistEventListener pel: this.playlistEventListeners) {
			pel.playlistEvent(pe);
		}
	}
	
	private void startTimer() {
		int newTime = this.getEffectList().get(getCurrentIndex()).getPlayTime();
		this.timer.setTime(newTime);
		
		if (this.timer.isRunning()) {
			if (this.timer.isTiming()) {
				this.timer.stopTimer();
				try {
					Thread.sleep(this.timer.getDelay() * 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
					LOG.severe("Error stopping timer thread: " + e);
				}
			}

			this.timer.resetTimer();
			this.timer.startTimer();
		}
	}
}
