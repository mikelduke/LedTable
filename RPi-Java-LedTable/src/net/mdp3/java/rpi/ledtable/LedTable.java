package net.mdp3.java.rpi.ledtable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;


/**
 * 
 * @author Mikel
 *
 * LedTable Main Class
 * 
 */
public class LedTable extends Thread {
	public static LedTable_Serial tableSerial;
	private LedTable_SQL sql;
	private LedTable_Selection prevSelection = null;
	private LedTable_Selection lastSelection;
	private LedTable_Animation animation = null;
	//private LedTable_Modes[] modes;
	
	private boolean run = false;
	private boolean runAnimation = false;

	public static void main(String[] args) {
		LedTable table;
		LedTable_Settings.loadSettings();
		
		System.out.println("LedTable Serial Interface");
		
		table = new LedTable();
		table.start();
	}
	
	public LedTable() {
		tableSerial = new LedTable_Serial(LedTable_Settings.serialPort, 
				LedTable_Settings.serialBaud, LedTable_Settings.ledX, LedTable_Settings.ledY);
		sql = new LedTable_SQL(LedTable_Settings.databaseIP, LedTable_Settings.userName, LedTable_Settings.userPass, LedTable_Settings.databaseName);
		sql.connect();
	}
	
	/**
	 * run
	 * 
	 * Main thread loop for LedTable
	 * This method checks the database for changes and then calls the handler when there is a new selection
	 */
	public void run() {
		run = true;
		//wait for stuff to connect and arduino to reset
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {
			System.out.println("Error Sleeping Thread: " + e1);
			e1.printStackTrace();
		}
		
		tableSerial.writeChar('2');
		
		//Main Loop
		while (run) {
			try {
				if (LedTable_Settings.debug) System.out.println("Checking db");
				if (sql.isConnected()) {
					lastSelection = sql.getLastSelection();
					if (LedTable_Settings.debug) System.out.println("Last Selection: " +lastSelection.toString());
					if (!lastSelection.equals(prevSelection)) {
						//new selection, parse it and send output to serial
						handleSelection();
					}
					prevSelection = lastSelection;
				}
				else sql.connect();
				
				Thread.sleep(LedTable_Settings.refreshTime);
			}
			catch (InterruptedException e) {
				System.out.println("Error Sleeping LedTable.java " + e);
			}
		}
	}
	
	/**
	 * handleSelection
	 * 
	 * Called in thread when a new selection is detected in the database
	 * This method is the switch to what do it, it will either make new serial output,
	 * or launch a new thread for serial output
	 */
	private void handleSelection() {
		if (LedTable_Settings.debug) System.out.println("Handle Selection Mode: " + lastSelection.getMode());
		if (tableSerial.isConnected()) {
			int mode = lastSelection.getMode();
			char c = new String(mode + "").charAt(0);
			
			if (mode != 8 && runAnimation) {
				runAnimation = false;
				animation.stopAnimation();
				try {
					Thread.sleep(animation.getDelay() * 2);
				} catch (InterruptedException e) {
					System.out.println("Error waiting for animation to finish " + e);
				}
				animation = null;
			}
			
			if (mode == 0 || mode == 1 || mode == 2 || mode == 3 || mode == 6) { //arduino demo modes
				tableSerial.writeChar(c);
			}
			else if (mode == 4) { //frame byte ar write - launch rpi method to output to table
				
			}
			else if (mode == 5) { //set table color
				//not parsing correctly
				byte bAr[] = new byte[4];
				
				bAr[0] = (byte)c;
				bAr[1] = new Integer(Integer.parseInt(lastSelection.getParm1(), 16)).byteValue();
				bAr[2] = new Integer(Integer.parseInt(lastSelection.getParm2(), 16)).byteValue();
				bAr[3] = new Integer(Integer.parseInt(lastSelection.getParm3(), 16)).byteValue();
				
				if (LedTable_Settings.debug) {
					System.out.print("Color selection byte ar: ");
					for (int i = 0; i < bAr.length; i++)
						System.out.print(bAr[i] + ", ");
					System.out.println("");
				}
				
				tableSerial.writeByteAr(bAr);
			}
			else if (mode == 7) { //image mode
				showImage(lastSelection.getParm1());
			}
			else if (mode == 8) {
				handleAnimations();
			}
			else {
				System.out.println("Error: Invalid Mode");
			}
		}
	}
	
	private void handleAnimations() {
		runAnimation = true;
		animation = new LedTable_Animation(lastSelection);
		animation.start();
	}
	
	public static void showImage(String imgPath) {
		try {
			byte tableAr[] = new byte[LedTable_Settings.ledX * LedTable_Settings.ledY * 3 + 1];
			tableAr[0] = (byte)'4';
			
			URL fileURL = new File(imgPath).toURI().toURL();
			BufferedImage img = ImageIO.read(fileURL);
			
			int cell = 1;
			
			/*if (LedTable_Settings.debug) {
				System.out.println("Image size x: " + img.getWidth() + " y: " + img.getHeight());
			}*/
			for (int y = 0; y < img.getHeight(); y++) {
				if (y % 2 == 0) {
					for (int x = 0; x < img.getWidth(); x++) {
						int rgb = img.getRGB(x, y);
						byte r = (byte) ((rgb & 0x00ff0000) >> 16);
						byte g = (byte) ((rgb & 0x0000ff00) >> 8);
						byte b = (byte)  (rgb & 0x000000ff);
						
						if (x < LedTable_Settings.ledX && y < LedTable_Settings.ledY) {
							tableAr[cell++] = (byte)r;
							tableAr[cell++] = (byte)g;
							tableAr[cell++] = (byte)b;
						}
						else {
							System.out.println("Pixel discared at x,y: " + x + "," + y);
						}
					}
				}
				else {
					for (int x = img.getWidth()-1; x >= 0; x--) {
						int rgb = img.getRGB(x, y);
						byte r = (byte) ((rgb & 0x00ff0000) >> 16);
						byte g = (byte) ((rgb & 0x0000ff00) >> 8);
						byte b = (byte)  (rgb & 0x000000ff);
						
						if (x < LedTable_Settings.ledX && y < LedTable_Settings.ledY) {
							tableAr[cell++] = (byte)r;
							tableAr[cell++] = (byte)g;
							tableAr[cell++] = (byte)b;
						}
						else {
							System.out.println("Pixel discared at x,y: " + x + "," + y);
						}
					}
				}
			}
			
			/*if (LedTable_Settings.debug) {
				System.out.println("tableAr Length: " + tableAr.length);
				int x = 0;
				int y = 0;
				int i = 1;
				Arrays.toString(tableAr);
				while (i < tableAr.length) {
					System.out.println("y = " + y + " x = " + x + " color r:" + tableAr[i++] + " g: " + tableAr[i++] + " b: " + tableAr[i++]);
					
					x++;
					if (x >= LedTable_Settings.ledX) { y++; x = 0;}
				}
			}*/
								
			tableSerial.serial.write(tableAr);
		}
		catch (IOException ioe) {
			System.out.println("Error opening file: " + ioe);
		}
	}
}
