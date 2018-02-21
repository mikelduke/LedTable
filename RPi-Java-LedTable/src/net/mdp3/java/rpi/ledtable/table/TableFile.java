package net.mdp3.java.rpi.ledtable.table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.mdp3.java.rpi.ledtable.LedTable_Settings;

public class TableFile extends Table {
	private final static String name = "LedTable_CMD";
	
	private File file = null;
	private FileOutputStream fos = null;
	
	public TableFile() {
		super();
		
		file = new File(LedTable_Settings.outputFile);
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
		
	public synchronized void write(byte[] bAr) {
		LOG.entering(TableFile.name, "write", bAr);
		//LOG.finest(LedTable_Util.bArToString(bAr));
		//long t = System.currentTimeMillis();
		
		if (LedTable_Settings.enableTableOutput) {
			try {
				if (fos != null) {
					fos.write(bAr);
				}
				Thread.sleep(10);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
		
		//LOG.finest("Write Time: " + (System.currentTimeMillis() - t));
		LOG.exiting(TableFile.name, "write", bAr.length + " bytes written");
	}
}
