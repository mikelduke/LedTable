/**
 * 
 */
package net.mdp3.java.util.gui;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author Mikel
 *
 * Class to help with adding Menu Items and Menu Bar options to a menu
 * 
 */
public class MenuBarHelper {
	/**
	 * Initializes a JMenu choice and adds it to the menu bar
	 * 
	 * @param menu
	 * @param menuOption
	 * @param optionName
	 * @param optionMnemonic
	 * @param desc
	 */
	public static JMenu addNewMenuOption(JMenuBar menu, String optionName, int optionMnemonic, String desc) {
		JMenu menuOption = new JMenu(optionName);
		
		if (optionMnemonic != 0) 
			menuOption.setMnemonic(optionMnemonic);
		
		if (desc != null && !desc.equals("")) 
			menuOption.getAccessibleContext().setAccessibleDescription(desc);
		
		if (menu != null) menu.add(menuOption);
		
		return menuOption;
	}
	
	/**
	 * Initializes a JMenu choice and adds it to the menu bar
	 * 
	 * @param menu
	 * @param optionName
	 * @return
	 */
	public static JMenu addNewMenuOption(JMenuBar menu, String optionName) {
		return MenuBarHelper.addNewMenuOption(menu, optionName, 0, optionName);
	}
	
	/**
	 * Initializes and adds a new menu item to the menu option on the menu bar
	 * 
	 * @param menuOption
	 * @param newItem
	 * @param itemName
	 * @param itemShortcut
	 * @param itemDesc
	 * @param actionListener
	 */
	public static JMenuItem addNewMenuItem(JMenu menuOption, String itemName, int itemShortcut, String itemDesc, ActionListener actionListener) {
		JMenuItem newItem = null;
		
		if (itemShortcut != 0)
			newItem = new JMenuItem(itemName, itemShortcut);
		else newItem = new JMenuItem(itemName);
		
		//quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		
		if (itemDesc != null && !itemDesc.equals(""))
			newItem.getAccessibleContext().setAccessibleDescription(itemDesc);
		
		if (actionListener != null)
			newItem.addActionListener(actionListener);
		
		if (menuOption != null) menuOption.add(newItem);
		
		return newItem;
	}
	
	/**
	 * Initializes and adds a new menu item to the menu option on the menu bar
	 *  
	 * @param menuOption
	 * @param itemName
	 * @param actionListener
	 * @return
	 */
	public static JMenuItem addNewMenuItem(JMenu menuOption, String itemName, ActionListener actionListener) {
		return MenuBarHelper.addNewMenuItem(menuOption, itemName, 0, itemName, actionListener);
	}
}
