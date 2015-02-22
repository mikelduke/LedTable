package net.mdp3.java.rpi.ledtable;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

/**
 * 
 * @author Mikel
 *
 * LedTable_Serial
 * Class to talk to the serial port using Pi4J
 */

public class LedTable_Serial {
	public final Serial serial;
	
	private String comPort = "";
	private int baud = 57600;
	
	private int x = 0;
	private int y = 0;
	
	private boolean connected = false;
	
	public LedTable_Serial(String c, int b, int x, int y) {
		this(c, b, x, y, null);
	}
	
	public LedTable_Serial(String c, int b, int x, int y, SerialDataListener sdl) {
		 serial = SerialFactory.createInstance();
		 
		 this.comPort = c;
		 this.baud = b;
		 this.x = x;
		 this.y = y;
		 
		 if (sdl == null) { // anonymous method in case another listener is not used
			 sdl = new SerialDataListener() {
				 @Override
			     public void dataReceived(SerialDataEvent event) {
					 // print out the data received to the console
			         System.out.print(event.getData());
			     }
			 };
		 }
		 
		 serial.addListener(sdl);
		 
		 this.connect();
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
			catch(InterruptedException e) {
				System.out.println("Thread Interrupted: " + e);
				connected = false;
				return false;
			} 
			catch(SerialPortException ex) {
				System.out.println("SERIAL SETUP FAILED : " + ex);
				connected = false;
				return false;
			}
		} else { //table output not enabled, use debug
			connected = true;
		}
		return connected;
	}
	
	public void disconnect() {
		serial.close();
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void writeByte(byte b) {
		if (LedTable_Settings.debug) System.out.println("Serial.writeByte: " + b);
		
		if (LedTable_Settings.enableTableOutput && connected) {
			serial.write(b);
		}
	}
	
	public void writeByteAr(byte bAr[]) {
		if (LedTable_Settings.debug) System.out.println("Serial.writeByteAr: " + bAr.toString());
		
		if (LedTable_Settings.enableTableOutput && connected) {
			serial.write(bAr);
		}
	}
	
	public void writeChar(char c) {
		if (LedTable_Settings.debug) System.out.println("Serial.writeChar: " + c);

		if (LedTable_Settings.enableTableOutput && connected) {
			serial.write(c);
		}
	}
	
	public void writeCharAr(char cAr[]) {
		if (LedTable_Settings.debug) System.out.println("Serial.writeCharAr: " + cAr.toString());

		if (LedTable_Settings.enableTableOutput && connected) {
			serial.write(cAr);
		}
	}
	
	public void testSend() {
		if (LedTable_Settings.debug) System.out.println("Serial.testSend");
		
		while (true) {
			for (int i = 2; i < 255; i++) {
				System.out.println("Line: " + i);
				byte[] byteAr = new byte[x * y * 3 + 1];
				byteAr[0] = (byte)'4';
				for (int j = 1; j < byteAr.length; j++) {
					byteAr[j] = (byte)i;
				}
				try {
				    //System.out.println(val);
		            serial.write(byteAr);
				    Thread.sleep(15);
		    	} 
				catch(InterruptedException e) {
					System.out.println("Thread Interrupted: " + e);
				} 
				catch(IllegalStateException ex) {
		        	ex.printStackTrace();
		    	}
			}
		}
	}
}
