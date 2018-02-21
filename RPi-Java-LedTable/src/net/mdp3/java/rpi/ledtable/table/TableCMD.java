package net.mdp3.java.rpi.ledtable.table;

import java.io.IOException;

import net.mdp3.java.rpi.ledtable.LedTable_Settings;

public class TableCMD extends Table {
	private final static String name = "LedTable_CMD";
		
	public synchronized void write(byte[] bAr) {
		LOG.entering(TableCMD.name, "write", bAr);
		//LOG.finest(LedTable_Util.bArToString(bAr));
		//long t = System.currentTimeMillis();
		
		if (LedTable_Settings.enableTableOutput) {
			try {
				String cmd = this.buildCmd(bAr);
				String cmdline[] = { "sh", "-c", cmd };
				Runtime.getRuntime().exec(cmdline);//.waitFor();
				
				Thread.sleep(10);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
		
		//LOG.finest("Write Time: " + (System.currentTimeMillis() - t));
		LOG.exiting(TableCMD.name, "write", bAr.length + " bytes written");
	}
	
	private String buildCmd(byte bAr[]) {
		LOG.entering(name, "buildCmd");
		
		String ret = "";
		String bytes = "";
		
		for (int i = 0; i < bAr.length; i++) {
			int val = (bAr[i] & 0xFF);
			String hex = Integer.toHexString(val).toUpperCase();
			while (hex.length() < 2) hex = '0' + hex;
			
			bytes += "\\x" + hex;
		}
		
		ret = "echo -ne \"" + bytes + "\" > /dev/spidev0.0";
		
		LOG.info(ret);
		LOG.exiting(name, "buildCmd", ret);
		return ret;
	}
}
