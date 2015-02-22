/**
 * 
 */
package net.mdp3.java.rpi.ledtable;

import java.io.IOException;
import java.net.BindException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.mdp3.java.util.webservice.Webservice;
import net.mdp3.java.util.webservice.WebserviceListener;

/**
 * @author Mikel
 *
 */
public class WebserviceHandler implements WebserviceListener {
	Webservice ws;
	LedTable table;
	
	public WebserviceHandler(LedTable table, int port, String name) {
		this.table = table;
		
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
	public String wsAccess(HashMap<String, String> params) {
		String returnValue = "";
		
		if (LedTable_Settings.debug) {
			if (params.size() > 0) {
				returnValue = "Has Params! " + params.size() + '\n';
				Iterator<Entry<String, String>> it = params.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
					returnValue += "Key: " + pairs.getKey() + " = " + pairs.getValue() + '\n';
					//it.remove();
				}
			}
			else returnValue = "Invalid Params!";
		}
		System.out.println("RetVal: \n" + returnValue);
		
		parseParams(params);
		
		return returnValue;
	}
	
	private void parseParams(HashMap<String,String> params) {
		int mode = 2;
		String modeStr = params.get("mode");
		if (modeStr.length() > 0) mode = new Integer(modeStr).intValue();
		
		String parm1 = params.get("param1");
		String parm2 = params.get("param2");
		String parm3 = params.get("param3");
		String parm4 = params.get("param4");
		String parm5 = params.get("param5");
		byte parm5bAr[] = null;
		if (parm5 != null && parm5.length() > 0) parm5bAr = parm5.getBytes();
		
		table.newSelection(new LedTable_Selection(mode, parm1, parm2, parm3, parm4, parm5bAr));
	}
}
