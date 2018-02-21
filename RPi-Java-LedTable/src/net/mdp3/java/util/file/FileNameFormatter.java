/**
 * 
 */
package net.mdp3.java.util.file;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mikel
 *
 */
public class FileNameFormatter {
	
	/**
	 * Common function to get the date string back in a format I like
	 * 
	 * Example: 20151021153304
	 * 
	 * @return Date/Time as yyyyMMddHHmmss
	 */
	public static String getDateString() {
		String ret = "";
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		ret += dateFormat.format(date);
		
		return ret;
	}
	
	/**
	 * Returns a Dated file name using the specified prefix and extension with 
	 * the separator after the prefix and before the date
	 * 
	 * Example: prefix-20151021132402.txt
	 * 
	 * @param prefix
	 * @param separator
	 * @param extension Not including the .
	 * @return
	 */
	public static String getDatedFileName(String prefix, String separator, String extension) {
		String ret = "";
		
		ret += prefix;
		ret += separator;
		ret += FileNameFormatter.getDateString();
		ret += '.';
		ret += extension;
		
		return ret;
	}
	
	/**
	 * Returns a Dated file name using the specified prefix and extension with 
	 * a - after the prefix and before the date
	 * 
	 * Example: prefix-20151021132402.txt
	 *  
	 * @param prefix
	 * @param extension Not including the .
	 * @return
	 */
	public static String getDatedFileName(String prefix, String extension) {
		return FileNameFormatter.getDatedFileName(prefix, "-", extension);
	}
	
	public static String getExtension(String fileName) {
		String ret = "";
		
		int lastDot = fileName.lastIndexOf(".");
		
		if (lastDot < fileName.length())
			ret = fileName.substring(lastDot + 1, fileName.length());
		
		return ret;
	}
}
