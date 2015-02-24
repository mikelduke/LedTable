package net.mdp3.java.rpi.ledtable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.mdp3.java.rpi.ledtable.LedTable_Selection.Mode;
import net.mdp3.java.rpi.ledtable.gui.MainWindow;


/**
 * 
 * @author Mikel
 *
 * LedTable Main Class
 * 
 */
public class LedTable {
	public static LedTable_Serial tableSerial;
	private LedTable_Animation animation = null;
	//private LedTable_Modes[] modes;
	private LedTable_Midi midiMode = null;
	
	@SuppressWarnings("unused")
	private WebserviceHandler wsh;
	
	private boolean runAnimation = false;
	
	@SuppressWarnings("unused")
	private MainWindow ledTableGUI = null;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		LedTable table;
		LedTable_Settings.loadSettings();
		
		System.out.println("LedTable Serial Interface");
		
		table = new LedTable();
	}
	
	public LedTable() {
		tableSerial = new LedTable_Serial(LedTable_Settings.serialPort, 
				LedTable_Settings.serialBaud, LedTable_Settings.ledX, LedTable_Settings.ledY);
		wsh = new WebserviceHandler(this, LedTable_Settings.wsPort, LedTable_Settings.wsName);
		
		if (LedTable_Settings.enableGUI) {
			ledTableGUI = new MainWindow(this);
		}
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {
			System.out.println("Error Sleeping Thread: " + e1);
			e1.printStackTrace();
		}

		newSelection(new LedTable_Selection(Mode.PULSE1));
	}
	
	/**
	 * newSelection
	 * 
	 * Called by webservice when a new selection occurs
	 * 
	 * Either outputs the selection with parameters over serial, or calls 
	 * other classes for more advanced stuff
	 */
	public void newSelection(LedTable_Selection s) {
		if (LedTable_Settings.debug) System.out.println("Handle Selection Mode: " + s.getMode());
		
		if (LedTable_Settings.enableTableOutput && !tableSerial.isConnected()) {
			tableSerial.connect();
		}
		
		if (tableSerial.isConnected()) {
			int modeVal = s.getMode().getValue();
			char c = new String(modeVal + "").charAt(0);
			
			if (s.getMode() != Mode.ANIMATION && animation != null && animation.isRunning()) {
				runAnimation = false;
				animation.stopAnimation();
				try {
					Thread.sleep(animation.getDelay() * 2);
				} catch (InterruptedException e) {
					System.out.println("Error waiting for animation to finish " + e);
				}
			}
			
			if (s.getMode() != Mode.MIDI && midiMode != null && midiMode.isRunning()) {
				midiMode.stopMidi();
				midiMode = null;
			}
			
			if (s.getMode() == Mode.OFF || s.getMode() == Mode.DEMO || s.getMode() == Mode.PULSE1 
					|| s.getMode() == Mode.PULSE2 || s.getMode() == Mode.PULSE_RANDOM) { //arduino demo modes
				tableSerial.writeChar(c);
			}
			else if (s.getMode() == Mode.RAW) { //TODO implement frame byte ar write - launch rpi method to output to table
				String byteArStr = s.getParams().get("byteAr");
				byte[] byteAr;
				if (byteArStr != null && byteArStr.length() > 0) byteAr = byteArStr.getBytes();
			}
			else if (s.getMode() == Mode.SET_COLOR) { //set table color
				//not parsing correctly
				byte bAr[] = new byte[4];
				
				bAr[0] = (byte)c;
				bAr[1] = new Integer(Integer.parseInt(s.getParams().get("r"), 16)).byteValue();
				bAr[2] = new Integer(Integer.parseInt(s.getParams().get("g"), 16)).byteValue();
				bAr[3] = new Integer(Integer.parseInt(s.getParams().get("b"), 16)).byteValue();
				
				if (LedTable_Settings.debug) {
					System.out.print("Color selection byte ar: ");
					for (int i = 0; i < bAr.length; i++)
						System.out.print(bAr[i] + ", ");
					System.out.println("");
				}
				
				tableSerial.writeByteAr(bAr);
			}
			else if (s.getMode() == Mode.IMAGE) { //image mode
				showImage(s.getParams().get("img"));
			}
			else if (s.getMode() == Mode.ANIMATION) {
				handleAnimations(s);
			}
			else if (s.getMode() == Mode.MIDI && (midiMode == null || !midiMode.isRunning())) {
				midiMode = new LedTable_Midi();
				midiMode.setMode(1);
				midiMode.startMidi();
			}
			else {
				System.out.println("Error: Invalid Mode");
			}
		}
	}
	
	private void handleAnimations(LedTable_Selection s) {
		if (animation != null && animation.isRunning()) {
			runAnimation = false;
			animation.stopAnimation();
			try {
				Thread.sleep(animation.getDelay() * 2);
			} catch (InterruptedException e) {
				System.out.println("Error waiting for animation to finish " + e);
			}
		}
		runAnimation = true;
		animation = new LedTable_Animation(s);
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
							System.out.println("Pixel discarded at x,y: " + x + "," + y);
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
			if (tableSerial.isConnected()) {
				tableSerial.serial.write(tableAr);
			}
		}
		catch (IOException ioe) {
			System.out.println("Error opening file: " + ioe);
		}
	}
	
	public void quit() {
		
		//this is a safe selection for quitting - handleSelection will close the opened threads
		newSelection(new LedTable_Selection(Mode.OFF));
		
		if (LedTable_Settings.debug) System.out.println("Exiting LedTable");
		System.exit(0);
	}
}
