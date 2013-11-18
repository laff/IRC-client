/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 *
 * @author HS Vikar
 */
public class ServerTab extends GenericTab {
	
	JButton quit;
	/**
	 * Constructor for ServerTab
	 * @param mng Pointer to parent TabManager
	 * @param dim The dimension of the IRCClientFrame
	 */
	public ServerTab(TabManager mng, Dimension dim) {
		super("Server", mng, dim);
		
		quit = new JButton("Close window");
		quit.addActionListener(new QuitListener());
		
		add(quit, BorderLayout.NORTH);
	}
	
	/**
	 * ActionListener for the quit-button
	 * Sends quit-message to server and calls closeConnection
	 * from TabManager
	 */
	class QuitListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			writeToLn("QUIT");
			manager.closeConnection();
		}
		
	}

}
