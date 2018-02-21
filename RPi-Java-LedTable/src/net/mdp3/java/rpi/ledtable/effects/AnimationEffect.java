package net.mdp3.java.rpi.ledtable.effects;

import java.io.File;
import java.io.IOException;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.effects.EffectInfoParameter.EffectInfoParameterType;
import net.mdp3.java.rpi.ledtable.table.Table;

public class AnimationEffect extends Effect {
	private final static String name = "AnimationEffect";
	
	protected File[] fileList;
	
	protected byte[][] imageAr;
	
	protected boolean fade = false;
	protected int numOfFrames = 10;
	protected int fadeDelay = 10;
	
	public AnimationEffect(Table t, LedTable_Selection s) {
		super(t, s);
		
		if(s.getParams().get("fade") != null)        this.fade = Boolean.parseBoolean(s.getParams().get("fade"));
		if(s.getParams().get("numOfFrames") != null) this.numOfFrames = Integer.parseInt(s.getParams().get("numOfFrames"));
		if(s.getParams().get("fadeDelay") != null)   this.fadeDelay = Integer.parseInt(s.getParams().get("fadeDelay"));
		
		String f = selection.getParams().get("folder");
		File folder;
		if (f != null && f != "") {
			folder = new File(f);
			fileList = folder.listFiles();
			loadImages();
		}
	}
	
	public void effectsLoop() {
		for (int i = 0; i < fileList.length; i++) {
			if (run) { //checked again so that when the mode changes, the thread can quit on the next frame
				if (i < imageAr.length) {
					if (!this.fade)
						table.write(imageAr[i]);
					else {
						try {
							Effect_Util.fade(this, table, imageAr[i], numOfFrames, fadeDelay);
							table.write(imageAr[i]);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				delay();
			}
		}
	}
	
	public long getRuntime() {
		return this.delay * fileList.length;
	}
	
	protected byte[] loadImage(String imgPath) {
		LOG.entering(name, "loadImage", imgPath);
		
		byte tableAr[] = null;
		try {
			tableAr = LedTable_Util.loadImage(imgPath).clone();
		}
		catch (IOException ioe) {
			LOG.info("Error opening file: " + ioe);
			ioe.printStackTrace();
		}
		
		LOG.exiting(name, "loadImage", tableAr);
		return tableAr;
	}
	
	protected void loadImages() {
		LOG.entering(name, "loadImages");
		
		imageAr = new byte[fileList.length][];
		
		for (int i = 0; i < fileList.length; i++) {
			imageAr[i] = loadImage(fileList[i].getAbsolutePath());
		}
		
		LOG.exiting(name, "loadImages");
	}

	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				new EffectInfoParameter("fade", "Toggle Fading between Images", EffectInfoParameterType.BOOL),
				new EffectInfoParameter("numOfFrames", "Number of Frames to fade", EffectInfoParameterType.INT, 1, 1000),
				new EffectInfoParameter("fadeDelay", "Time between each fade frame", EffectInfoParameterType.INT, 1, 20000),
				new EffectInfoParameter("folder", "Folder to select images from", EffectInfoParameterType.FOLDER)
		};
		EffectInfo ei = new EffectInfo("AnimationEffect", "Can show images in a folder", paramInfo);
		
		return ei;
	}
}
