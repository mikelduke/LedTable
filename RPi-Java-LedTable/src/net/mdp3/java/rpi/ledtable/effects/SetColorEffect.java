/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * @author Mikel
 *
 */
public class SetColorEffect extends Effect {

	/**
	 * @param t
	 * @param s
	 */
	public SetColorEffect(Table t, LedTable_Selection s) {
		super(t, s);
	}

	/* (non-Javadoc)
	 * @see net.mdp3.java.rpi.ledtable.effects.Effect#effectsLoop()
	 */
	@Override
	public void effectsLoop() {
		try {
			Effect_Util.colorSelection(table, selection);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Error setting table color: " + e);
		}
		
		stopEffect();
	}

	/* (non-Javadoc)
	 * @see net.mdp3.java.rpi.ledtable.effects.Effect#setEffectInfo()
	 */
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("color", "Name of color", EffectInfoParameterType.STRING),
				new EffectInfoParameter("r", "Red",   EffectInfoParameterType.INT, 0, 255),
				new EffectInfoParameter("g", "Green", EffectInfoParameterType.INT, 0, 255),
				new EffectInfoParameter("b", "Blue",  EffectInfoParameterType.INT, 0, 255)
		};
		EffectInfo ei = new EffectInfo("Set Color", 
				"Sets the table to a set color", paramInfo);
		
		return ei;
	}

}
