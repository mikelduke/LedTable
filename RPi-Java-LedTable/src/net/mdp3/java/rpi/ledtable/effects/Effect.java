/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.awt.Color;
import java.util.Random;
import java.util.logging.Logger;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * Effect class is abstract class meant to be extended to add new complex 
 * threaded effects to the Led Table. It sets up the main thread and gets
 * called from LedTable.java to start the effect loop without hanging the 
 * main app.
 * 
 * When adding a new effect, a new mode entry should be added to 
 * LedTable_Selection.Mode with the class name if one is needed. This will 
 * allow the class to be loaded via Reflection in LedTable.newSelection
 * without having to constantly update a big switch statement. 
 * 
 * @author Mikel
 *
 */
public abstract class Effect extends Thread {
	protected final static Logger LOG = Logger.getLogger(Effect.class.getName());
	private final static String name = "Effect";
	
	protected Table table = null;
	protected LedTable_Selection selection;
	protected Color[][] tableC = LedTable_Util.getTableColor();
	
	protected boolean run = false;
	protected long delay = 100;
	protected int minDelay = 1;
	protected int maxDelay = 1000;
	
	protected EffectInfo effectInfo;
	
	protected Random rnd = new Random();
	
	public Effect(Table t, LedTable_Selection s) {
		LOG.entering(name, "new");
		
		this.effectInfo = setEffectInfo();
		if (this.effectInfo == null) {
			LOG.warning("No Effect Info Set!");
			this.effectInfo = new EffectInfo("Unknown Effect", "Not Set", null);
		} 
		
		//Common Parameters for all effects
		this.effectInfo.getParamInfo().add(0, new EffectInfoParameter("delay", "Main delay amount", EffectInfoParameterType.INT, this.minDelay, this.maxDelay));
		
		this.table = t;
		setMode(s);
		
		this.setPriority(Thread.MAX_PRIORITY);
		
		LOG.exiting(name, "new");
	}
	
	public Effect() {
		this.setPriority(Thread.MAX_PRIORITY);
	}
	
	public void setTable(Table t) {
		this.table = t;
	}
	
	public void setMode(LedTable_Selection s) {
		this.selection = s;
		
		if (s.getParams().get("delay") != null)
			this.setDelay(Long.parseLong(s.getParams().get("delay")));
	}
	
	public LedTable_Selection getSelection() {
		return this.selection;
	}
	
	public boolean isRunning() {
		return run;
	}
	
	public void setDelay(long t) {
		delay = t;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void stopEffect() {
		run = false;
	}
	
	protected void delay() {
		try {
			Thread.sleep(this.delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public final void run() {
		run = true;
		
		while (run) {
			effectsLoop();
		}
	}
	
	/**
	 * Main loop for the effect
	 * 
	 * Must check variable run if looping inside of this loop to see if it 
	 * needs to be broke out of
	 * 
	 * Should call delay in between loops
	 */
	public abstract void effectsLoop();
	
	/**
	 * Method to be implemented by subclasses. This will store data about the 
	 * effect, name, desc, parameters, etc.
	 *
	 * @Example Implementation:
	 * 
	 * public EffectInfo setEffectInfo() {
	 * 		EffectInfoParameter[] paramInfo = {
	 *			new EffectInfoParameter("fade", "Toggle Fading between Images", "bool"),
	 *			new EffectInfoParameter("numOfFrames", "Number of Frames to fade", "int", 1, 1000),
	 *			new EffectInfoParameter("fadeDelay", "Time between each fade frame", "int", 1, 20000)
	 *		};
	 *		EffectInfo ei = new EffectInfo("AnimationEffect", "Can show images in a folder", paramInfo);
	 *	
	 *		return ei;
	 *	}
	 * 
	 * @return EffectInfo for the subclass
	 */
	public abstract EffectInfo setEffectInfo();
	
	public EffectInfo getEffectInfo() {
		return this.effectInfo;
	}
}
