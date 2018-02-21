/**
 * 
 */
package net.mdp3.java.util.webservice;

import java.util.Map;

/**
 * This interface is used to define the callback for the Webservice Class.
 * 
 * @author Mikel
 *
 */
public interface WebserviceListener {
	
	/**
	 * Callback for calls to the Webservice
	 * 
	 * It gets the params in a hashmap, using the &param=value form and
	 * returns the String to be sent back to the browser 
	 * 
	 * Example url: http://localhost:80/context&parm1=aaa&parm2=bbb
	 * Would get passed a hashmap with 
	 * Key: parm1 Value: aaa
	 * Key: parm2 Value: bbb
	 * 
	 * @param params HashMap containing the parameters and values used in the URL
	 * @return String to be send back to the browser
	 */
	public abstract String wsAccess(Map<String, String> params);
}
