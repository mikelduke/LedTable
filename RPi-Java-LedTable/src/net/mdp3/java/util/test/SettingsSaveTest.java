/**
 * 
 */
package net.mdp3.java.util.test;

import java.io.File;

import net.mdp3.java.util.settings.SettingsLoader;

/**
 * @author Mikel
 *
 */
public class SettingsSaveTest {
	public static int staticIntParm = 0;
	public static String staticStringParm = "STATIC";
	public static boolean staticBoolParm = false;
	public static long staticLongParm = 123456789;
	public static double staticDoubleParm = 1.55;
	
	public int intParm = 0;
	public String stringParm = "String";
	public boolean boolParm = false;
	public long longParm = 123456789;
	public double doubleParm = 1.55;
	public int newVal = 100;
	
	public static void main(String[] args) {
		System.out.println("Static Test");
		SettingsLoader set = new SettingsLoader(SettingsSaveTest.class, true);
		
		File file = new File("SettingsStaticSaveTest.txt");
		set.saveSettings(file);

		System.out.println("Object Test");
		@SuppressWarnings("unused")
		SettingsSaveTest sst = new SettingsSaveTest();
	}
	
	public SettingsSaveTest() {
		SettingsLoader set = new SettingsLoader(this, true);
		
		File file = new File("SettingsSaveTest.txt");
		set.saveSettings(file);
	}
}
