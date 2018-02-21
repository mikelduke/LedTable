/**
 * 
 */
package net.mdp3.java.util.settings;


/**
 * @author Mikel
 *
 */
public abstract class Settings {
	/**
	 * Loads the settings to the static variables using the mdp3_Util 
	 * Settings methods
	 * 
	 * Currently hardcoded to use filename of settings.txt
	 */
	public static void loadSettings(Class<?> clazz) {
		loadSettings(clazz, "settings.txt");
	}
	
	/**
	 * Loads the settings to the static variables using the mdp3_Util 
	 * Settings methods
	 * 
	 * @param class
	 * @param fileName
	 */
	public static void loadSettings(Class<?> clazz, String fileName) {
		SettingsLoader set = new SettingsLoader(clazz, false);
		
		set.loadSettings(fileName);
	}
}
