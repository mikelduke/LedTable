/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.io.IOException;
import java.util.Random;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * @author Mikel
 *
 */
public class ImageScanEffect extends Effect {
	private final static String name = "ImageScanEffect";
	
	private Random rnd = new Random();
	
	private String imgPath = "";
	private boolean imageLoaded = false;
	private int inc = 1;
	private int x = 0;
	private int y = 0;
	
	public ImageScanEffect(Table t, LedTable_Selection s) {
		super(t, s);
		
		if (s.getParams().get("img") != null) this.imgPath = s.getParams().get("img");
		if (s.getParams().get("inc") != null) this.inc = Integer.parseInt(s.getParams().get("inc"));
		if (inc < 1) inc = 1;
		
		if (imgPath != "") {
			try {
				LedTable_Util.loadImage(imgPath);
				imageLoaded = true;
			} catch (IOException e) {
				LOG.warning("Error loading Image: " + imgPath);
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("img", "Path to Image File", EffectInfoParameterType.FILE),
				new EffectInfoParameter("inc", "Number of pixels to move by", EffectInfoParameterType.INT, 1, 100)
		};
		EffectInfo ei = new EffectInfo("ImageScanEffect", 
				"Randomly moves around a portion of a larger image", paramInfo);
		
		return ei;
	}

	@Override
	public void effectsLoop() {
		this.scan();
	}
		
	private void scan() {
		LOG.entering(name, "scan");
		
		if (imageLoaded && run) {
			try {
				getNextCoords();
				
				LOG.finer("Displaying Image at x:" + x + ", y:" + y);
				table.write(LedTable_Util.getImagePart(this.x, this.y));
				
				delay();
			} catch (IOException e) {
				LOG.warning("Error Displaying Image part image: " + this.imgPath + " x:"+ this.x + ", y:" + this.y);
				e.printStackTrace();
			}
		}
		
		LOG.exiting(name, "scan");
	}
	
	private void getNextCoords() {
		LOG.entering(name, "getNextCoords");
		LOG.finer("Old Coords x:" + this.x + ", y:" + this.y + " inc: " + this.inc);
		
		int dirX = rnd.nextInt(3);
		int dirY = rnd.nextInt(3);
		
		switch (dirX) {
		case 0: this.x += this.inc;
				break;
		case 1: break;
		case 2: this.x -= this.inc;
				break;
		}
		
		switch (dirY) {
		case 0: this.y += this.inc;
				break;
		case 1: break;
		case 2: this.y -= this.inc;
				break;
		}
		
		//bounds check
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x > LedTable_Util.getImage().getWidth() - LedTable_Settings.ledX) x = LedTable_Util.getImage().getWidth() - LedTable_Settings.ledX;
		if (y > LedTable_Util.getImage().getHeight() - LedTable_Settings.ledY) y = LedTable_Util.getImage().getHeight() - LedTable_Settings.ledY;
		
		LOG.finer("New Coords x:" + this.x + ", y:" + this.y + " inc: " + this.inc);
		LOG.exiting(name, "getNextCoords");
	}
}
