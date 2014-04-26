package net.mdp3.java.rpi.ledtable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LedTable_Mode {
	private int mode_id = 0;
	private int mode_num = 0;
	private String mode_desc = "";
	
	public LedTable_Mode(ResultSet rs) {
		try {
			if (rs.next()) {
				int i = 1;
				mode_id    = rs.getInt(i++);
				mode_num   = rs.getInt(i++);
				mode_desc  = rs.getString(i++);
			}
		} catch (SQLException e) {
			System.out.println("Error loading result set: " + e);
			e.printStackTrace();
		}
	}
	
	public LedTable_Mode(int id, int num, String desc) {
		mode_id = id;
		mode_num = num;
		mode_desc = desc;
	}
	
	public int getId() {
		return mode_id;
	}
	
	public int getMode() {
		return mode_num;
	}
	
	public String getDesc() {
		return mode_desc;
	}
	
	@Override
	public String toString() {
		return mode_num + " " + mode_desc;
	}
}
