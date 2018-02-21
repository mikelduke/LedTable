/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.awt.Color;
import java.util.Arrays;
import java.util.Stack;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;
import net.mdp3.java.util.string.StringUtils;

/**
 * @author Mikel
 *
 */
public class RandomFillEffect extends Effect {

	private RandomMode randomType = RandomMode.FILL;
	private boolean enableFade = true;
	private boolean direction = true;
	
	private int totalNumOfPixels = LedTable_Settings.ledX * LedTable_Settings.ledY;
	
	private static enum RandomMode {
		FILL,
		RANDOM,
		RANDOM_TO_COLOR,
		SEQ,
		SEQ_RAND,
		DIRECTIONAL_FILL,
		SQUARES
	}
		
	/**
	 * @param t
	 * @param s
	 */
	public RandomFillEffect(Table t, LedTable_Selection s) {
		super(t, s);

		setMode(s);
	}

	/* (non-Javadoc)
	 * @see net.mdp3.java.rpi.ledtable.effects.Effect#effectsLoop()
	 */
	@Override
	public void effectsLoop() {
		if (this.randomType == RandomMode.FILL) randomFill();
		else if (this.randomType == RandomMode.RANDOM) randomRandom();
		else if (this.randomType == RandomMode.RANDOM_TO_COLOR) randomToColor();
		else if (this.randomType == RandomMode.SEQ) randomSeq();
		else if (this.randomType == RandomMode.SEQ_RAND) randomSeqRandom();
		else if (this.randomType == RandomMode.DIRECTIONAL_FILL) randomDirectionFill();
		else if (this.randomType == RandomMode.SQUARES) randomRectangles();
	}

