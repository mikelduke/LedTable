package net.mdp3.java.rpi.ledtable;

import java.util.HashMap;

/**
 * 
 * @author Mikel
 *
 * Class to hold the selection mode and parameter map
 */

public class LedTable_Selection {
	private HashMap<String,String> params;
	
	public static enum Mode {
		OFF(0),
		DEMO(1),
		PULSE1(2),
		PULSE2(3),
		RAW(4),
		SET_COLOR(5),
		PULSE_RANDOM(6),
		IMAGE(7),
		ANIMATION(8),
		MIDI(9);
		
		private final int value;
		private Mode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	private Mode selection_mode = Mode.PULSE1;
	
	public LedTable_Selection(Mode s_mode) {
		this(s_mode, null);
	}

	public LedTable_Selection(Mode s_mode, HashMap<String,String> params) {
		if (params == null) params = new HashMap<String,String>();
		
		this.selection_mode = s_mode;
		this.params = params;
	}
	
	public void setMode(Mode mode) {
		selection_mode = mode;
	}
	
	public Mode getMode() {
		return this.selection_mode;
	}
	
	@Override
	public String toString() {
		return selection_mode + "";
	}
	
	public HashMap<String, String> getParams() {
		return this.params;
	}
}
