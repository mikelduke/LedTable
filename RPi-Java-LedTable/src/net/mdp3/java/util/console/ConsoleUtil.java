package net.mdp3.java.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Class to hold Util functions for doing things in console mode in an app
 * 
 * 
 * @author Mikel
 *
 */
public class ConsoleUtil {
	private static Process p;
	
	/**
	 * Runs the specified cmd string as a shell command, if wait = true, then 
	 * this method will block until the command is finished
	 * 
	 * @param cmd
	 * @param wait
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String execute(String cmd, boolean wait) throws IOException, InterruptedException {
		return execute(new String[]{ cmd }, wait);
	}
	
	/**
	 * Combines the string array into a command and runs it as a shell command,
	 * if wait = true then this method will block
	 * 
	 * @param cmd
	 * @param wait
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String execute(String cmd[], boolean wait) throws IOException, InterruptedException {
		StringBuffer output = new StringBuffer();
		p = Runtime.getRuntime().exec(cmd);
		if (wait) {
			p.waitFor();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			
			return output.toString();
		} else return "";
	}
	
	/**
	 * Returns true if any String item in the argList starts with the 
	 * argName parameter. This is useful to check for the presence of 
	 * a specific command like argument.
	 * 
	 * @param argList
	 * @param argName
	 * @return
	 */
	public static boolean hasArg(List<String> argList, String argName) {
		return hasArg(argList, argName, "");
	}
	
	/**
	 * Returns true if any String item in the argList starts with the 
	 * argName parameter. This is useful to check for the presence of 
	 * a specific command like argument. This method also allows for the 
	 * delimeter to be specified which gets appended to the argName,
	 * ie to search for --file=blah then = is the delim.
	 * 
	 * @param argList
	 * @param argName
	 * @param delim
	 * @return
	 */
	public static boolean hasArg(List<String> argList, String argName, String delim) {
		for (int i = 0; i < argList.size(); i++) {
			if (argList.get(i).startsWith(argName + delim)) return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the first item in the argList that starts with the string 
	 * argName not including the arg.
	 * 
	 * @param argList
	 * @param argName
	 * @return
	 */
	public static String getArgStartsWith(List<String> argList, String argName) {
		return getArgStartsWith(argList, argName, "");
	}
	
	/**
	 * Returns the first item in the argList that starts with the String 
	 * argName + delim, not including the arg.
	 * 
	 * Example: If argList contains --file=blah, then if using 
	 * argName:--file and delim:= would return blah.
	 * 
	 * @param argList
	 * @param argName
	 * @param delim
	 * @return
	 */
	public static String getArgStartsWith(List<String> argList, String argName, String delim) {
		String ret = "";
		
		for (int i = 0; i < argList.size(); i++) {
			if (argList.get(i).startsWith(argName + delim)) {
				String arg = argList.get(i);
				return arg.substring(argName.length() + delim.length(), arg.length());
			}
		}
		
		return ret;
	}
}
