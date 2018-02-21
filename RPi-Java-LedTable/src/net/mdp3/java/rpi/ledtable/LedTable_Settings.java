package net.mdp3.java.rpi.ledtable;

import java.util.logging.Logger;

import net.mdp3.java.util.settings.SettingsLoader;

/**
 * 
 * @author Mikel
 *
 * Class of static variables to hold the global settings
 * Has database, serial port, and table info
 */

public class LedTable_Settings {
	private final static Logger LOG = Logger.getLogger(LedTable_Settings.class.getName());
	private final static String name = "LedTable_Settings";
	
	//Default settings if not overwritten in settings.txt
	public static boolean enableGUI = true;
	public static int guiW = 504;
	public static int guiH = 336;
	public static boolean flipY = true;
	
	public static String wsName = "/ledtable";
	public static int wsPort = 85;
	
	public static boolean enableTableOutput = false;
	public static String tableMode = "SPI";
	public static String serialPort = "/dev/ttyACM0";
	public static int serialBaud = 57600;

	public static int spiSpeed = 1953000;
	
	public static String outputFile = "/dev/spidev0.0";
	
	public static int ledY = 8;
	public static int ledX = 12;
	public static boolean snakedLeds = true;
	
	public static boolean debug = true;
	
	public static long midiUpdateDelay = 100;
	
	public static String defaultSaveFolder = "";
	public static String defaultFilePrefix = "LedTable";
	
	public static boolean remoteWS = false;
	public static String remoteWSURL = "";
	
	public static boolean autoplaySelection = false;
	public static String defaultSelectionFile = "";
	
	public static boolean autoplayPlaylist = false;
	public static String defaultPlaylistFile = "";
	
	/**
	 * Loads the settings to the static variables using the mdp3_Util 
	 * Settings methods
	 * 
	 * Currently hardcoded to use filename of settings.txt
	 */
	public static void loadSettings() {
		loadSettings("settings.txt");
	}
	
	/**
	 * Loads the settings to the static variables using the mdp3_Util 
	 * Settings methods
	 * 
	 * @param fileName
	 */
	public static void loadSettings(String fileName) {
		LOG.entering(name, "loadSettings", "Filename: " + fileName);
		
		SettingsLoader set = new SettingsLoader(LedTable_Settings.class, false);
		
		//URL url = SettingsTest.class.getResource("SettingsTest.txt");
		//File file = new File(url.getPath());
		set.loadSettings(fileName);
		
		checkSettings();
		
		LOG.exiting(name, "loadSettings");
	}
	
	/**
	 * Method to run to bounds check variables after being loaded from file
	 */
	public static void checkSettings() {
		//Check bounds, must have atleast 1 row of each
		if (ledX < 1) {
			LOG.warning("Error in settings: ledX < 1; changing value to ledX = 1");
			ledX = 1;
		}
		
		if (ledY < 1) {
			LOG.warning("Error in settings: ledY < 1; changing value to ledY = 1");
			ledY = 1;
		}
	}
}
