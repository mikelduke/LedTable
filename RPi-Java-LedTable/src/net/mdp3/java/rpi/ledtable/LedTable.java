package net.mdp3.java.rpi.ledtable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.mdp3.java.rpi.ledtable.effects.Effect;
import net.mdp3.java.rpi.ledtable.effects.EffectFactory;
import net.mdp3.java.rpi.ledtable.effects.EffectMode;
import net.mdp3.java.rpi.ledtable.effects.Effect_Util;
import net.mdp3.java.rpi.ledtable.effects.playlist.Playlist;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEvent;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEvent.PlaylistEventName;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEventListener;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistItem;
import net.mdp3.java.rpi.ledtable.gui.MainWindow;
import net.mdp3.java.rpi.ledtable.table.Table;
import net.mdp3.java.rpi.ledtable.table.TableFactory;
import net.mdp3.java.rpi.ledtable.table.TableFactory.TableOutputMode;
import net.mdp3.java.rpi.ledtable.webservice.WebserviceHandler;
import net.mdp3.java.util.webservice.WebserviceClient;


/**
 * 
 * @author Mikel
 *
 * LedTable Main Class
 * 
 */
public class LedTable implements PlaylistEventListener {
	private final static Logger LOG = Logger.getLogger(LedTable.class.getName());
	private final static String name = "LedTable";
	
	public Table table;
	private Effect effect = null;
	private LedTable_Selection currentSelection = null;
	
	private Playlist currentPlaylist = new Playlist("Unnamed");
	
	@SuppressWarnings("unused")
	private WebserviceHandler wsh;
	
	@SuppressWarnings("unused")
	private MainWindow ledTableGUI = null;
	
	private boolean canUseRemoteWS = true;
	private final static int wsVersion = 1;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LOG.info("LedTable Interface");
		
		try {
			File logFile = new File("logging.properties");
			if (logFile.exists()) {
				InputStream is;
				is = new FileInputStream(logFile);
				LogManager.getLogManager().readConfiguration(is);
			}
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		
		LedTable table;
		LedTable_Settings.loadSettings();
		
		table = new LedTable();
	}
	
