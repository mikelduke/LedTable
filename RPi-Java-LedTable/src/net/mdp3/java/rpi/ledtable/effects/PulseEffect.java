/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;
import net.mdp3.java.util.string.StringUtils;

/**
 * @author Mikel
 *
 */
public class PulseEffect extends Effect {
	private final static String name = "PulseEffect";
	
	private int pulseInc = 1;
	private PulseType pulseType = PulseType.RGB;
	
	private int oldR = 0;
	private int oldG = 0;
	private int oldB = 0;
	
	private static enum PulseType {
		RGB,
		RGB2,
		RANDOM,
		RANDOM2,
		RANDOM3
	}
	
	public PulseEffect(Table t, LedTable_Selection s) {
		super(t, s);
		delay = 1;
		setMode(s);
		
		table.write(LedTable_Util.getRGBArray(0, 0, 0));
	}
	
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("inc", "Amount to change colors by", EffectInfoParameterType.INT, 1, 100),
				new EffectInfoParameter("pulseType", "Pulse Type Mode", StringUtils.enumToString(PulseType.class))
		};
		EffectInfo ei = new EffectInfo("PulseEffect", 
				"Fades the table color in and out", paramInfo);
		
		return ei;
	}
	
	public void setMode(LedTable_Selection s) {
		super.setMode(s);
		
		if(s.getParams().get("inc") != null) this.pulseInc = Integer.parseInt(s.getParams().get("inc"));
		if(s.getParams().get("pulseType") != null) this.pulseType = PulseType.valueOf(s.getParams().get("pulseType").toUpperCase());
	}

	@Override
	public void effectsLoop() {
		if (this.pulseType == PulseType.RGB) this.rgbPulse();
		else if (this.pulseType == PulseType.RGB2) this.rgbPulse2();
		else if (this.pulseType == PulseType.RANDOM) this.randomPulse();
		else if (this.pulseType == PulseType.RANDOM2) this.randomPulse2();
		else if (this.pulseType == PulseType.RANDOM3) this.randomPulse3();
	}
	
	private void rgbPulse() {
		LOG.entering(name, "rgbPulse");
		
		for (int i = 0; i <= 255; i += pulseInc) {
			if (!run) return;
			
			LOG.finer("pulse RED");
			table.write(LedTable_Util.getRGBArray(i, 0, 0));
			this.delay();
		}
		for (int i = 0; i <= 255; i += pulseInc) {
			if (!run) return;
			
			LOG.finer("pulse GREEN");
			table.write(LedTable_Util.getRGBArray(0, i, 0));
			this.delay();
		}
		for (int i = 0; i <= 255; i += pulseInc) {
			if (!run) return;
			
			LOG.finer("pulse BLUE");
			table.write(LedTable_Util.getRGBArray(0, 0, i));
			this.delay();
		}
		
		LOG.exiting(name, "rgbPulse");
	}
	
	private void rgbPulse2() {
		LOG.entering(name, "rgbPulse2");
		
		for (int i = 0; i <= 255; i += pulseInc) {
			if (!run) return;
			
			table.write(LedTable_Util.getRGBArray(i, 0, 0));
			this.delay();
		}
		for (int i = 255; i >= 0; i -= pulseInc) {
			if (!run) return;
			
			table.write(LedTable_Util.getRGBArray(i, 0, 0));
			this.delay();
		}
		for (int i = 0; i <= 255; i += pulseInc) {
			if (!run) return;
			
			table.write(LedTable_Util.getRGBArray(0, i, 0));
			this.delay();
		}
		for (int i = 255; i >= 0; i -= pulseInc) {
			if (!run) return;
			
			table.write(LedTable_Util.getRGBArray(0, i, 0));
			this.delay();
		}
		for (int i = 0; i <= 255; i += pulseInc) {
			if (!run) return;
			
			table.write(LedTable_Util.getRGBArray(0, 0, i));
			this.delay();
		}
		for (int i = 255; i >= 0; i -= pulseInc) {
			if (!run) return;
			
			table.write(LedTable_Util.getRGBArray(0, 0, i));
			this.delay();
		}
		
		LOG.exiting(name, "rgbPulse");
	}
	
	/**
	 * Fades to the next random color, once color type at a time R then G then B
	 */
	private void randomPulse() {
		int newR = rnd.nextInt(256);
		int newG = rnd.nextInt(256);
		int newB = rnd.nextInt(256);
		
		while ((newR > oldR + pulseInc/2) || (newR < oldR - pulseInc/2)) {
			if (oldR < newR) {
				oldR += pulseInc;
			}
			else if (oldR > newR) {
				oldR -= pulseInc;
			}

			table.write(LedTable_Util.getRGBArray(oldR, oldG, oldB));
			if (!run) return;
			this.delay();
		}
		while ((newG > oldG + pulseInc/2) || (newG < oldG - pulseInc/2)) {
			if (oldG < newG) {
				oldG += pulseInc;
			}
			else if (oldG > newG) {
				oldG -= pulseInc;
			}
			table.write(LedTable_Util.getRGBArray(oldR, oldG, oldB));
			if (!run) return;
			this.delay();
		}
		while ((newB > oldB + pulseInc/2) || (newB < oldB - pulseInc/2)) {
			if (oldB < newB) {
				oldB += pulseInc;
			}
			else if (oldB > newB) {
				oldB -= pulseInc;
			}
			table.write(LedTable_Util.getRGBArray(oldR, oldG, oldB));
			if (!run) return;
			this.delay();
		}
	}
	
	/**
	 * Different from randomPulse because it uses the fade method, which will 
	 * fade through all 3 colors at once instead of doing R then G then B
	 */
	private void randomPulse2() {
		int newR = rnd.nextInt(256);
		int newG = rnd.nextInt(256);
		int newB = rnd.nextInt(256);
		
		byte[] newTable = LedTable_Util.getRGBArray(newR, newG, newB);
		
		try {
			Effect_Util.fade((Effect)this, table, newTable, pulseInc, (int)delay);
			delay();
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.severe("Error in randomPulse2 waiting thread: " + e);
		}
		
	}
	
	private void randomPulse3() {
		int oldRed   = table.getLastWrite()[0] & 0xFF;
		int oldGreen = table.getLastWrite()[1] & 0xFF;
		int oldBlue  = table.getLastWrite()[2] & 0xFF;
		
		int newR; 
		int newG;
		int newB;
		
		int colorToChange = rnd.nextInt(3);
		
		if (colorToChange == 0) newR = rnd.nextInt(256); 
		else newR = oldRed; 
		
		if (colorToChange == 0) newG = rnd.nextInt(256); 
		else newG = oldGreen;
		
		if (colorToChange == 0) newB = rnd.nextInt(256); 
		else newB = oldBlue;
		
		byte[] newTable = LedTable_Util.getRGBArray(newR, newG, newB);
		
		try {
			Effect_Util.fade((Effect)this, table, newTable, pulseInc, (int)delay);
			delay();
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.severe("Error in randomPulse2 waiting thread: " + e);
		}
		
	}
}
