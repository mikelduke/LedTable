/**
 * 
 */
package net.mdp3.java.rpi.ledtable.webservice;

import java.io.IOException;
import java.net.BindException;
import java.util.Map;
import java.util.logging.Logger;

import net.mdp3.java.rpi.ledtable.LedTable;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.util.webservice.Webservice;
import net.mdp3.java.util.webservice.WebserviceListener;

/**
 * @author Mikel
 *
 */
public class WebserviceHandler implements WebserviceListener {
	private final static Logger LOG = Logger.getLogger(WebserviceHandler.class.getName());
	private final static String name = "WebserviceHandler";
	
	Webservice ws;
	
	public WebserviceHandler(LedTable table, int port, String name) {
		WebserviceFunctions.setLedTable(table);
		
		boolean started = false;
		try {
			ws = new Webservice(port, name, this);
			started = true;
		} catch (BindException e) {
			System.out.println("Error opening port: " + port);
		} catch (IOException e) {
			System.out.println("Error starting webservice: " + e);
			e.printStackTrace();
		}
		
		if (LedTable_Settings.debug && started) System.out.println("Webservice started on port: " + port + " for Context: " + name);
	}


	@Override
	public String wsAccess(Map<String, String> params) {
		LOG.entering(name, "wsAccess", "params: " + params);
		
		String returnValue = "";

		//Parameters are send, so send back link to homepage and process the params
		if (params.size() > 0) {
			try {
				returnValue += WebserviceFunctions.parseParams(params);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.severe("Error in Webservice: " + e);
			}
		} else { //No parameters sent so send landing page
			returnValue += WebserviceFunctions.serveFiles();
		}
		
		LOG.exiting(name, "wsAccess", "returnValue: " + returnValue);
		return returnValue;
	}
}
