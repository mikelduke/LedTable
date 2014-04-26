package net.mdp3.java.rpi.ledtable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author Mikel
 *
 * Class to contain the sql functionality for the led table app
 * This reads selections and settings from the database that will be handled and 
 * output to the table over serial 
 */

public class LedTable_SQL {
	private Connection conn = null;
	private String urlPrefix = "jdbc:mysql://";
	private String url = "";
	
	private boolean isConnected = false;
	
	public LedTable_SQL(String host, String user, String pass, String tableName) {
		setURL(host, user, pass, tableName);
	}
	
	public boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url);
			
			isConnected = true;
			return true;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.out.println("Error Loading Driver: " + e);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Error connecting: " + e);
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean disconnect() {
		if (isConnected) {
			try {
				conn.close();
				isConnected = false;
				return true;
			} catch (SQLException e) {
				System.out.println("Error closing connection: " + e);
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void setURL(String host, String user, String pass, String tableName) {
		url = urlPrefix + host + "/" + tableName + "?user=" + user + "&password=" + pass;
		if (LedTable_Settings.debug) System.out.println("Database URL: " + url);
	}
	
	public String getURL() {
		return url;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	/**
	 * 
	 * @param cmd
	 * @return ResultSet
	 * 
	 * Runs the sql command and returns ResultSet object
	 */
	public ResultSet runCommand(String cmd) {
		if (isConnected) {
			try {
				Statement st = conn.createStatement();
				return st.executeQuery(cmd);
				
			} catch (SQLException e) {
				System.out.println("Error running sql: " + e);
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Not connected to sql server");
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return LedTable_Selection
	 * 
	 * Method to get last selection and automatically load it into a selection object for 
	 * easy use
	 */
	public LedTable_Selection getLastSelection() {
		if (!isConnected) return null;
		
		String sql = "Select * from selection order by selection_date desc limit 1";
		
		return new LedTable_Selection(runCommand(sql));
	}
	
	/*public LedTable_Mode[] getModes() {
		if (!isConnected) return null;
		LedTable_Mode modeAr[];
		
		String sql = "Select * from arduino_mode order by mode_seq";
		
		ResultSet rs = runCommand(sql);
		
		return modeAr;
	}*/
}
