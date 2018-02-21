/**
 * 
 */
package net.mdp3.java.util.console;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.mdp3.java.util.misc.ObjectNotInitializedException;

/**
 * Attempts to map console arguments to methods on the specified object and 
 * call them automatically with a single value parameter
 * 
 * @author Mikel
 *
 */
public class ConsoleArgumentHandler {
	private final static Logger LOG = Logger.getLogger(ConsoleArgumentHandler.class.getName());
	private final static String name = "ConsoleArgumentHandler";

	private Object objectToInit;
	private List<String> argList;
	private HashMap<String, String> shortCutMap = new HashMap<String, String>();
	private String cmdDelim = "=";
	private List<String> cmdPrefixList;
	private ConsoleReader cr = null;
	
	private static final String[] DEFAULT_PREFIX_LIST = {
		"--",
		"-",
		"/"
	};
	
	/**
	 * Creates an empty ConsoleArgumentHandler that will need setup by the 
	 * set methods
	 */
	public ConsoleArgumentHandler() {
		this(null, null);
	}
	
	/**
	 * Creates a ConsoleArgumentHandler for the specified object using the 
	 * default prefixes, = sign delim, and no shortcut mappings
	 * 
	 * @param o
	 * @param args
	 */
	public ConsoleArgumentHandler(Object o, String[] args) {
		this(o, args, "=", null);
	}
	
	/**
	 * Creates a ConsoleArgumentHandler for the specified object using the set 
	 * delim, set prefix list, and no shortcut mappings
	 * 
	 * @param o
	 * @param args
	 * @param delim
	 * @param prefix
	 */
	public ConsoleArgumentHandler(Object o, String[] args, String delim, String[] prefix) {
		this(o, args, null, delim, prefix);
	}
	
	/**
	 * Creates a ConsoleArgumentHandler with the specified object, short cut 
	 * map, delim, prefix list, and arguements
	 * @param o
	 * @param args
	 * @param shortCutMap
	 * @param delim
	 * @param prefix
	 */
	public ConsoleArgumentHandler(Object o, String[] args, HashMap<String, String> shortCutMap, String delim, String[] prefix) {
		this.setObjectToInit(o);
		this.setArgs(args);
		this.setShortCutMap(shortCutMap);
		this.setDelim(delim);
		this.setPrefixList(prefix);
	}
	
	/**
	 * Sets the object to run methods on
	 * @param o
	 */
	public void setObjectToInit(Object o) {
		this.objectToInit = o;
	}
	
	/**
	 * Array of Strings from the command line parameters, this can be passed 
	 * in from the main(String[] args) method
	 * 
	 * @param args
	 */
	public void setArgs(String[] args) {
		this.argList = Arrays.asList(args);
	}
	
	/**
	 * Sets the arg list using a List instead of String array
	 * 
	 * @param argList
	 */
	public void setArgs(List<String> argList) {
		this.argList = argList;
	}
	
	/**
	 * Sets the shortcut map for mapping eg "f" to "file"
	 * Example: sets -f= to --file=
	 * 
	 * @param map
	 */
	public void setShortCutMap(HashMap<String, String> map) {
		this.shortCutMap = map;
	}
	
	/**
	 * Adds a new shortcut to the shortcut map
	 * 
	 * @param abbr Abbreviation to look for in the argument list
	 * @param methodName method name to call in objectToInit
	 * @return true if the method exists and the shortcut was added to the map
	 * @throws NoSuchMethodException If the method was not found
	 */
	public boolean addShortcut(String abbr, String methodName) throws NoSuchMethodException {
		if (this.checkIfMethodExists(methodName)) {
			this.shortCutMap.put(abbr, methodName);
			return true;
		} else {
			throw new NoSuchMethodException("Method '" + methodName + "' Not found on " + this.objectToInit);
		}
	}
	
	public HashMap<String, String> getShortcutMap() {
		return this.shortCutMap;
	}
	
