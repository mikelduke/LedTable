/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.awt.Color;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * @author Mikel
 *
 */
public class RainbowEffect extends Effect {
	private final static String name = "RainbowEffect";
	
	private int rbInc1 = 10;
	private int rbInc2 = 1;
	private int rbType = 1;

	public RainbowEffect(Table t, LedTable_Selection s) {
		super(t, s);
		setMode(s);
	}
	
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("rbInc1", "Amount to change colors by", EffectInfoParameterType.INT, 1, 100),
				new EffectInfoParameter("rbInc2", "Other amount to change by", EffectInfoParameterType.INT, 1, 100),
				new EffectInfoParameter("rbType", "Rainbow Mode", EffectInfoParameterType.INT, 1, 2)
		};
		EffectInfo ei = new EffectInfo("RainbowEffect", 
				"Changes between lots of colors", paramInfo);
		
		return ei;
	}

	public void setMode(LedTable_Selection s) {
		super.setMode(s);
		
		if(s.getParams().get("rbInc1") != null) this.rbInc1 = Integer.parseInt(s.getParams().get("rbInc1"));
		if(s.getParams().get("rbInc2") != null) this.rbInc2 = Integer.parseInt(s.getParams().get("rbInc2"));
		if(s.getParams().get("rbType") != null) this.rbType = Integer.parseInt(s.getParams().get("rbType"));
	}

	@Override
	public void effectsLoop() {
		if (rbType == 1) this.rainbow();
		else this.rainbow2();
	}
		
	private void rainbow() {
		LOG.entering(name, "rainbow");
		
		byte[] bAr = LedTable_Util.getbAr();
		//TODO Change to user tableAr[][] instead of byteAr[], the snake code is being passed over when ran
		for (int j=0; j < 256; j += rbInc1) {     // 3 cycles of all 256 colors in the wheel
			for (int i = 0; i < LedTable_Util.getbAr().length; i += rbInc2) {
				Color c = wheel((i + j) % 255);
				bAr[i++] = (byte)c.getRed();
				bAr[i++] = (byte)c.getGreen();
				bAr[i]   = (byte)c.getBlue();
			}
			table.write(bAr);
			
			if (!run) return;
			delay();
		}
		
		LOG.exiting(name, "rainbow");
	}
	
	private void rainbow2() {
		for (int j = 0; j < 256; j += rbInc1) {
			for (int y = 0; y < LedTable_Util.getTableAr().length; y++) {
				for (int x = 0; x < LedTable_Util.getTableAr()[y].length; x++) {
					Color c = wheel((j + rbInc2) % 255);
					LedTable_Util.getTableAr()[y][x] = (byte)c.getRed();
					LedTable_Util.getTableAr()[y][x++] = (byte)c.getGreen();
					LedTable_Util.getTableAr()[y][x++] = (byte)c.getBlue();
					
					table.write(LedTable_Util.tableArToByteAr());
					
					if (!run) return;
					delay();
				}
			}
		}
	}
	
	private Color wheel(int pos) {
		if (pos < 85) {
			return new Color(pos * 3, 255 - pos * 3, 0);
		} else if (pos < 170) {
			pos -= 85;
			return new Color(255 - pos * 3, 0, pos * 3);
		} else {
			pos -= 170;
			return new Color(0, pos * 3, 255 - pos * 3);
		}
	}
}