	/* (non-Javadoc)
	 * @see net.mdp3.java.rpi.ledtable.effects.Effect#setEffectInfo()
	 */
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("randomType", "Random Type Mode", StringUtils.enumToString(RandomMode.class)),
				new EffectInfoParameter("enableFade", "Enable Fading between frames", EffectInfoParameterType.BOOL),
				new EffectInfoParameter("direction", "Direction to sequential fill", EffectInfoParameterType.BOOL),
		};
		EffectInfo ei = new EffectInfo("RandomFillEffect", 
				"Sets pixels to random colors", paramInfo);
		
		return ei;
	}
	
	@Override
	public void setMode(LedTable_Selection s) {
		super.setMode(s);
		
		if(s.getParams().get("randomType") != null) this.randomType = RandomMode.valueOf(s.getParams().get("randomType").toUpperCase());
		if(s.getParams().get("enableFade") != null) this.enableFade = Boolean.parseBoolean(s.getParams().get("enableFade"));
		if(s.getParams().get("direction") != null)  this.direction = Boolean.parseBoolean(s.getParams().get("direction"));
	}
	
	/**
	 * Fill the table with all random colors
	 */
	private void randomFill() {
		byte[] newTableAr = new byte[table.getLastWrite().length];
		
		for (int i = 0; i < newTableAr.length; i++) {
			newTableAr[i] = (byte) rnd.nextInt(256);
		}
		
		showTable(newTableAr);
	}
	
	/**
	 * Changes a random amount of pixels on the table to a random color
	 */
	private void randomRandom() {
		byte[] newTableAr = Arrays.copyOf(table.getLastWrite(), table.getLastWrite().length);
		
		int numOfPixelsToFill = rnd.nextInt(totalNumOfPixels);
		
		for (int i = 0; i < numOfPixelsToFill; i++) {
			int pixel = rnd.nextInt(totalNumOfPixels);
			pixel *= 3;
			
			newTableAr[pixel]   = (byte) rnd.nextInt(256); //R
			newTableAr[pixel+1] = (byte) rnd.nextInt(256); //G
			newTableAr[pixel+2] = (byte) rnd.nextInt(256); //B
		}
		
		showTable(newTableAr);
	}
	
	/**
	 * Randomly fills pixels on the table until it is full of the next random color
	 */
	private void randomToColor() {
		byte[] newTableAr = Arrays.copyOf(table.getLastWrite(), table.getLastWrite().length);

		Color targetColor = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

		int numOfPixelsToFill = totalNumOfPixels;
		int numOfPixelsToFillAtATime = 1;
		
		Stack<Integer> s = new Stack<Integer>();
		
		int pixel = rnd.nextInt(totalNumOfPixels);
		
		for (int i = 0; i < numOfPixelsToFill; i++) {
			if (!run) return;
			
			for (int j = 0; j < numOfPixelsToFillAtATime; j++) {
				if (!run) return;
				
				while (s.contains(Integer.valueOf(pixel))) pixel = rnd.nextInt(totalNumOfPixels);
				s.push(Integer.valueOf(pixel));
				
				setPixelColor(newTableAr, pixel, targetColor);
			}
			showTable(newTableAr);
		}
	}
	
	/**
	 * Fills the table sequentially with random colors, snake like effect if 
	 * leds are snaked
	 */
	private void randomSeq() {
		byte[] newTableAr = Arrays.copyOf(table.getLastWrite(), table.getLastWrite().length);

		Color targetColor = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

		int numOfPixelsToFill = totalNumOfPixels;
		
		if (direction) {
			for (int i = 0; i < numOfPixelsToFill; i++) {
				if (!run) return;
				
				setPixelColor(newTableAr, i, targetColor);
	
				showTable(newTableAr);
			}
		} else {
			for (int i = numOfPixelsToFill-1; i >= 0; i--) {
				if (!run) return;
				
				setPixelColor(newTableAr, i, targetColor);
	
				showTable(newTableAr);
			}
		}
	}
	
	/**
	 * Fills the table sequentially with random colors
	 */
	private void randomSeqRandom() {
		byte[] newTableAr = Arrays.copyOf(table.getLastWrite(), table.getLastWrite().length);

		Color targetColor = null;

		int numOfPixelsToFill = totalNumOfPixels;
		
		if (direction) {
			for (int i = 0; i < numOfPixelsToFill; i++) {
				if (!run) return;
				
				targetColor = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
				
				setPixelColor(newTableAr, i, targetColor);
	
				showTable(newTableAr);
			}
		} else {
			for (int i = numOfPixelsToFill-1; i >= 0; i--) {
				if (!run) return;
				
				targetColor = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
				
				setPixelColor(newTableAr, i, targetColor);
	
				showTable(newTableAr);
			}
		}
	}
	
	private void randomDirectionFill() {
		byte[] newTableAr = Arrays.copyOf(table.getLastWrite(), table.getLastWrite().length);
		Color[][] colorTableAr = LedTable_Util.bArToColorAr(newTableAr);

		Color targetColor = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

		
		int runTime = this.totalNumOfPixels;
		int x = rnd.nextInt(LedTable_Settings.ledX);
		int y = rnd.nextInt(LedTable_Settings.ledY);
		
		for (int i = 0; i < runTime; i++) {
			if (!run) return;

			colorTableAr[y][x] = targetColor;
			
			int oldX = x;
			int oldY = y;
			
			int direction = rnd.nextInt(4);
			if (direction == 0) x--;
			else if (direction == 1) x++;
			else if (direction == 2) y--;
			else if (direction == 3) y++;

			if (y < 0) y = 0;
			if (y > colorTableAr.length - 1) y = colorTableAr.length - 1; 
			if (x < 0) x = 0;
			if (x > colorTableAr[y].length - 1) x = colorTableAr[y].length - 1;
			
			//If the new pixel has already been filled, the go back
			if (colorTableAr[y][x] == colorTableAr[oldY][oldX]) {
				y = oldY;
				x = oldX;
			}
			
			showTable(LedTable_Util.tableColorToByteAr(colorTableAr));
		}
	}
	
	private void randomRectangles() {
		byte[] newTableAr = Arrays.copyOf(table.getLastWrite(), table.getLastWrite().length);
		Color[][] colorTableAr = LedTable_Util.bArToColorAr(newTableAr);
		
		int startX = rnd.nextInt(LedTable_Settings.ledX - 1);
		int startY = rnd.nextInt(LedTable_Settings.ledY - 1);
		int width = rnd.nextInt(LedTable_Settings.ledX - startX);
		int height = rnd.nextInt(LedTable_Settings.ledY - startY);
		
		if (width < 1) width = 1;
		if (height < 1) height = 1;
		
		Color color = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
		
		Effect_Util.drawSquare(colorTableAr, startX, startY, width, height, color, true);
		
		showTable(LedTable_Util.tableColorToByteAr(colorTableAr));
		
		delay();
	}
	
	private void setPixelColor(byte[] newTableAr, int pos, Color c) {
		newTableAr[pos*3]   = (byte) c.getRed();
		newTableAr[pos*3+1] = (byte) c.getGreen();
		newTableAr[pos*3+2] = (byte) c.getBlue();
	}
		
	private void showTable(byte[] newTableAr) {
		if (enableFade) {
			try {
				Effect_Util.fade((Effect)this, table, newTableAr, 10, (int)delay);
			} catch (InterruptedException e) {
				LOG.severe("Error fading RandomFill: " + e);
			}
		} else {
			table.write(newTableAr);
			delay();
		}
	}
}
