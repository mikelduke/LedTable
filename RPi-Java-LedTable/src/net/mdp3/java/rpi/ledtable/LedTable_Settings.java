package net.mdp3.java.rpi.ledtable;

/**
 * 
 * @author Mikel
 *
 * Class of static variables to hold the global settings
 * Has database, serial port, and table info
 */

public class LedTable_Settings {
	public static String databaseName = "led_table";
	public static String userName = "user";
	public static String userPass = "password";
	public static String databaseIP = "127.0.0.1";
	
	public static String serialPort = "/dev/ttyACM0";
	public static int serialBaud = 57600;
	public static int refreshTime = 1000; //ms
	
	public static int ledY = 8;
	public static int ledX = 12;
	
	public static boolean debug = true;
	
	public static void loadSettings() {
		
	}
}
