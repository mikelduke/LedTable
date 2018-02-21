/**
 * 
 */
package net.mdp3.java.util.test;

import java.io.IOException;
import java.net.BindException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.mdp3.java.util.webservice.Webservice;
import net.mdp3.java.util.webservice.WebserviceListener;

/**
 * Test/Example class for using the Webservice class
 * Creates a webserver at localhost:83/test which returns the URL parameters 
 * to the page
 *  
 * @author Mikel
 *
 */
public class WebserviceTest implements WebserviceListener {
	
	Webservice ws;
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		WebserviceTest test = new WebserviceTest(83, "/test");
	}
	
	public WebserviceTest(int port, String name) {
		try {
			ws = new Webservice(port, name, this);
		} catch (BindException e) {
			System.out.println("Error opening port: " + port);
		} catch (IOException e) {
			System.out.println("Error starting webservice: " + e);
			e.printStackTrace();
		}
		
	}


	/**
	 * wsAccess Override Required by the WebserviceListener interface
	 * 
	 * It gets passed a HashMap containing the parameters and values used in 
	 * the Webservice URL.
	 * 
	 * @param params HashMap containing the parameters and values used in the URL
	 * @return String with the parameters and values from the URL
	 */
	@Override
	public String wsAccess(Map<String, String> params) {
		String returnValue = "";
		
		if (params.size() > 0) {
			returnValue = "Has Params! " + params.size() + '\n';
			Iterator<Entry<String, String>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
				returnValue += "Key: " + pairs.getKey() + " = " + pairs.getValue() + '\n';
			}
		}
		else returnValue = "Invalid Params!";
		System.out.println("RetVal: " + returnValue);
		
		return returnValue;
	}

}
