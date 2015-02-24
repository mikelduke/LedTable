/**
 * 
 */
package net.mdp3.java.rpi.ledtable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.mdp3.java.rpi.ledtable.LedTable_Selection.Mode;
import net.mdp3.java.util.file.SimpleFileIO;
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
			} else {
				try {
					File f = new File("LedTable.html");
					returnValue = SimpleFileIO.loadFileToString(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (params.size() > 0) {
				returnValue = "OK";
			} else {
				try {
					File f = new File("LedTable.html");
					returnValue = SimpleFileIO.loadFileToString(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("RetVal: \n" + returnValue);
		
		if (params.size() > 0) {
			returnValue += parseParams(params);
		}
		
		return returnValue;
	}
	
	private String parseParams(HashMap<String,String> params) {
		String ret = "";
		Mode mode = Mode.PULSE1; //default mode for bad params
		
		//read mode parameter
		if (params.get("mode") != null) {
			try {
				mode = Mode.valueOf(params.get("mode"));
				table.newSelection(new LedTable_Selection(mode, params));
			} catch (Exception e) {
				System.out.println("Invalid Mode");
				ret = "Invalid Mode";
			}
		}
		
		return ret;
	}
}
