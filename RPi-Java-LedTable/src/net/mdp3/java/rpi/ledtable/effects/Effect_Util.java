/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Logger;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.table.Table;
import net.mdp3.java.util.string.StringUtils;

/**
 * @author Mikel
 *
 */
public class Effect_Util {
	private final static Logger LOG = Logger.getLogger(Effect_Util.class.getName());
	private final static String NAME = "Effect_Util";
	
	/**
	 * Uses the current tableAr in LedTable_Util and fades it towards the finishAr
	 * 
	 * @param e Reference to the Effect that object that is calling Fade, used to check if it is still running to break out of the loop. Can be null 
	 * @param t LedTable to output to
	 * @param finishAr byte array to fade to
	 * @param numOfFrames number of frames to fade over
	 * @param delay time between each frame
	 * @throws InterruptedException
	 */
	public static void fade(Effect e, Table t, byte[] finishAr, int numOfFrames, int delay) throws InterruptedException {
		LOG.entering(NAME, "fade");
		
		if (numOfFrames < 1) return;
		if (delay < 1) return;
		
		byte[] lastWrite = Arrays.copyOf(t.getLastWrite(), t.getLastWrite().length);
		if (lastWrite == null) {
			LOG.warning("Last Write not Initialized");
			return;
		}
		
		if (finishAr.length != lastWrite.length) {
			LOG.severe("Invalid Array Lengths");
			return;
		}
		
		for (int i = 0; i < numOfFrames; i++) {
			//Simple change of every byte in the array by frame/numberofframes amount
			for (int j = 0; j < lastWrite.length; j++) {
				int diff = (int)(lastWrite[j] & 0xFF) - (int)(finishAr[j] & 0xFF);
				diff /= numOfFrames;
				lastWrite[j] -= diff;
			}

			t.write(lastWrite);
			
			if (e == null || e.isRunning()) {
				Thread.sleep(delay);
			} else {
				return;
			}
		}
		
		LOG.exiting(NAME, "fade");
	}
	
	/**
	 * Changes the table to the specified color
	 * 
	 * @param table
	 * @param s
	 * @throws Exception
	 */
	public static void colorSelection(Table table, LedTable_Selection s) throws Exception {
		boolean colorsSet = false;
		
		byte r = 0;
		byte g = 0;
		byte b = 0;
		
		if (s.getParams().get("color") != null && s.getParams().get("color") != "") {
			String colorStr = s.getParams().get("color");
			final Field f = Color.class.getField(colorStr);
			Color color = (Color) f.get(null);
			
			if (color != null) {
				r = (byte)color.getRed();
				g = (byte)color.getGreen();
				b = (byte)color.getBlue();
				
				colorsSet = true;
			}
		} else if (s.getParams().get("r") != null && s.getParams().get("r") != "" &&
				s.getParams().get("g") != null && s.getParams().get("g") != "" &&
				s.getParams().get("b") != null && s.getParams().get("b") != "") {
			
			int iR = StringUtils.tryParseInt(s.getParams().get("r"));
			int iG = StringUtils.tryParseInt(s.getParams().get("g"));
			int iB = StringUtils.tryParseInt(s.getParams().get("b"));
			
			r = (byte) iR;
			g = (byte) iG;
			b = (byte) iB;
			
			colorsSet = true;
		} else throw new Exception("Invalid Color Selection for Mode");
		
		if (colorsSet) {
			table.write(LedTable_Util.getRGBArray(r, g, b));
		}
	}
	
	public static void showImage(Table table, LedTable_Selection s) {
		int x = 0;
		int y = 0;
		boolean scaleToFit = false;
		
		if (s.getParams().get("x") != null) x = Integer.parseInt(s.getParams().get("x"));
		if (s.getParams().get("y") != null) y = Integer.parseInt(s.getParams().get("y"));
		if (s.getParams().get("scale") != null) scaleToFit = Boolean.parseBoolean(s.getParams().get("scale"));
		
		String img = s.getParams().get("img");
		if (img == null || img == "") return;
		
		try {
			if (scaleToFit) 
				table.write(LedTable_Util.loadImage(img, x, y, LedTable_Settings.ledX, LedTable_Settings.ledY));
			else 
				table.write(LedTable_Util.loadImage(img, x, y));
		} catch (IOException e) {
			LOG.warning("Error load image: " + img + " x: " + x + " y: " + y);
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a Color object created from the parameters for Selection
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	protected static Color loadColor(LedTable_Selection s) throws Exception {
		int r = 0;
		int g = 0;
		int b = 0;
		
		if (s.getParams().get("color") != null && s.getParams().get("color") != "") {
			String colorStr = s.getParams().get("color");
			final Field f = Color.class.getField(colorStr);
			Color color = (Color) f.get(null);
			
			if (color != null) {
				r = color.getRed();
				g = color.getGreen();
				b = color.getBlue();
			}
		} else if (s.getParams().get("r") != null && s.getParams().get("r") != "" &&
				s.getParams().get("g") != null && s.getParams().get("g") != "" &&
				s.getParams().get("b") != null && s.getParams().get("b") != "") {
			
			r = StringUtils.tryParseInt(s.getParams().get("r"));
			g = StringUtils.tryParseInt(s.getParams().get("g"));
			b = StringUtils.tryParseInt(s.getParams().get("b"));
		} else throw new Exception("Invalid Color Selection");
		
		Color c = new Color(r, g, b);
		return c;
	}
	
	protected static void drawSquare(Color[][] colorAr, int startX, int startY, int w, int h, Color color, boolean fill) {
		if (startX < 0) return;
		if (startY < 0) return;
		if (colorAr.length < 1) return;
		if (startX >= colorAr[0].length) return;
		if (startY >= colorAr.length) return;
		if (w <= 0) return;
		if (h <= 0) return;
		
		int stopX = startX + w;
		int stopY = startY + h;
		
		for (int y = startY; y <= stopY; y++) {
			for (int x = startX; x <= stopX; x++) {
				if (fill) {
					colorAr[y][x] = color;
				} else {
					if (x == startX || y == startY || x == stopX || y == stopY) {
						colorAr[y][x] = color;
					}
				}
			}
		}
	}
}
