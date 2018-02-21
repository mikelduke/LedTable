/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * @author Mikel
 *
 */
public class EffectFactory {
	private final static Logger LOG = Logger.getLogger(EffectFactory.class.getName());
	private final static String name = "EffectFactory";
	
	public static Effect getEffect(EffectMode mode, Table table, LedTable_Selection s) throws Exception {
		LOG.entering(name, "getEffect", "mode: " + mode);
		
		Effect effect = null;
		
		if (mode.getEffectClass() != null) {
			LOG.fine("Loading Class with reflection " + mode);
			Class<? extends Effect> clazz = mode.getEffectClass();
			Constructor<? extends Effect> c = clazz.getConstructor(Table.class, LedTable_Selection.class);
			effect = c.newInstance(table, s);
		} else {
			LOG.warning("Warning: Possible Invalid Mode " + mode + ": Effect Class Not Set");
		}
		
		LOG.exiting(name, "getEffect", "Effect: " + effect);
		
		return effect;
	}
}