	/**
	 * Checks the objectToInit for a method with methodName that takes 0 
	 * or 1 String parameter
	 * 
	 * @param methodName Method name to look for
	 * @return true if the method name exists and takes 0 or 1 String parameter
	 */
	private boolean checkIfMethodExists(String methodName) {
		Method oMethods[] = objectToInit.getClass().getMethods();
		for (Method m : oMethods) {
			if (m.getName().equalsIgnoreCase(methodName)) {
				LOG.fine("Method " + m.getName() + " Found");
				
				Class<?> paramTypes[] = m.getParameterTypes();
				if (paramTypes.length == 0) return true;
				if (paramTypes.length == 1) {
					LOG.fine("paramType: " + paramTypes[0].getName());
					
					if (paramTypes[0].getName().equals("java.lang.String")) return true;
					else return false;
				}
				if (paramTypes.length > 1) return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Generates shortcuts for setter methods, shortcuts will be same name 
	 * without set and if a first letter shortcut does not already exists, 
	 * then one will be added too
	 */
	public void autoCreateShortCuts() {
		Method[] methods = this.objectToInit.getClass().getMethods();
		
		for (Method m : methods) {
			if (m.getName().toLowerCase().startsWith("set")) {
				Class<?> paramTypes[] = m.getParameterTypes();
				
				if (paramTypes.length == 0 || (paramTypes.length == 1 && paramTypes[0].getName().equals("java.lang.String"))) {
					String key = m.getName().substring("set".length(), m.getName().length());
					if (key.length() > 0) {
						this.shortCutMap.put(key.toLowerCase(), m.getName());
						
						//Add first letter shortcut if its not on the map yet
						if (!this.shortCutMap.containsKey(key.substring(0, 1))) {
							this.shortCutMap.put(key.toLowerCase().substring(0, 1), m.getName());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Clears out the shortcut map
	 */
	public void clearShortcuts() {
		this.shortCutMap.clear();
	}
	
	/**
	 * Sets the delim between the argument command and value, most likely =
	 * 
	 * @param delim
	 */
	public void setDelim(String delim) {
		this.cmdDelim = delim;
	}
	
	/**
	 * Sets the prefix list to be used, if null then the default list is used. 
	 * These are the characters that will be removed to find the command or 
	 * method name.
	 * 
	 * @param prefixAr
	 */
	public void setPrefixList(String[] prefixAr) {
		if (prefixAr != null && prefixAr.length > 0)
			this.cmdPrefixList = Arrays.asList(prefixAr);
		else this.cmdPrefixList = Arrays.asList(DEFAULT_PREFIX_LIST);
	}
	
	/**
	 * Adds another prefix to the list, helpful if you want to use the default 
	 * list and an extra item or two
	 * 
	 * @param prefix
	 */
	public void addPrefix(String prefix) {
		this.cmdPrefixList.add(prefix);
	}
	
	/**
	 * Runs the argument runner against the object, returns a list of failed 
	 * arguments from the argList. A failure is when the command cannot be 
	 * found in the arg, not when the command doesnt exist on the object.
	 * 
	 * @return List<String> of failed arguments
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws ObjectNotInitializedException 
	 */
	public List<String> runArgs() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ObjectNotInitializedException {
		ArrayList<String> failedCommands = new ArrayList<String>();
		
		if (this.objectToInit == null) throw new ObjectNotInitializedException("No Object set to run commands against. Run the setIbjectToInit method");
		if (this.argList == null || this.argList.size() == 0) return failedCommands;
		
		cr = new ConsoleReader("ArgRunner", this.objectToInit, false);
		
		for (int i = 0; i < argList.size(); i++) {
			boolean argRun = this.runArg(argList.get(i));
			if (!argRun) failedCommands.add(argList.get(i));
		}
		
		return failedCommands;
	}

	/**
	 * Attempts to break up and run a single arguement from the list
	 * 
	 * @param arg
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private boolean runArg(String arg) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String argCommand = "";
		String argValue = "";
		String cmd = "";
		
		argCommand = getCommand(arg);
		argValue = getValue(arg);
		
		if (argCommand == "") return false;
		cmd = argCommand;
		
		if (argValue != "") cmd = argCommand + " " + argValue;
		LOG.info("Running Command: " + cmd);
		return cr.parseInput(cmd);
	}
	
	/**
	 * Gets the command name from the argument list and maps it using the 
	 * short cut map.
	 * 
	 * @param arg
	 * @return
	 */
	private String getCommand(String arg) {
		LOG.entering(name, "getCommand" + "arg: " + arg);
		
		String cmd = arg.trim();
		LOG.finer(cmd);
		
		//First remove the prefix
		for (String prefix: this.cmdPrefixList) {
			if (arg.startsWith(prefix)) {
				cmd = cmd.substring(prefix.length(), cmd.length());
				break;
			}
		}
		LOG.finer(cmd);
		
		//Remove value
		int delimPos = cmd.indexOf(this.cmdDelim);
		if (delimPos > -1) {
			cmd = cmd.substring(0, delimPos);
		}
		LOG.finer(cmd);
		
		//Map to commands in map list
		if (this.shortCutMap != null) {
			for (Map.Entry<String, String> entry: this.shortCutMap.entrySet()) {
				if (cmd.equalsIgnoreCase(entry.getKey())) {
					cmd = entry.getValue();
					break;
				}
			}
		}
		LOG.finer(cmd);
		
		LOG.exiting(name, "getCommand", cmd);
		return cmd;
	}
	
	/**
	 * Gets the value for the command from the argument, returns the string 
	 * after the first delim
	 * 
	 * @param arg
	 * @return
	 */
	private String getValue(String arg) {
		LOG.entering(name, "getValue", arg);
		
		String val = arg.trim();
		
		int delimPos = val.indexOf(this.cmdDelim);
		if (delimPos > -1) {
			val = val.substring(delimPos +1, val.length());
		} else {
			val = "";
		}
		//LOG.finer(val);
		
		LOG.exiting(name, "getValue", val);
		return val;
	}
}
