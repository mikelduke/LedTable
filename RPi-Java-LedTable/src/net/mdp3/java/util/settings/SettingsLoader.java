/**
 * 
 */
package net.mdp3.java.util.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import net.mdp3.java.util.test.SettingsTest;

/**
 * Settings
 * 
 * Java package used to make loading various settings from a text file onto an 
 * object easier.
 * 
 * To use instantiate this Settings class using either an Object or a Class 
 * definition, depending on whether the destination class is static or not. 
 * 
 * After loadSettings is called with either a File object or String file name,
 * the class looks up properties of the class that was passed in earlier, and 
 * attempts to load values from the text file onto it.
 * 
 * Supported property types are: int, long, double, bool, and String.
 * 
 * Settings Text File usage:
 * 	# or // denotes a comment line, lines beginning with these symbols are ignored
 * 	Key Value pairs should be denoted use property name: value using either : or =
 * 	Values are trimmed of whitespace
 * 		Example:
 * 			#this is an example
 * 			enableSomeSetting: true
 * 			someSettingValue = 500
 * 
 * @author Mikel
 * @see SettingsTest.java
 */
public class SettingsLoader {
	private HashMap<String, String> settingsMap = new HashMap<String, String>();
	private Object o = null;
	private Class<?> settingsClass;
	@SuppressWarnings("unused")
	private boolean staticB = true;
	private boolean debug = true;
	private String saveDivider = ":";
	
	public SettingsLoader(Class<?> c) {
		this(c, false);
	}
	
	public SettingsLoader(Class<?> c, boolean debug) {
		this(null, c, debug);
	}
	
	public SettingsLoader(Object o, boolean debug) {
		this(o, o.getClass(), debug);
	}
	
	public SettingsLoader(Object o, Class<?> c, boolean debug) {
		this.o = o;
		this.settingsClass = c;
		this.debug = debug;
		if (o == null) staticB = false;
		
		if (debug) {
			System.out.println("c: " + c);
			System.out.println("getClass: " + c.getClass().getSimpleName());
		}
	}
	
	public void loadSettings(String fileName) {
		File file = new File(fileName);
		loadSettings(file);
	}
	
	public void loadSettings(File file) {
		if (debug) System.out.println("loadSettings: " + file.getName());
		
		settingsMap.clear();
		
		try {
			Scanner sc = new Scanner(file);
			String input = "";
			
			while (sc.hasNext()) input += sc.nextLine() + "\n";
			if (debug) System.out.println(input);
			sc.close();
			
			loadToHashMap(input);
			hashMapToObject();
		} catch (FileNotFoundException e) {
			System.out.println("Settings File Not Found: " + file.getAbsolutePath());
			//e.printStackTrace();
		}
	}
	
	private void loadToHashMap(String str) {
		String line[] = str.split("\n");
		for (int i = 0; i < line.length; i++) {
			if (line[i].length() < 1) continue; 		//Skip blank lines
			if (line[i].charAt(0) == '#') {				//Skip lines starting with #
				if (debug) System.out.println(line[i]);
				continue;
			}
			if (line[i].startsWith("//")) {
				if (debug) System.out.println(line[i]);
				continue;
			}
			
			String sp[] = null;
			if (line[i].indexOf(':') > -1)
				sp = line[i].trim().split(":", 2);
			else if (line[i].indexOf('=') > -1)
				sp = line[i].trim().split("=", 2);
			else {
				System.out.println("Error loading line #" + i);
				continue;
			}
			
			if (sp.length < 2) continue;
			String key = sp[0].trim();
			String val = sp[1].trim();
			if (debug) System.out.println("Key: " + key + " Val: " + val);
			
			if (key.length() > 0 && val.length() > 0) {
				settingsMap.put(key, val);
			}
		}
	}
	
