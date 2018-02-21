/**
 * 
 */
package net.mdp3.java.rpi.ledtable.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.mdp3.java.rpi.ledtable.LedTable;
import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.effects.EffectMode;
import net.mdp3.java.rpi.ledtable.effects.playlist.Playlist;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEvent;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEvent.PlaylistEventName;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistEventListener;
import net.mdp3.java.rpi.ledtable.effects.playlist.PlaylistItem;
import net.mdp3.java.util.file.FileNameFormatter;
import net.mdp3.java.util.gui.MenuBarHelper;
import net.mdp3.java.util.string.StringUtils;

/**
 * @author Mikel
 *
 */
public class MainWindow extends JFrame implements WindowListener, ActionListener, PlaylistEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1823052653831657186L;
	private final static Logger LOG = Logger.getLogger(MainWindow.class.getName());
	private final static String name = "MainWindow";
	
	private LedTable table;
	private TablePanel tablePanel;
	
	//private JButton quitBtn;
	//private JButton midiModeBtn;
	
	public static int buttonHeight = 30;
	public static int buttonWidth = 80;
	public static int border = 46;
	public static int borderX = 15;
	
	JMenuBar menuBar;
	JMenu menuFile;
	JMenuItem menuItemFileOpen;
	JMenuItem menuItemFileSave;
	JMenuItem menuItemFileQuit;
	
	JMenu menuEffect;
	JMenuItem menuItemEffectInfo;
	JMenuItem menuItemSaveImage;
	JMenuItem menuItemEffectEnter;
	JMenuItem menuItemEffectShowAll;
	
	JMenu menuPlaylist;
	JMenuItem menuItemPlaylistNew;
	JMenuItem menuItemPlaylistLoad;
	JMenuItem menuItemPlaylistSave;
	JMenuItem menuItemPlaylistAdd;
	JMenuItem menuItemPlaylistView;
	JMenuItem menuItemPlaylistRename;
	JMenuItem menuItemPlaylistPlayStop;
	JMenuItem menuItemPlaylistNext;
	
	JMenu menuConfigure;
	JMenuItem menuItemConfigureGUI;
	JMenuItem menuItemConfigureTable;
	
	public MainWindow(LedTable table) {
		LOG.entering(name, "MainWindow");
		
		this.table = table;
		
		loadComponents();
		buildMenu();
		
		this.setSize(LedTable_Settings.guiW,LedTable_Settings.guiH);
		this.setTitle("LedTable");
		this.setVisible(true);
		
		table.getCurrentPlaylist().addPlaylistEventListener(this);
		
		LOG.exiting(name, "MainWindow");
	}
	
	private void loadComponents() {
		LOG.entering(name, "loadComponents");
		
		Container window = this.getContentPane();
		window.setLayout(null);
		this.addWindowListener(this);
		
		tablePanel = new TablePanel();
		window.add(tablePanel);
		tablePanel.setBounds(0, 5, LedTable_Settings.guiW - borderX, LedTable_Settings.guiH - buttonHeight - border);
		tablePanel.setVisible(true);
		table.table.addWriteListener(tablePanel);
		
		/*quitBtn = new JButton("Quit");
		window.add(quitBtn);
		quitBtn.setBounds(LedTable_Settings.guiW - borderX - buttonWidth, LedTable_Settings.guiH - buttonHeight - border, buttonWidth, buttonHeight);
		quitBtn.addActionListener(this);
		
		midiModeBtn = new JButton("Midi Mode");
		window.add(midiModeBtn);
		midiModeBtn.setBounds(LedTable_Settings.guiW - (2 * (borderX + 90)), LedTable_Settings.guiH - buttonHeight - border, 100, buttonHeight);
		midiModeBtn.addActionListener(this);*/
		
		LOG.exiting(name, "loadComponents");
	}
	
	private void buildMenu() {
		menuBar = new JMenuBar();
		
		buildFileMenu();
		buildEffectMenu();
		buildPlaylistMenu();
		buildConfigureMenu();
		
		//menuBar.add(Box.createHorizontalGlue());
		//menuBar.add(menuItemFileQuit);
		
		this.setJMenuBar(menuBar);	
	}
	
	private void buildFileMenu() {
		this.menuFile = MenuBarHelper.addNewMenuOption(menuBar, "File", KeyEvent.VK_F, "File Menu");
		
		this.menuItemFileOpen = MenuBarHelper.addNewMenuItem(this.menuFile, "Open", KeyEvent.VK_O, "Open Preset", this);
		this.menuItemFileSave = MenuBarHelper.addNewMenuItem(menuFile, "Save", KeyEvent.VK_S, "Save Preset", this);
		this.menuItemFileQuit = MenuBarHelper.addNewMenuItem(menuFile, "Quit", KeyEvent.VK_Q, "Quit", this);
	}
	
	private void buildEffectMenu() {
		this.menuEffect = MenuBarHelper.addNewMenuOption(menuBar, "Effect", KeyEvent.VK_E, "Effect Option Menu");
		
		this.menuItemEffectInfo  = MenuBarHelper.addNewMenuItem(menuEffect, "Effect Info", KeyEvent.VK_I, "Get Effect Info", this);
		this.menuItemSaveImage   = MenuBarHelper.addNewMenuItem(menuEffect, "Save Current Image", KeyEvent.VK_S, "Save Current Image", this);
		this.menuItemEffectEnter = MenuBarHelper.addNewMenuItem(menuEffect, "Enter Command", KeyEvent.VK_E, "Enter Effect Command", this);
		this.menuItemEffectShowAll = MenuBarHelper.addNewMenuItem(menuEffect, "Show Effects", KeyEvent.VK_H, "Show Effects", this);
	}
	
	private void buildPlaylistMenu() {
		this.menuPlaylist = MenuBarHelper.addNewMenuOption(menuBar, "Playlist", KeyEvent.VK_P, "Playlist Menu");
		
		this.menuItemPlaylistNew    = MenuBarHelper.addNewMenuItem(menuPlaylist, "New",    KeyEvent.VK_N, "New Playlist",  this);
		this.menuItemPlaylistLoad   = MenuBarHelper.addNewMenuItem(menuPlaylist, "Load",   KeyEvent.VK_L, "Load Playlist", this);
		this.menuItemPlaylistSave   = MenuBarHelper.addNewMenuItem(menuPlaylist, "Save",   KeyEvent.VK_S, "Save Playlist", this);
		this.menuItemPlaylistAdd    = MenuBarHelper.addNewMenuItem(menuPlaylist, "Add",    KeyEvent.VK_A, "Add Current Selection to Playlist", this);
		this.menuItemPlaylistView   = MenuBarHelper.addNewMenuItem(menuPlaylist, "View",   KeyEvent.VK_V, "View Playlist", this);
		this.menuItemPlaylistRename = MenuBarHelper.addNewMenuItem(menuPlaylist, "Rename", KeyEvent.VK_R, "Rename Playlist", this);
		this.menuItemPlaylistPlayStop = MenuBarHelper.addNewMenuItem(menuPlaylist, "Play", KeyEvent.VK_P, "Play or Stop the playlist", this);
		this.menuItemPlaylistNext   = MenuBarHelper.addNewMenuItem(menuPlaylist, "Next", KeyEvent.VK_X, "Advance the Playlist", this);
	}
	
	private void buildConfigureMenu() {
		this.menuConfigure = MenuBarHelper.addNewMenuOption(menuBar, "Configure", KeyEvent.VK_C, "Configure Settings");
		
		this.menuItemConfigureGUI = MenuBarHelper.addNewMenuItem(menuConfigure, "GUI Settings", KeyEvent.VK_G, "GUI Settings", this);
		this.menuItemConfigureTable = MenuBarHelper.addNewMenuItem(menuConfigure, "Table Settings", KeyEvent.VK_T, "Table Settings", this);
	}
	
	/**
	 * Handles the Open File Dialog and calls the Selection Load routine
	 */
	private void loadSelection() {
		//TODO load to remote LedTable Webservice instead of local, maybe add popup to set ip?
		FileFilter ff = new FileNameExtensionFilter("LedTable XML", "xml");
		final JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(ff);
		//if (HiResSettings.lastFileOpenFolder.length() > 0) fc.setCurrentDirectory(new File(HiResSettings.lastFileOpenFolder));
		if (LedTable_Settings.defaultSaveFolder.length() > 0) fc.setCurrentDirectory(new File(LedTable_Settings.defaultSaveFolder + "/selections/"));
		
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			String fileName = file.getName();
			if (fileName.endsWith("xml")) {
				try {
					LedTable_Selection s = new LedTable_Selection(EffectMode.OFF);
					s.load(file);
					table.newSelection(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//HiResSettings.lastFileOpenFolder = file.getParent();
		}
	}
	
	/**
	 * Handles the Open File Dialog and calls the Selection Load routine
	 */
	private void loadPlaylist() {
		FileFilter ff = new FileNameExtensionFilter("LedTable XML", "xml");
		final JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(ff);
		//if (HiResSettings.lastFileOpenFolder.length() > 0) fc.setCurrentDirectory(new File(HiResSettings.lastFileOpenFolder));
		if (LedTable_Settings.defaultSaveFolder.length() > 0) fc.setCurrentDirectory(new File(LedTable_Settings.defaultSaveFolder + "/playlists/"));
		
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			String fileName = file.getName();
			if (fileName.endsWith("xml")) {
				try {
					table.getCurrentPlaylist().load(file);
					
					if (table.getCurrentPlaylist().getEffectList().size() > 0)
						table.newSelection(table.getCurrentPlaylist().getEffectList().get(0).getSelection());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Shows and input dialog with the current name of the playlist, and 
	 * renames it if the input value is not null and greater than 0
	 */
	private void playlistRename() {
		String newName = "";
		newName = JOptionPane.showInputDialog(this, "Enter the playlist name", this.table.getCurrentPlaylist().getPlaylistName());
		LOG.fine("New Playlist Name: " + newName);
		if (newName != null && newName.length() > 0) {
			this.table.getCurrentPlaylist().setPlaylistName(newName);
		}
	}
	
	/**
	 * Advances the playlist and starts the new effect playing 
	 * 
	 */
	private void playlistNext() {
		this.table.getCurrentPlaylist().next();
	}
	
	/**
	 * Adds the currently visible selection to the playlist, popups a dialog 
	 * to get the playtime value 
	 */
	private void playlistAdd() {
		//TODO add dialog saying it was added
		//TODO add error dialog when invalid time is entered
		
		String newTime = "";
		newTime = JOptionPane.showInputDialog(this, "Enter the effect display time in seconds", PlaylistItem.DEFAULT_PLAYTIME);
		newTime = newTime.trim();
		
		int newTimeI = PlaylistItem.DEFAULT_PLAYTIME;
		try {
			newTimeI = StringUtils.tryParseInt(newTime);
			
			boolean added = table.getCurrentPlaylist().addSelectionToPlaylist(newTimeI, table.getCurrentSelection());
			if (added) LOG.info("Item added to list");
			else LOG.info("Item not added");
		} catch (Exception e) {
			//e.printStackTrace();
			LOG.severe("Invalid Time Entry, List add not attempted");
		}
	}
	
	/**
	 * Saves the current selection and shows error dialog if exception occurs
	 */
	private void saveSelection() {
		try {
			//TODO Change to use a save dialog, or add a new save option
			table.saveSelection();
		} catch (IOException | TransformerFactoryConfigurationError
				| TransformerException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this.getFocusOwner(), "Error Saving File");
		}
	}
	
	/**
	 * Saves the current playlist with autogenerated name
	 */
	private void savePlaylist() {
		try {
			table.getCurrentPlaylist().save();
		} catch (IOException | TransformerFactoryConfigurationError
				| TransformerException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this.getFocusOwner(), "Error Saving File");
		}
	}
	
	/**
	 * Saves whatever is currently displayed on the table panel on screen to 
	 * an image file, with the name autogenerated
	 */
	private void saveImage() {
		try {
			String fileName = FileNameFormatter.getDatedFileName(LedTable_Settings.defaultFilePrefix, "bmp");
			
			tablePanel.saveTable(LedTable_Settings.defaultSaveFolder + "//" + fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
			
			JOptionPane.showMessageDialog(this.getFocusOwner(), "Error Saving Image File");
			
			LOG.severe(Arrays.toString(e1.getStackTrace()));
		}
	}
	
	/**
	 * Shows effect info in popup for currently running effect
	 */
	private void showEffectInfo() {
		String info = table.getEffect().getEffectInfo().toString();
		
		JOptionPane.showMessageDialog(this.getFocusOwner(), info);
	}
	
	private void playlistPlayStop() {
		//TODO add stopping the playlist when effects are opened or changed manually
		if (this.table.getCurrentPlaylist().isPlaying()) {
			this.table.getCurrentPlaylist().stop();
		} else {
			this.table.getCurrentPlaylist().play();
		}
	}
	
	private void effectEnterCmd() {
		String command = "";
		command = JOptionPane.showInputDialog(this, "WS Command", this.table.getCurrentSelection().toWSCmd());
		LOG.info("Command Entered: " + command);
		
		if (command != null && command.length() > 0) {
			try {
				LedTable_Selection s = new LedTable_Selection(command);
				this.table.newSelection(s);
			} catch (Exception e) {
				//TODO Add error dialog
				e.printStackTrace();
				LOG.severe("Error loading from command: " + command);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.entering(name, "actionPerformed", "Source: "+ e.getSource());
		
		//File Menu
		if (e.getSource() == menuItemFileQuit) {
			table.quit();
		} else if (e.getSource() == menuItemFileSave) {
			saveSelection();
		} else if (e.getSource() == menuItemFileOpen) {
			loadSelection();
		}
		
		//Effect Menu
		else if (e.getSource() == menuItemEffectInfo) {
			showEffectInfo();
		} else if (e.getSource() == menuItemSaveImage) {
			saveImage();
		} else if (e.getSource() == menuItemEffectEnter) {
			effectEnterCmd();
		} else if (e.getSource() == menuItemEffectShowAll) {
			//TODO add window where you can select an effect
			for (EffectMode em: EffectMode.values()) {
				System.out.println(em + " " + em.getDescName());
			}
		}
		
		//Playlist Menu
		else if (e.getSource() == this.menuItemPlaylistAdd) {
			playlistAdd();
		} else if (e.getSource() == this.menuItemPlaylistLoad) {
			loadPlaylist();
		} else if (e.getSource() == this.menuItemPlaylistNew) {
			table.setCurrentPlaylist(new Playlist());
		} else if (e.getSource() == this.menuItemPlaylistRename) {
			playlistRename();
		} else if (e.getSource() == this.menuItemPlaylistSave) {
			savePlaylist();
		} else if (e.getSource() == this.menuItemPlaylistView) {
			//TODO replace with dialog
			LOG.info("Current Playlist: " + table.getCurrentPlaylist());
		} else if (e.getSource() == this.menuItemPlaylistPlayStop) {
			playlistPlayStop();
		} else if (e.getSource() == this.menuItemPlaylistNext) {
			playlistNext();
		} //TODO Add Change Play Length for current selection in playlist option
		
		//Buttons (Now Hidden on screen)
		/*else if (e.getSource() == quitBtn) {
			table.quit();
		} else if (e.getSource() == midiModeBtn) {
			try {
				table.newSelection(new LedTable_Selection(EffectMode.MIDI));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}*/
		
		LOG.exiting(name, "actionPerformed");
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		table.quit();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		table.quit();
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

	@Override
	public void playlistEvent(PlaylistEvent pev) {
		if (pev.getEventName() == PlaylistEventName.PLAYBACK_PLAY) {
			this.menuItemPlaylistPlayStop.setText("Stop");
		} else if (pev.getEventName() == PlaylistEventName.PLAYBACK_STOP) {
			this.menuItemPlaylistPlayStop.setText("Play");
		}
	}

}
