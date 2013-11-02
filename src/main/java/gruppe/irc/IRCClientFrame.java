/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author HS Vikar
 */
public class IRCClientFrame extends JFrame implements ActionListener {
	
	// Variables for the menu.
	private static JMenuBar menuBar;
	private static JMenu moreMenu;
	private static JMenuItem showLogin;
	
	public TabManager thisTab;
	private String frameTitle;
	
	public IRCClientFrame() {
	
		// Fantastic GUI settings.
		setTitle("I was told this is the server");
		// Exit button should only close the program if it is the last IRC-Frame.
		// Perhaps a popup check should be sent?
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ?
		setSize(500, 500);
		setVisible(true);
		
		// Creating some JMenus? login for starters..
		menuBar = new JMenuBar();
		moreMenu = new JMenu("More..");
		showLogin = new JMenuItem("login");
		showLogin.addActionListener(this);
		moreMenu.add(showLogin);
		menuBar.add(moreMenu);
		add(menuBar, BorderLayout.NORTH);
		
		// Creating the TabManager panel.
		thisTab = new TabManager();
        add(thisTab);
        
	}
	/**
	 * Method that picks the name of this frame's connection
	 * And changes the title of this frame.
	 * 
	 * OBS! I dont know where to call this yet. - Olaf.
	 * 
	 * @param serverName : A string.
	 */
	public void updateTitle(String serverName) {
		frameTitle = serverName;
		setTitle("IRC-Client : "+frameTitle);
	}
	
	public Boolean noServerName() {
		return (frameTitle == null) ? true : false;
	}

	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getSource() == showLogin) {
			
			try {
				IRCClient.loginMenu.showem(true);
			} catch (NullPointerException npe) {
				System.out.println("couldnt even try to show");
			}
		}
		
	}
}
