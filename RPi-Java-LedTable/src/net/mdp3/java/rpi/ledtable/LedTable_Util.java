/**
 * 
 */
package net.mdp3.java.rpi.ledtable;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.mdp3.java.rpi.ledtable.table.TableSPI;

/**
 * @author Mikel
 *
 */
public class LedTable_Util {
	private final static Logger LOG = Logger.getLogger(TableSPI.class.getName());
	private final static String name = "LedTable_Util";
	
	private static byte tableAr[][] = new byte[LedTable_Settings.ledY][LedTable_Settings.ledX * 3];
	private static byte bAr[] = new byte[LedTable_Settings.ledY * LedTable_Settings.ledX * 3];
	private static Color[][] tableColor = new Color[LedTable_Settings.ledY][LedTable_Settings.ledX];
	
	private static BufferedImage img = null;
	private static String lastImg = "";
	
	public static byte[] getRGBArray(int r, int g, int b) {
		return getRGBArray((byte)r, (byte)g, (byte)b);
	}
	/**
	 * Fills and returns byte array of set color
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static byte[] getRGBArray(byte r, byte g, byte b) {
		LOG.entering(name, "getRGBArray", r + " " + g + " " + b);
		
		int i = 0;
		while (i < bAr.length) {
			bAr[i++] = r;
			bAr[i++] = g;
			bAr[i++] = b;
		}
		
		LOG.exiting(name, "getRGBArray", bAr);
		return bAr;
	}
	
	/**
	 * intArToByteAr
	 * 
	 * Converts int tableAr to byte bAr and sets byte 0 to "4"
	 * 
	 * tableAr is a 2 dimensional array, this converts it to a 1 dimensional array
	 * This function assumes that the leds are snaked and reverses every other row
	 */
	public static byte[] tableArToByteAr() {
		int cell = 0;
		
		if (LedTable_Settings.snakedLeds) {
			for (int y = 0; y < LedTable_Settings.ledY; y++) {
				if (y % 2 == 0) {
					for (int x = 0; x < LedTable_Settings.ledX; x++) {
						bAr[cell++] = (byte)tableAr[y][x * 3];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 1];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 2];
					}
				} else {
					for (int x = LedTable_Settings.ledX - 1; x >= 0; x--) {
						bAr[cell++] = (byte)tableAr[y][x * 3];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 1];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 2];
					}
				}
			}
		} else { //Non snaked leds
			for (int y = 0; y < LedTable_Settings.ledY; y++) {
				for (int x = 0; x < LedTable_Settings.ledX; x++) {
					bAr[cell++] = (byte)tableAr[y][x * 3];
					bAr[cell++] = (byte)tableAr[y][x * 3 + 1];
					bAr[cell++] = (byte)tableAr[y][x * 3 + 2];
				}
			}
		}
		
		return bAr;
	}
	
	public static byte[] tableArToByteAr(byte[][] tAr) {
		LedTable_Util.tableAr = tAr;
		
		return LedTable_Util.tableArToByteAr();
	}
	
	public static byte[] tableColorToByteAr() {
		int cell = 0;
		if (LedTable_Settings.snakedLeds) {
			for (int y = 0; y < tableColor.length; y++) {
				if (y % 2 == 0) {
					for (int x = 0; x < tableColor[y].length; x++) {
						Color c = tableColor[y][x];
						
						int r = 0;
						int g = 0;
						int b = 0;
						
						if (c != null) { 
							r = c.getRed();
							g = c.getGreen();
							b = c.getBlue();
						} else {
							LOG.finer("Null Color at x:" + x + ", y:" + y);
						}
						
						bAr[cell++] = (byte)r;
						bAr[cell++] = (byte)g;
						bAr[cell++] = (byte)b;
					}
				} else {
					for (int x = LedTable_Settings.ledX - 1; x >= 0; x--) {
						Color c = tableColor[y][x];
						
						int r = 0;
						int g = 0;
						int b = 0;
						
						if (c != null) {
							r = c.getRed();
							g = c.getGreen();
							b = c.getBlue();
						} else {
							LOG.finer("Null Color at x:" + x + ", y:" + y);
						}
						
						bAr[cell++] = (byte)r;
						bAr[cell++] = (byte)g;
						bAr[cell++] = (byte)b;
					}
				}
			}
		} else { // Non snaked leds
			for (int y = 0; y < tableColor.length; y++) {
				for (int x = 0; x < tableColor[y].length; x++) {
					Color c = tableColor[y][x];
					
					int r = 0;
					int g = 0;
					int b = 0;
					
					if (c != null) { 
						r = c.getRed();
						g = c.getGreen();
						b = c.getBlue();
					} else {
						LOG.finer("Null Color at x:" + x + ", y:" + y);
					}
					
					bAr[cell++] = (byte)r;
					bAr[cell++] = (byte)g;
					bAr[cell++] = (byte)b;
				}
			}
		}

		return bAr;
	}
	
	public static byte[] tableColorToByteAr(Color c[][]) {
		LedTable_Util.tableColor = c;
		
		return LedTable_Util.tableColorToByteAr();
	}
	
	
	/**
	 * Debug function to dump byte array to console
	 */
	public static void showByteArray() {
		String str = "Array: \n";
		str += bArToString(bAr);
		System.out.println(str);
	}
	
	public static String bArToString(byte bAr[]) {
		String str = "\n";
		
		for (int i = 0; i < bAr.length; i++) {
			str += (int)(bAr[i] & 0xFF);
			str += ' ';
			if ((i+1) % 24 == 0) str += '\n';
		}
		
		return str;
	}
	
	public static byte[] loadImage(String imgPath) throws IOException {
		return LedTable_Util.loadImage(imgPath, 0, 0);
	}
	
	public static byte[] loadImage(String imgPath, int startX, int startY) throws IOException {
		return LedTable_Util.loadImage(imgPath, startX, startY, 0, 0);
	}
	
	public static byte[] loadImage(String imgPath, int startX, int startY, int newW, int newH) throws IOException {
		if (imgPath != lastImg) {
			URL fileURL = new File(imgPath).toURI().toURL();
			img = ImageIO.read(fileURL);
			lastImg = imgPath;
			System.out.println("Image size x: " + img.getWidth() + " y: " + img.getHeight());
			
			if (newW > 0 && newH > 0) {
				img = LedTable_Util.resizeImage(img, newW, newH);
			}
		}
		
		return getImagePart(startX, startY);
	}
	
	public static BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight) {
		return LedTable_Util.resizeImage(img, newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
	}
	
	public static BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight, int resizeType) {
		Image newImg = img.getScaledInstance(newWidth, newHeight, resizeType);
		BufferedImage buffered = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(newImg, 0, 0 , null);
		
		return buffered;
	}
	
	public static byte[] getImagePart(int startX, int startY) throws IOException {
		if (img == null) throw new IOException("Image not loaded yet");
		
		int endX = startX + LedTable_Settings.ledX;
		int endY = startY + LedTable_Settings.ledY;
		
		if (startX > img.getWidth()) throw new IOException("Start X of " + startX + " Greater than Image Width: " + img.getWidth());
		if (startY > img.getHeight()) throw new IOException("Start Y of " + startY + " Greater than Image Height: " + img.getHeight());
		
		//if (endX > img.getWidth()) throw new IOException("End X of " + startX + " Greater than Image Width: " + img.getWidth());
		//if (endY > img.getHeight()) throw new IOException("End Y of " + startY + " Greater than Image Height: " + img.getHeight());
		
		if (endX > img.getWidth()) endX = img.getWidth();
		if (endY > img.getHeight()) endY = img.getHeight();
		
		int tableX = 0;
		int tableY = 0;
		
		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				int rgb = img.getRGB(x, y);
				Color c = new Color(rgb);
				
				if (tableX < LedTable_Settings.ledX && tableY < LedTable_Settings.ledY) {
					tableColor[tableY][tableX] = c;
				}
				else {
					System.out.println("Pixel discarded at x,y: " + x + "," + y);
				}
				tableX++;
			}
			tableY++;
			tableX = 0;
		}
		return LedTable_Util.tableColorToByteAr();
	}
	
	public static BufferedImage getImage() {
		return img;
	}
	
	public synchronized static byte[][] getTableAr() {
		return tableAr;
	}
	
	public synchronized static Color[][] getTableColor() {
		return tableColor;
	}
	
	public synchronized static byte[] getbAr() {
		return bAr;
	}
	
	/*public static byte[][] bArToTableAr(byte[] bAr) {
		byte[][] tableAr = new byte[LedTable_Settings.ledY][LedTable_Settings.ledX * 3];
		
		if (LedTable_Settings.snakedLeds) {
			int pos = 0;
			for (int y = 0; y < tableAr.length; y++) {
				if ((y % 2) == 0) {
					for (int x = 0; x < tableAr[y].length; x++) {
						tableAr[y][x] = bAr[pos++];
					}
				} else { //FIXME i think its reversing the colors when reversing the rows
					for (int x = tableAr[y].length - 1; x >= 0; x-= 3) {
						tableAr[y][x] = bAr[pos++];
						tableAr[y][x-1] = bAr[pos++];
						tableAr[y][x-2] = bAr[pos++];
					}
				}
			}
		} else {
			int pos = 0;
			for (int y = 0; y < tableAr.length; y++) {
				for (int x = 0; x < tableAr[y].length; x++) {
					tableAr[y][x] = bAr[pos++];
				}
			}
		}
		
		return tableAr;
	}*/
	
	public static Color[][] tableArToColorAr(byte[][] tableAr) {
		Color[][] colorAr = new Color[LedTable_Settings.ledY][LedTable_Settings.ledX];
		
		for (int y = 0; y < colorAr.length; y++) {
			for (int x = 0; x < colorAr[y].length; x++) {
				int r = tableAr[y][x * 3] & 0xFF;
				int g = tableAr[y][x * 3 + 1] & 0xFF;
				int b = tableAr[y][x * 3 + 2] & 0xFF;
				
				Color c = new Color(r, g, b);
				colorAr[y][x] = c;
			}
		}
		
		return colorAr;
	}
	
	public static Color[][] bArToColorAr(byte[] bAr) {
		Color[][] grid = new Color[LedTable_Settings.ledY][LedTable_Settings.ledX];
		
		int byteCounter = 0;
		if (LedTable_Settings.snakedLeds) {
			for (int y = 0; y < LedTable_Settings.ledY; y++) {
				if (y % 2 == 0) {
					for (int x = 0; x < LedTable_Settings.ledX; x++) {
						int r = (int)(bAr[byteCounter++] & 0xFF);
						int g = (int)(bAr[byteCounter++] & 0xFF);
						int b = (int)(bAr[byteCounter++] & 0xFF);
						
						grid[y][x] = new Color(r, g, b);
					}
				} else {
					for (int x = LedTable_Settings.ledX - 1; x >= 0; x--) {
						int r = (int)(bAr[byteCounter++] & 0xFF);
						int g = (int)(bAr[byteCounter++] & 0xFF);
						int b = (int)(bAr[byteCounter++] & 0xFF);
						
						grid[y][x] = new Color(r, g, b);
					}
				}
			}
		} else {
			for (int y = 0; y < LedTable_Settings.ledY; y++) {
				for (int x = 0; x < LedTable_Settings.ledX; x++) {
					int r = (int)(bAr[byteCounter++] & 0xFF);
					int g = (int)(bAr[byteCounter++] & 0xFF);
					int b = (int)(bAr[byteCounter++] & 0xFF);
					grid[y][x] = new Color(r, g, b);
				}
			}
		}
		
		return grid;
	}
}
