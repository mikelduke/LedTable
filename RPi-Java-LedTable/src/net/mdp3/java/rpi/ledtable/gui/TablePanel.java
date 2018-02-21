/**
 * 
 */
package net.mdp3.java.rpi.ledtable.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.table.WriteListener;

/**
 * @author Mikel
 *
 */
public class TablePanel extends JPanel implements WriteListener {
	private final static Logger LOG = Logger.getLogger(TablePanel.class.getName());
	private final static String name = "TablePanel";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4720593039682897655L;
	private int pixelW = (LedTable_Settings.guiW - 10) / LedTable_Settings.ledX;
	private int pixelH = (LedTable_Settings.guiH - 70) / LedTable_Settings.ledY;
	
	private Color grid[][];

	public TablePanel() {
		LOG.finer("Pixel Size: " + pixelW + "x" + pixelH);

		this.setBackground(Color.BLACK);
		
		//init Color Grid
		grid = new Color[LedTable_Settings.ledY][];
		for (int y = 0; y < LedTable_Settings.ledY; y++) {
			grid[y] = new Color[LedTable_Settings.ledX];
			
			for (int x = 0; x < LedTable_Settings.ledX; x++) {
				grid[y][x] = Color.BLACK;
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		LOG.entering(name, "paintComponent");
		
		drawTable(g);
		drawPixelGrid(g);
		
		LOG.exiting(name, "paintComponent");
	}
	
	/**
	 * Draws the grid lines
	 * 
	 * @param g
	 */
	private void drawPixelGrid(Graphics g) {
		LOG.entering(name, "drawPixelGrid");
		
		g.setColor(Color.WHITE);
		
		for (int y = 0; y < LedTable_Settings.ledY + 1; y++) {
			g.drawLine(0, y * pixelH, this.getWidth(), y * pixelH);
		}
		for (int x = 0; x < LedTable_Settings.ledX + 1; x++) {
			g.drawLine(x * pixelW, 0, x * pixelW, this.getHeight());
		}
		
		LOG.exiting(name, "drawPixelGrid");
	}
	
	
	/**
	 * Draws table grid to screen
	 * 
	 * @param g
	 */
	private void drawTable(Graphics g) {
		LOG.entering(name, "drawTable");
		
		Graphics2D g2d = (Graphics2D)g;
		
		if (LedTable_Settings.flipY) {
			g2d.scale(1, -1);
			g2d.translate(0, -getHeight());
		}
		
		for (int y = 0; y < LedTable_Settings.ledY; y++) {
			for (int x = 0; x < LedTable_Settings.ledX; x++) {
				g2d.setColor(grid[y][x]);
				g2d.fillRect(x * pixelW, y * pixelH, pixelW, pixelH);
			}
		}
		
		LOG.exiting(name, "drawTable");
	}
	
	/**
	 * Converts the byte Array bAr to the Color[][] grid and calls repaint()
	 * 
	 * @param bAr
	 */
	public void drawTable(byte bAr[]) {
		LOG.entering(name, "drawTable");
		
		if (bAr.length != LedTable_Settings.ledX * LedTable_Settings.ledY * 3) {
			System.out.println("TablePanel.drawTable ERROR: bAr is wrong size!");
			return;
		}
		
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
		
		//if (LedTable_Settings.debug) dumpColorGrid();
		
		repaint();
		LOG.exiting(name, "drawTable");
	}
	
	@SuppressWarnings("unused")
	private void dumpColorGrid() {
		String gridStr = "Array: \n";
		for (int y = 0; y < LedTable_Settings.ledY; y++) {
			for (int x = 0; x < LedTable_Settings.ledX; x++) {
				if (x % 8 == 0) gridStr += '\n';
				gridStr += grid[y][x].getRed() + " ";
				gridStr += grid[y][x].getGreen() + " ";
				gridStr += grid[y][x].getBlue() + " ";
			}
		}
		System.out.println(gridStr);
	}

	@Override
	public void writeEvent(byte[] bAr) {
		this.drawTable(bAr);
	}
	
	/**
	 * Saves the current table grid to an image file for later loading
	 * Note: This saves exactly what is shown at the time
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void saveTable(String fileName) throws IOException {
		saveTable(new File(fileName));
	}
	
	/**
	 * Saves the current table grid to an image file for later loading
	 * Note: This saves exactly what is shown at the time
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void saveTable(File file) throws IOException {
		//if (!file.canWrite()) throw new IOException("Cannot write to file: " + file);
		
		BufferedImage bi = new BufferedImage(LedTable_Settings.ledX, LedTable_Settings.ledY, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				bi.setRGB(x, y, grid[y][x].getRGB());
			}
		}
		
		ImageIO.write(bi, "bmp", file);
	}
}