	private void hashMapToObject() {
		if (settingsMap.size() > 0) {
			Iterator<Entry<String, String>> it = settingsMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
				
				loadPairToObject(pairs.getKey(), pairs.getValue());
			}
		}
	}
	
	/**
	 * Currently supports parsing int, long, double, bool, and string types
	 * @param key
	 * @param val
	 */
	private void loadPairToObject(String key, String val) {
		try {
			Field f = settingsClass.getDeclaredField(key);
			f.setAccessible(true);
			
			if (debug) {
				System.out.println("Name: " + f.getName() + " FieldClass: " + f.getType());
			}
			
			try {
				if (f.getType().equals(int.class)) {
					if (o != null && !Modifier.isStatic(f.getModifiers())) {
						f.setInt(o, Integer.parseInt(val));
					} else if (Modifier.isStatic(f.getModifiers())) {
						f.setInt(null, Integer.parseInt(val));
					}
				} else if (f.getType().equals(long.class)) {
					if (o != null && !Modifier.isStatic(f.getModifiers())) {
						f.setLong(o, Long.parseLong(val));
					} else if (Modifier.isStatic(f.getModifiers())) {
						f.setLong(null, Long.parseLong(val));
					}
				} else if (f.getType().equals(double.class)) {
					if (o != null && !Modifier.isStatic(f.getModifiers())) {
						f.setDouble(o, Double.parseDouble(val));
					} else if (Modifier.isStatic(f.getModifiers())) {
						f.setDouble(null, Double.parseDouble(val));
					}
				} else if (f.getType().equals(boolean.class)) { 
					if (o != null && !Modifier.isStatic(f.getModifiers())) {
						f.setBoolean(o, val.trim().equalsIgnoreCase("true"));
					} else if (Modifier.isStatic(f.getModifiers())) {
						f.setBoolean(null, val.trim().equalsIgnoreCase("true"));
					}
				} else {
					if (o != null && !Modifier.isStatic(f.getModifiers())) {
						f.set(o, val);
					} else if (Modifier.isStatic(f.getModifiers())) {
						f.set(null, val);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				System.out.println("Access not permitted: " + e);
				e.printStackTrace();
			} 
		} catch (NoSuchFieldException e) {
			/*System.out.println("Error No Such Field " + key + "; " + e);
			e.printStackTrace();*/
		} catch (SecurityException e) {
			System.out.println("Error: Security Exception: " + e);
			e.printStackTrace();
		}
	}
	
	public void saveSettings(String fileName) {
		saveSettings(new File(fileName));
	}
	
	public void saveSettings(File file) {
		if (debug) System.out.println("Saving Settings to " + file.getAbsolutePath());
		
		String outString = "";
		outString += '#' + this.settingsClass.getSimpleName() + '\n';
		
		Field fields[] = this.settingsClass.getFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldStr = getSaveString(fields[i]);
			if (fieldStr != "") outString += fieldStr + '\n';
		}

		writeString(file, outString);
		
		if (debug) System.out.println("Done");
	}
	
	private void writeString(File file, String s) {
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(s);
			bw.close();
		} catch (IOException e) {
			System.out.println("Error saving file: " + e);
			e.printStackTrace();
		}
	}
	
	private String getSaveString(Field f) {
		String retStr = "";
		
		if (Modifier.isPublic(f.getModifiers())) {
			if (Modifier.isStatic(f.getModifiers())) {
				try {
					retStr += f.getName();
					retStr += saveDivider + " ";
					retStr += f.get(this.settingsClass);
				} catch (IllegalArgumentException e) {
					System.out.println("Illegal Argument: " + e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("Illegal Access: " + e);
					e.printStackTrace();
				}
			} else if (o != null) {
				try {
					retStr += f.getName();
					retStr += saveDivider + " ";
					retStr += f.get(o);
				} catch (IllegalArgumentException e) {
					System.out.println("Illegal Argument: " + e);
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.out.println("Illegal Access: " + e);
					e.printStackTrace();
				}
			}
		}
		return retStr;
	}
}
