/**
 * 
 */
package net.mdp3.java.rpi.ledtable.table;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Mikel
 *
 */
public abstract class Table {
	protected final static Logger LOG = Logger.getLogger(Table.class.getName());
	private final static String name = "Table";
	
	private List<WriteListener> listeners = new ArrayList<WriteListener>();
	
	protected byte[] lastWrite = null;
	
	public synchronized void write(byte bAr[]) {
		this.lastWrite = bAr;
		for (WriteListener l : listeners) l.writeEvent(bAr);
	}
	
	public void addWriteListener(WriteListener listener) {
		LOG.entering(name, "addWriteListener", listener);
		
		if (!this.listeners.contains(listener))
			this.listeners.add(listener);
		
		LOG.exiting(name, "addWriteListener");
	}
	
	public byte[] getLastWrite() {
		return this.lastWrite;
	}
}
