package net.mdp3.java.rpi.ledtable.effects;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * Class extends AnimationEffect to add transparency feature for effects
 * Image Series for animation will be loaded, shown in random location,
 * and will use 1 random new color to overwrite existing colors.
 * 
 * Images should be 2 color black and white, black will be used as 
 * transparency color
 * 
 * @author Mikel
 *
 */
public class TransparencyAnimationEffect extends AnimationEffect {
	private final static String name = "TransparencyAnimationEffect";
	
	public TransparencyAnimationEffect(Table t, LedTable_Selection s) {
		super(t, s);
	}
	
	public void effectsLoop() {
		LOG.entering(TransparencyAnimationEffect.name, "effectsLoop");
		
		//TODO Implement new transparency effect, right now this just shows plain animations
		for (int i = 0; i < fileList.length; i++) {
			if (run) { //checked again so that when the mode changes, the thread can quit on the next frame
				if (i < super.imageAr.length) {
					if (!this.fade)
						super.table.write(imageAr[i]);
					else {
						try {
							Effect_Util.fade(this, super.table, super.imageAr[i], super.numOfFrames, super.fadeDelay);
							super.table.write(super.imageAr[i]);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				delay();
			}
		}
		
		LOG.exiting(TransparencyAnimationEffect.name, "effectsLoop");
	}
	
	public long getRuntime() {
		return this.delay * fileList.length;
	}
	
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("fade", "Toggle Fading between Images", EffectInfoParameterType.BOOL),
				new EffectInfoParameter("numOfFrames", "Number of Frames to fade", EffectInfoParameterType.INT, 1, 1000),
				new EffectInfoParameter("fadeDelay", "Time between each fade frame", EffectInfoParameterType.INT, 1, 20000),
				new EffectInfoParameter("folder", "Folder to select images from", EffectInfoParameterType.FOLDER)
		};
		EffectInfo ei = new EffectInfo("TransparencyAnimationEffect", "Can show images in a folder", paramInfo);
		
		return ei;
	}
}
