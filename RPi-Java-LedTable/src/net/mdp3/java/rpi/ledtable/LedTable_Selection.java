package net.mdp3.java.rpi.ledtable;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	public LedTable_Selection(ResultSet rs) {
		//if (LedTable_Settings.debug) System.out.println("Selection: " + rs.toString());
		
		try {
			if (rs.next()) {
				int i = 1;
				selection_id    = rs.getLong(i++);
				selection_mode  = rs.getInt(i++);
				selection_date  = rs.getDate(i++);
				selection_parm1 = rs.getString(i++);
				selection_parm2 = rs.getString(i++);
				selection_parm3 = rs.getString(i++);
				selection_parm4 = rs.getString(i++);
				selection_parm5 = rs.getBytes(i++);
			}
		} catch (SQLException e) {
			System.out.println("Error loading result set: " + e);
			e.printStackTrace();
		}
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
		else if (s.getId() == this.getId()) return true;
		else return false;
	}
}
