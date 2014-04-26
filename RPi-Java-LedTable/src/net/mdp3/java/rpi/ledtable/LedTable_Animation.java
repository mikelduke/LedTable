package net.mdp3.java.rpi.ledtable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class LedTable_Animation extends Thread {
	private LedTable_Selection selection = null;
	
	private boolean run = false;
	private long imageDelay = 100;
	private File[] fileList;
	
	private byte[][] imageAr;
	
	public LedTable_Animation(LedTable_Selection s) {
		selection = s;
		imageDelay = new Long(selection.getParm2());
		
		File folder = new File(selection.getParm1());
		fileList = folder.listFiles();
		
		loadImages();
	}
	
	public void run() {
		run = true;
		
		while (run) {
			for (int i = 0; i < fileList.length; i++) {
				if (run) { //checked again so that when the mode changes, the thread can quit on the next frame
					if (i < imageAr.length) LedTable.tableSerial.serial.write(imageAr[i]);
					try {
						Thread.sleep(imageDelay);
					}
					catch (InterruptedException e) {
						System.out.println("Error in image delay: " + e);
					}
				}
			}
		}
	}
	
	public void stopAnimation() {
		run = false;
	}
	
	public long getDelay() {
		return imageDelay;
	}
	
	public long getRuntime() {
		return imageDelay * fileList.length;
	}
	
	private byte[] loadImage(String imgPath) {
		byte tableAr[] = new byte[LedTable_Settings.ledX * LedTable_Settings.ledY * 3 + 1];
		tableAr[0] = (byte)'4';
		
		try {
			URL fileURL = new File(imgPath).toURI().toURL();
			BufferedImage img = ImageIO.read(fileURL);
			
			int cell = 1;
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
		}
		catch (IOException ioe) {
			System.out.println("Error opening file: " + ioe);
		}
		return tableAr;
	}
	
	private void loadImages() {
		imageAr = new byte[fileList.length][];
		
		for (int i = 0; i < fileList.length; i++) {
			imageAr[i] = loadImage(fileList[i].getAbsolutePath());
		}
	}
}
