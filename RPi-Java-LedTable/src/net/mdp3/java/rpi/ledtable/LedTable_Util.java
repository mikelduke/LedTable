/**
 * 
 */
package net.mdp3.java.rpi.ledtable;

import net.mdp3.java.rpi.ledtable.gui.TablePanel;

/**
 * @author Mikel
 *
 */
public class LedTable_Util {
	public static int tableAr[][] = new int[LedTable_Settings.ledY][LedTable_Settings.ledX * 3];
	public static byte bAr[] = new byte[LedTable_Settings.ledY * LedTable_Settings.ledX * 3 + 1];
	
	private static int tableDownPasses = 0;
	
	private static TablePanel testPanel = null;
	
	/**
	 * Fades entire table by amount per pass
	 * 
	 * @param amount
	 */
	public static void fade(int amount) {
		for (int i = 0; i < tableAr.length; i++) {
			for (int j = 0; j < tableAr[i].length; j++)
				if (tableAr[i][j] >= amount)
					tableAr[i][j] -= amount;
				else
					tableAr[i][j] = 0;
		}
	}
	
	//TODO Fix this method
	public static void moveTableUp() {
		int maxTableDownPasses = 2; //controls speed to move down
		if (tableDownPasses < maxTableDownPasses) {
			tableDownPasses++;
			return;
		} else {
			tableDownPasses = 0;
		}
		
		/*for (int i = tableAr.length-1; i >= 0; i--) {
			//byte value = tableAr[i];
			int nextValue = 0;
			if (i > LedTable_Settings.ledX * 3) nextValue = tableAr[i - LedTable_Settings.ledX * 3];
			tableAr[i] = nextValue;
		}*/
	}
	
	public static void moveTableDown() {
		//int maxTableDownPasses = 4; //controls speed to move down
		if (tableDownPasses < LedTable_Settings.ledY) {
			tableDownPasses++;
			//return;
		} else {
			tableDownPasses = 1;
		}
		
		for (int y = tableDownPasses; y < LedTable_Settings.ledY; y++) {
			for (int x = 0; x < LedTable_Settings.ledX; x++) {
				tableAr[y][x * 3] 	  = tableAr[y - 1][x * 3];
				tableAr[y][x * 3 + 1] = tableAr[y - 1][x * 3 + 1];
				tableAr[y][x * 3 + 2] = tableAr[y - 1][x * 3 + 2];
			}
		}
	}
	
	/**
	 * clearBAr
	 * 
	 * Clears bAr with all 0, then sets byte 0 to "4" to signal to arduino array mode
	 */
	@SuppressWarnings("unused")
	private static void clearBAr() {
		for (int i = 0; i < bAr.length; i++)
			bAr[i] = 0;
		bAr[0] = (byte)'4';
	}
	
	/**
	 * intArToByteAr
	 * 
	 * Converts int tableAr to byte bAr and sets byte 0 to "4"
	 */
	private static void intArToByteAr() {
		bAr[0] = (byte)'4';
		
		//Bounds check ints in tableAr for byte conversion
		for (int y = 0; y < tableAr.length; y++)
			for (int x = 0; x < tableAr[y].length; x++)
				if (tableAr[y][x] > 255) tableAr[y][x] = 255;
				else if (tableAr[y][x] < 0) tableAr[y][x] = 0; 
		
		if (LedTable_Settings.snakedLeds) {
			int cell = 1;
			for (int y = 0; y < LedTable_Settings.ledY; y++) {
				if (y % 2 == 0) {
					for (int x = 0; x < LedTable_Settings.ledX; x++) {
						bAr[cell++] = (byte)tableAr[y][x * 3];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 1];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 2];
					}
				} else {
					for (int x = LedTable_Settings.ledX - 1; x >= 0; x--) {
						bAr[cell++] = (byte)tableAr[y][x * 3];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 1];
						bAr[cell++] = (byte)tableAr[y][x * 3 + 2];
					}
				}
			}
		}
	}
	
	/**
	 * sendTableAr
	 * 
	 * Converts Static tableAr to bAr then calls send function for table serial
	 * Requires for tableSerial to be connected
	 */
	public static void sendTableAr() {
		//if (LedTable_Settings.debug) System.out.println("LedTable_Util.sendTableAr");
		
		intArToByteAr();
		
		sendTableAr(bAr);
	}
	
	/**
	 * Similar sendTableAr, but does not call intArToByteAr, and instead sends the 
	 * preformatted bAr that is passed in.
	 *  
	 * @param bAr
	 */
	public static void sendTableAr(byte bAr[]) {
		if (LedTable.tableSerial.isConnected()) {
			if (LedTable_Settings.debug) {
				//showByteArray();
				
				if (LedTable_Settings.enableGUI && LedTable_Util.testPanel != null)
					testPanel.drawTable(bAr);
			}
			
			//LedTable.tableSerial.serial.write(bAr);
			LedTable.tableSerial.writeByteAr(bAr);
		}
	}
	
	/**
	 * Debug function to dump byte array to console
	 */
	public static void showByteArray() {
		String str = "Array: \n";
		str += bArToString(bAr);
		System.out.println(str);
	}
	
	public static String bArToString(byte bAr[]) {
		String str = "";
		
		for (int i = 0; i < bAr.length; i++) {
			str += (int)(bAr[i] & 0xFF);
			str += ' ';
			if (i % 24 == 0) str += '\n';
		}
		
		return str;
	}
	
	public static void setTestPanel(TablePanel p) {
		LedTable_Util.testPanel = p;
	}
}