	public LedTable() {
		try {
			table = TableFactory.getTable(TableOutputMode.valueOf(LedTable_Settings.tableMode));
		} catch (IOException e1) {
			e1.printStackTrace();
			
			if (LedTable_Settings.enableTableOutput) {
				LOG.info("Error Loading SPI. Either set table output to FALSE or install Pi4J");
				System.exit(0);
			}
		}
		
		if (table == null) {
			LOG.severe("Invalid Table Mode: " + LedTable_Settings.tableMode);
			System.exit(0);
		}
		
		wsh = new WebserviceHandler(this, LedTable_Settings.wsPort, LedTable_Settings.wsName);
		
		if (LedTable_Settings.enableGUI) {
			ledTableGUI = new MainWindow(this);
		}
		
		//Enable and start webservice if turned on in settings
		if (LedTable_Settings.remoteWS) {
			try {
				this.canUseRemoteWS = this.checkRemoteWS();
			} catch (IOException e) {
				e.printStackTrace();
				
				this.canUseRemoteWS = false;
			}
		}
		
		try {
			newSelection(new LedTable_Selection(EffectMode.PULSE1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//If autoplaySelection in settings file is true, then attempt to load and pick it
		if (LedTable_Settings.autoplaySelection) {
			File file = new File(LedTable_Settings.defaultSelectionFile);

			String fileName = file.getName();
			if (fileName.endsWith("xml")) {
				try {
					LedTable_Selection s = new LedTable_Selection();
					s.load(file);
					newSelection(s);
				} catch (Exception e) {
					e.printStackTrace();
					LOG.severe("Error loading defaultSelectionFile from settings.txt \n" + e);
				}
			}
		}
		
		this.getCurrentPlaylist().addPlaylistEventListener(this);
		if (LedTable_Settings.autoplayPlaylist && !LedTable_Settings.autoplaySelection) {
			File file = new File(LedTable_Settings.defaultPlaylistFile);

			String fileName = file.getName();
			if (fileName.endsWith("xml")) {
				try {
					this.getCurrentPlaylist().load(file);
					this.getCurrentPlaylist().play(); //TODO Implement playlist play before calling this
				} catch (Exception e) {
					e.printStackTrace();
					LOG.severe("Error loading defaultSelectionFile from settings.txt \n" + e);
				}
			}
		}
	}
	
	public Table getTable() {
		return this.table;
	}
	
	/**
	 * newSelection
	 * 
	 * Used to change the table mode
	 * 
	 * Called by webservice when a new selection occurs
	 * 
	 * Either outputs the selection with parameters over serial, or calls 
	 * other classes for more advanced stuff
	 * 
	 * @throws Exception 
	 */
	public String newSelection(LedTable_Selection s) throws Exception {
		LOG.entering(name, "newSelection", s);
		String ret = "";
		
		this.currentSelection = s;
		
		LOG.info("Handle Selection Mode: " + s.getMode());
		
		//TODO research implementing JavaScript for effect programming
		
		if (LedTable_Settings.remoteWS && LedTable_Settings.remoteWSURL.length() > 0 && this.canUseRemoteWS) {
			newRemoteSelection(s);
		}
		
		if (effect != null && effect.isRunning()) {
			effect.stopEffect();
			Thread.sleep(100);
		}
		
		effect = EffectFactory.getEffect(s.getMode(), table, s);
		
		if (effect != null) {
			effect.start();
		} else if (s.getMode() == EffectMode.OFF) { //FIXME Move OFF mode to class to get the EIP
			table.write(LedTable_Util.getRGBArray((byte)0, (byte)0, (byte)0));
		} else if (s.getMode() == EffectMode.IMAGE) { //image mode
			Effect_Util.showImage(table, s); //FIXME Move to Effect SubClass and set Effect Info 
		}
		
		LOG.exiting(name, "newSelection", s);
		return ret;
	}
	
	/**
	 * Saves the current selection mode and parameters
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void saveSelection() throws IOException, TransformerFactoryConfigurationError, TransformerException {
		LOG.entering(name, "saveSelection");
		
		if (this.currentSelection != null) this.currentSelection.save();
		
		LOG.exiting(name, "saveSelection");
	}
	
	public LedTable_Selection getCurrentSelection() {
		return this.currentSelection;
	}
	
	public Effect getEffect() {
		return this.effect;
	}
	
	public void quit() {
		//this is a safe selection for quitting - handleSelection will close the opened threads
		try {
			newSelection(new LedTable_Selection(EffectMode.OFF));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.info("Exiting LedTable");
		System.exit(0);
	}
	
	/**
	 * Sends the most recent mode selection to the remote URL
	 * Attempts to avoid an infinite loop by aborting the app if localhost 
	 * or 127.0.0.1 is specified.
	 * 
	 * @param s
	 * @throws Exception
	 */
	private void newRemoteSelection(LedTable_Selection s) throws Exception {
		String wsURL = LedTable_Settings.remoteWSURL + s.toWSCmd();
		LOG.fine(wsURL);
		
		URL url = new URL(wsURL);
		
		if (url.getHost().toLowerCase().contains("localhost") || url.getHost().contains("127.0.0.1")) {
			canUseRemoteWS = false;
			
			LOG.severe("Error: Do not connect back to the same app. It will cause an infinite loop." + "\n" + url.toExternalForm());
			quit();
		}
		
		URLConnection urlc = url.openConnection();
		urlc.connect();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlc.getInputStream()));
		
		String inputLine;
		String resp = "";
		while ((inputLine = in.readLine()) != null) { 
			resp += inputLine + '\n';
		}
		System.out.println("Response: " + resp);
		in.close();
	}
	
	/**
	 * Connects to the url set in the settings and checks to make sure its the 
	 * same version and can be used
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean checkRemoteWS() throws IOException {
		LOG.entering(name, "checkRemoteWS");
		
		boolean ret = true;
		
		String wsURL = LedTable_Settings.remoteWSURL;
		LOG.fine(wsURL);
		
		URL url = new URL(wsURL);
		
		if (url.getHost().toLowerCase().contains("localhost") || url.getHost().contains("127.0.0.1")) {
			LOG.severe("Error: Possible attempt to loopback to localhost");
			return false;
		}
		
		String resp = WebserviceClient.webserviceCall(url);
		LOG.finer("Webservice Call Response:\n" + resp);
		
		//Make sure web service version matches this one
		if (!resp.contains("Led Table Controller")) {
			LOG.warning("Remote Webservice Does not contain string 'Led Table Controller'");
			ret = false;
		} else if (!resp.contains("WS Version " + LedTable.wsVersion)) {
			LOG.warning("Remote Webservice Does not contain string 'WS Version " + LedTable.wsVersion + "'");
			ret = false;
		}
		
		LOG.finer("WS Check:"+ret);
		LOG.exiting(name, "checkRemoteWS", "ret=" + ret);
		return ret;
	}

	public Playlist getCurrentPlaylist() {
		return currentPlaylist;
	}

	public void setCurrentPlaylist(Playlist currentPlaylist) {
		this.currentPlaylist = currentPlaylist;
	}

	/**
	 * Listens for callbacks from the playlist containing the new selection to 
	 * use
	 */
	@Override
	public void playlistEvent(PlaylistEvent pev) {
		if (pev.getEventName() == PlaylistEventName.INDEX_CHANGED 
				&& pev.getEventValue() instanceof PlaylistItem) {
			PlaylistItem pi = (PlaylistItem) pev.getEventValue();
			
			try {
				this.newSelection(pi.getSelection());
			} catch (Exception e) {
				e.printStackTrace();
				LOG.severe("Error changing Selection in callback from Playlist Next " + e);
			}
		} else if (pev.getEventName() == PlaylistEventName.PLAYBACK_PLAY 
				&& pev.getEventValue() instanceof PlaylistItem) {
			PlaylistItem pi = (PlaylistItem) pev.getEventValue();
			
			try {
				this.newSelection(pi.getSelection());
			} catch (Exception e) {
				e.printStackTrace();
				LOG.severe("Error changing Selection in callback from Playlist Play " + e);
			}
		}
	}
}
