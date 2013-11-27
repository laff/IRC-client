package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Olaf
 */
public class IRCClientFrame extends JFrame implements ActionListener {
	
	// Variables for the menu.
	private JMenuBar menuBar;
	private JMenu moreMenu, serverMgmt, actions, help;
	private JMenuItem   showLogin, 
                        showAttrC, 
                        importServers, 
                        addServer, 
                        listUsers,
                        about, 
                        helpContent;
	
	public TabManager thisTab;
	private String frameTitle;
	private IRCClientFrame self;
	
	public IRCClientFrame() {
		int initSize = 500;
	
		// Fantastic GUI settings.
		setTitle(IRCClient.messages.getString("frame.title"));
		// Exit button should only close the program if it is the last IRC-Frame.
		// Perhaps a popup check should be sent?
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		self = IRCClientFrame.this;
		setSize(initSize, initSize);
		setVisible(true);
        setLocationRelativeTo(null);
		
        
		menuBar = new JMenuBar();
		moreMenu = new JMenu(IRCClient.messages.getString("frame.more"));
		showLogin = new JMenuItem(IRCClient.messages.getString("frame.login"));
		showLogin.addActionListener(IRCClientFrame.this);
		showAttrC = new JMenuItem(IRCClient.messages.getString("frame.attribute"));
		showAttrC.addActionListener(IRCClientFrame.this);
		
        serverMgmt = new JMenu(IRCClient.messages.getString("frame.srvMgmt"));
        importServers = new JMenuItem(IRCClient.messages.getString("frame.import"));
        importServers.addActionListener(IRCClientFrame.this);
        addServer = new JMenuItem(IRCClient.messages.getString("frame.addServ"));
        addServer.addActionListener(IRCClientFrame.this);
        
        actions = new JMenu(IRCClient.messages.getString("frame.action"));
        listUsers = new JMenuItem(IRCClient.messages.getString("frame.list"));
        listUsers.addActionListener(IRCClientFrame.this);
        
        help = new JMenu(IRCClient.messages.getString("frame.help"));
        about = new JMenuItem(IRCClient.messages.getString("frame.about"));
        about.addActionListener(IRCClientFrame.this);
        helpContent = new JMenuItem(IRCClient.messages.getString("frame.helpContent"));
        helpContent.addActionListener(IRCClientFrame.this);
        
        serverMgmt.add(importServers);
        serverMgmt.add(addServer);
		moreMenu.add(showLogin);
		moreMenu.add(showAttrC);
        actions.add(listUsers);
        help.add(about);
        help.add(helpContent);
		menuBar.add(moreMenu);
        menuBar.add(serverMgmt);
        menuBar.add(actions);
        menuBar.add(help);
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
	
    public String getServerName() {
        return frameTitle;
    }
    
	/**
	 * Different actions for our menuitems.
	 * @param ae 
	 */

	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getSource() == showLogin) {
			
			try {
				IRCClient.loginMenu.showem(true);
			} catch (NullPointerException npe) {
				System.out.println("couldnt even try to show");
			}
			
		} else if (ae.getSource() == showAttrC) {
			try {
				IRCClient.attrC.showFrame(true);
			} catch (Exception e) {}
			
		} else if (ae.getSource() == importServers) {
            IRCClient.loginMenu.importServers();
            
        } else if (ae.getSource() == addServer) {
            IRCClient.loginMenu.addServer();
            
        } else if (ae.getSource() == listUsers) {
            thisTab.writeToLn("LIST");
			
        } else if (ae.getSource() == helpContent) {
			displayHelp();
			
        } else if (ae.getSource() == about) {
			JOptionPane.showMessageDialog(
                null, 
                IRCClient.messages.getString("frame.aboutText"), 
                IRCClient.messages.getString("frame.about"), 
                JOptionPane.INFORMATION_MESSAGE
            );
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
	
	/**
	 * Function that displays some guidelines for how to use the application.
	 * Hardcoded, and very simple.
	 */
	private void displayHelp() {
		String helpOutput = "";
		
		for (int i = 1; i < 8; i++) {
			
			helpOutput = helpOutput+i+": "+IRCClient.messages.getString("frame.helpText"+i)+"\n";
		}
		JOptionPane.showMessageDialog(
            null, 
            helpOutput, 
            IRCClient.messages.getString("frame.help"), 
            JOptionPane.PLAIN_MESSAGE
        );
	}
}
