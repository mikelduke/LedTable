/**
 * 
 */
package net.mdp3.java.rpi.ledtable.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author Mikel
 *
 */
public class EffectWindow extends JDialog {
	
	private final static Logger LOG = Logger.getLogger(EffectWindow.class.getName());
	private final static String name = "EffectWindow";
	
	private static final long serialVersionUID = -7970848848773218137L;
	
	private final JPanel contentPanel = new JPanel();
	
	public EffectWindow() {
		LOG.entering(name, "new");
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 350, 474);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		
		
		this.setVisible(true);
		
		LOG.exiting(name, "new");
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		EffectWindow ew = new EffectWindow();
	}
}
