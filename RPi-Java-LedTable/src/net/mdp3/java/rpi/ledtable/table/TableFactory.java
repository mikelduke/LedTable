/**
 * 
 */
package net.mdp3.java.rpi.ledtable.table;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Mikel
 *
 */
public class TableFactory {
	private final static Logger LOG = Logger.getLogger(TableFactory.class.getName());
	private final static String NAME = "TableFactory";
	
	public static enum TableOutputMode {
		SPI,
		CMD,
		FILE,
		SERIAL
	}

	public static Table getTable(TableOutputMode tableMode) throws IOException {
		LOG.entering(NAME, "getTable", "mode: " + tableMode);
		
		LOG.info("Creating Table to " + tableMode);
		
		Table table = null;

		if (tableMode == TableOutputMode.SPI) {
			table = new TableSPI();
		} else if (tableMode == TableOutputMode.CMD) {
			table = new TableCMD();
		} else if (tableMode == TableOutputMode.FILE) {
			table = new TableFile();
		} else if (tableMode == TableOutputMode.SERIAL) {
			table = new TableSerial();
		} else {
			LOG.info("Invalid Table Mode: " + tableMode);
		}
		
		LOG.exiting(NAME, "getTable", "table: " + table);
		
		return table;
	}
}
