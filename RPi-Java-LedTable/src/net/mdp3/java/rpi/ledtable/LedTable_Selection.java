package net.mdp3.java.rpi.ledtable;

import java.sql.Date;

/**
 * 
 * @author Mikel
 *
 * Class to hold the selection from the sql table selection in the database
 */

public class LedTable_Selection {
	private long selection_id   = 0;
	private int  selection_mode = 0;
	private Date selection_date = null;
	private String selection_parm1 = "";
	private String selection_parm2 = "";
	private String selection_parm3 = "";
	private String selection_parm4 = "";
	private byte selection_parm5[];
	
	public LedTable_Selection(int s_mode, String s_p1, String s_p2, String s_p3, String s_p4, byte s_p5[]) {
		this.selection_id = -1;
		this.selection_mode = s_mode;
		this.selection_date = this.getDate();
		this.selection_parm1 = s_p1;
		this.selection_parm2 = s_p2;
		this.selection_parm3 = s_p3;
		this.selection_parm4 = s_p4;
		this.selection_parm5 = s_p5;
	}
	
	public long getId() {
		return selection_id;
	}
	
	public int getMode() {
		return selection_mode;
	}
	
	public Date getDate() {
		return selection_date;
	}
	
	public String getParm1() {
		return selection_parm1;
	}
	
	public String getParm2() {
		return selection_parm2;
	}
	
	public String getParm3() {
		return selection_parm3;
	}
	
	public String getParm4() {
		return selection_parm4;
	}
	
	public byte[] getParm5() {
		return selection_parm5;
	}
	
	public void setMode(int mode) {
		selection_mode = mode;
	}
	
	public void setParm1(String p1) {
		selection_parm1 = p1;
	}
	
	public void setParm2(String p) {
		selection_parm2 = p;
	}
	
	public void setParm3(String p3) {
		selection_parm3 = p3;
	}
	
	public void setParm4(String p4) {
		selection_parm4 = p4;
	}
	
	public void setParm1(byte b[]) {
		selection_parm5 = b;
	}
	
	@Override
	public String toString() {
		return selection_date.toString() + " " + selection_mode;
	}
	
	public boolean equals(LedTable_Selection s) {
		if (s == null) return false;
		else if (!(s instanceof LedTable_Selection)) return false;
		else if (this.getId() >= 0 && s.getId() >= 0 && s.getId() == this.getId()) return true;
		else if (s.getDate() == this.getDate() 
				&& s.getMode() == this.getMode() 
				&& s.getParm1() == this.getParm1() 
				&& s.getParm2() == this.getParm2() 
				&& s.getParm3() == this.getParm3() 
				&& s.getParm4() == this.getParm4() 
				&& s.getParm5() == this.getParm5()) 
			return true;
		else return false;
	}
}
