/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
	private static JMenuItem showLogin, showAttrC, importServers;
	
	public TabManager thisTab;
	private String frameTitle;
	private IRCClientFrame self;
	
	public IRCClientFrame() {
		int initSize = 500;
	
		// Fantastic GUI settings.
		setTitle("I was told this is the server");
		// Exit button should only close the program if it is the last IRC-Frame.
		// Perhaps a popup check should be sent?
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE ); // ? ! Because we have listener instead
        //setDefaultCloseOperation((EXIT_ON_CLOSE));
		self = IRCClientFrame.this;
		setSize(initSize, initSize);
		setVisible(true);
        setLocationRelativeTo(null);
		
		// Creating some JMenus? login for starters..
		menuBar = new JMenuBar();
		moreMenu = new JMenu(IRCClient.messages.getString("frame.more"));
		showLogin = new JMenuItem(IRCClient.messages.getString("frame.login"));
		showLogin.addActionListener(IRCClientFrame.this);
		showAttrC = new JMenuItem(IRCClient.messages.getString("frame.attribute"));
		showAttrC.addActionListener(IRCClientFrame.this);
		
        importServers = new JMenuItem(IRCClient.messages.getString("frame.import"));
        importServers.addActionListener(IRCClientFrame.this);
		moreMenu.add(showLogin);
		moreMenu.add(showAttrC);
        moreMenu.add(importServers);
		menuBar.add(moreMenu);
		add(menuBar, BorderLayout.NORTH);
		
		// Creating the TabManager panel.
		thisTab = new TabManager(self);
        add(thisTab);
        this.addWindowListener(exitListener);
        this.addComponentListener(resizeListener);
        pack();
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
			
		} else if (ae.getSource() == showAttrC) {
			try {
				IRCClient.attrC.ShowFrame(true);
			} catch (Exception e) {}
			
		} else if (ae.getSource() == importServers) {
            IRCClient.loginMenu.importServers();
        }
        
	}
	
	private WindowListener exitListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			thisTab.closeConnection();
			self.dispose();
		}
	};
	
	private ComponentAdapter resizeListener = new ComponentAdapter() {
		
		@Override
		public void componentResized(ComponentEvent e) {
			int newHeight = self.getHeight();
			thisTab.resizeTabs(newHeight);
		}

	};
}
