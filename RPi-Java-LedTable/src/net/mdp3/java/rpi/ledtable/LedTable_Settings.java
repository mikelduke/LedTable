package net.mdp3.java.rpi.ledtable;

import net.mdp3.java.util.settings.Settings;

/**
 * 
 * @author Mikel
 *
 * Class of static variables to hold the global settings
 * Has database, serial port, and table info
 */

public class LedTable_Settings {
	
	//Default settings if not overwritten in settings.txt
	public static boolean enableGUI = true;
	public static int guiW = 504;
	public static int guiH = 336;
	public static boolean flipY = true;
	
	public static String wsName = "/ledtable";
	public static int wsPort = 85;
	
	public static boolean enableTableOutput = false;
	public static String serialPort = "/dev/ttyACM0";
	public static int serialBaud = 57600;
	public static int refreshTime = 1000; //ms
	
	public static int ledY = 8;
	public static int ledX = 12;
	public static boolean snakedLeds = true;
	
	public static boolean debug = true;
	
	public static long midiUpdateDelay = 100;
	
	public static void loadSettings() {
		Settings set = new Settings(LedTable_Settings.class, false);
		
		//URL url = SettingsTest.class.getResource("SettingsTest.txt");
		//File file = new File(url.getPath());
		set.loadSettings("settings.txt");
	}
}
