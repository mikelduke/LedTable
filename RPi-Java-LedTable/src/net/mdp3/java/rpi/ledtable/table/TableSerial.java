/**
 * 
 */
package net.mdp3.java.rpi.ledtable.table;

import net.mdp3.java.rpi.ledtable.LedTable_Settings;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

/**
 * @author Mikel
 *
 */
public class TableSerial extends Table {
	private final static String name = "TableSerial";
	private SerialDataListener sdl = null;
	
	public final Serial serial;
	private boolean connected = false;
	private String comPort = "";
	private int baud = 57600;
	
	public TableSerial() {
		super();
		serial = SerialFactory.createInstance();
		
		this.comPort = LedTable_Settings.serialPort;
		this.baud = LedTable_Settings.serialBaud;
		 
		 sdl = new SerialDataListener() {
			 @Override
		     public void dataReceived(SerialDataEvent event) {
				 // print out the data received to the console
		         LOG.info(event.getData());
		     }
		 };
		 
		 serial.addListener(sdl);
		 
		 this.connect();
	}
	
	public synchronized void write(byte[] bAr) {
		LOG.entering(name, "write", bAr);
		super.write(bAr);
		
		if (LedTable_Settings.enableTableOutput && connected) {
			serial.write(bAr);
		}
		
		//LOG.finest("Write Time: " + (System.currentTimeMillis() - t));
		LOG.exiting(name, "write", bAr.length + " bytes written");
	}
	
	/**
	 * Returns true if the connection to the serial port is successful
	 * Also returns true if the output is disabled so that debugging will work
	 * 
	 * @return connected status
	 */
	public boolean connect() {
		if (LedTable_Settings.enableTableOutput) {
			try {
				// open the default serial port provided on the GPIO header
				serial.open(comPort, baud);
				Thread.sleep(1000); //connect time
				connected = true;
				return true;
			} 
			catch(InterruptedException | SerialPortException e) {
				System.out.println("Exception in Serial: " + e);
				connected = false;
				return false;
			} 
		}
		return connected;
	}
	
	public void disconnect() {
		serial.close();
	}
	
	public boolean isConnected() {
		return connected;
	}
}
