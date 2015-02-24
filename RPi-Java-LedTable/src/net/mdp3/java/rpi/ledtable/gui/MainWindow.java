/**
 * 
 */
package net.mdp3.java.rpi.ledtable.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.mdp3.java.rpi.ledtable.LedTable;
import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.LedTable_Selection.Mode;

/**
 * @author Mikel
 *
 */
public class MainWindow extends JFrame implements WindowListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1823052653831657186L;
	private LedTable table;
	private TablePanel tablePanel;
	
	private JButton quitBtn;
	private JButton midiModeBtn;
	
	public static int buttonHeight = 30;
	public static int buttonWidth = 80;
	public static int border = 40;
	public static int borderX = 20;
	
	public MainWindow(LedTable table) {
		if (LedTable_Settings.debug) System.out.println("Starting GUI");
		
		this.table = table;
		
		loadComponents();
		
		this.setSize(LedTable_Settings.guiW,LedTable_Settings.guiH);
		this.setTitle("LedTable");
		this.setVisible(true);
	}
	
	private void loadComponents() {
		if (LedTable_Settings.debug) System.out.println("MainWindow.loadComponents");
		
		Container window = this.getContentPane();
		window.setLayout(null);
		this.addWindowListener(this);
		
		tablePanel = new TablePanel();
		window.add(tablePanel);
		tablePanel.setBounds(0, 0, LedTable_Settings.guiW - borderX, LedTable_Settings.guiH - buttonHeight - border);
		tablePanel.setVisible(true);
		
		quitBtn = new JButton("Quit");
		window.add(quitBtn);
		quitBtn.setBounds(LedTable_Settings.guiW - borderX - buttonWidth, LedTable_Settings.guiH - buttonHeight - border, buttonWidth, buttonHeight);
		quitBtn.addActionListener(this);
		
		midiModeBtn = new JButton("Midi Mode");
		window.add(midiModeBtn);
		midiModeBtn.setBounds(LedTable_Settings.guiW - (2 * (borderX + 90)), LedTable_Settings.guiH - buttonHeight - border, 100, buttonHeight);
		midiModeBtn.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (LedTable_Settings.debug) System.out.println("MainWindow.actionPerformed");
		
		if (e.getSource() == quitBtn) {
			table.quit();
		} else if (e.getSource() == midiModeBtn) {
			table.newSelection(new LedTable_Selection(Mode.MIDI));
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}

}
