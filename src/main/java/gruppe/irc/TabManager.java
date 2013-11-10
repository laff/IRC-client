/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package gruppe.irc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 *
 * @author HS Vikar
 * 
 * TODO: When we click a tab, the focus should be set to the InternalFrame that 
 * tab belongs to.
 */


public class TabManager extends JPanel implements ActionListener {
	
	private JInternalFrame serverTab;
	private Vector<JInternalFrame> channelTabs = new Vector<JInternalFrame>();
	private Vector<JInternalFrame> personalTabs = new Vector<JInternalFrame>();
	
	// This TabManager's IRCConnection
	private IRCConnection connection;
	
	/*
	 * Each user is distinguished from other users by a unique nickname
	 * having a maximum length of nine (9) characters.  See the protocol
	 * grammar rules (section 2.3.1) for what may and may not be used in a
	 * nickname.
	 * While the maximum length is limited to nine characters, clients
	 * SHOULD accept longer strings as they may become used in future
	 * evolutions of the protocol.
	 * 
	 * PS! Should rather have this inside the loginmenu logic.
	 */
	private final int maxNickLength = 9;
	
	
	// The server name this TabManager is connected to.
	private String serverName, nick, altNick;
	
	// A Bunch of components
	private JTabbedPane tabbedPane;

    private JDesktopPane desktop;

	private JScrollPane scrollPane;
	private BorderLayout layout;
	
	// These components are used wihtin the servertab
	private JTextField write;
	private JButton quit;
	private JTextPane text;
	
	public TabManager () {
	
        setLayout(new BorderLayout());
		setVisible(true);
		
        desktop = new JDesktopPane();
		
        serverTab = createServerTab();
   
        desktop.add(serverTab);
        
        add(desktop);

        tabbedPane = new JTabbedPane();
		
        tabbedPane.addTab("ServerTab", null, new JPanel(), "Main window for server communication");
		
        add(tabbedPane, BorderLayout.NORTH);
		
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
	}
	
	
	public void setConnection (IRCConnection ourConnection) {
		connection = ourConnection;
		serverName = connection.getServerName();
		nick = connection.getNick();
		//altNick = connection.getAltNick();
	}
    
    public IRCConnection getConnection () {
        return connection;
    }
	
	public JInternalFrame createServerTab() {
		JInternalFrame intFrame = new JInternalFrame();
		
        intFrame.setLayout(new BorderLayout());
        intFrame.add(scrollPane = new JScrollPane(text = new JTextPane()), BorderLayout.CENTER);
        write = new JTextField();
        intFrame.add(write, BorderLayout.SOUTH);
 
		write.addActionListener(this);

		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);
     
		quit = new JButton("Close connection");
		quit.addActionListener(this);
        
        intFrame.add(quit, BorderLayout.NORTH);
        intFrame.setVisible(true);
		
        try { 
        	intFrame.setMaximum(true);
        } catch (Exception e) {
        	// TODO: Error logging
        }
		
		
		
		return intFrame;
	}
	
		
	/**
	 * Function that distributes messages to appropriate tabs.
	 * @param : message containing a message.
	 * @param : command conatining a code.
	 * @param : prefix containing the servername
	 * @param : server The servername as received by the message
	 */
	public void distributeMessage (String prefix, String command, String alias, String server, String message) {
		
		if (server.equals(serverName) && alias.equals(nick)) {
			
			// if the command matches "PRIVMSG" we have a personal message incoming.
			if ( command.equals("PRIVMSG") ) {

				distributePrivate(prefix, message);
			
			// else if the command incoming matches "POTATO MASH SO GOOD WHEN HUNGRY" we have a problem on our hands.
			} else if ( command.equals("POTATO") ) {
				
				distributeChannel("tomato", "potato", "scissors");
			
			// else Add the rest to the local servertab.
			} else {
				
				addText(prefix, command, message);
			}
		}
	}
	
	/**
	 * Function that takes care of distributing messages to the personaltabs.
	 * 
	 * Not yet implemented.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param arg3 
	 */
	private void distributeChannel(String arg1, String arg2, String arg3) {
		
	}
	
	/**
	 * Function that takes care of distributing messages to the personaltabs.
	 * @param prefix 
	 * @param message 
	 */
	private void distributePrivate(String prefix, String message) {
		
		int personalCount = personalTabs.size();
		boolean noFoundTab = true;

		// Goes through the personal tabs to find one that matches our description.
		// Sets the noFoundTab variable to false if there was found a tab that match.
		for (int i = 0; i < personalCount; ++i) {

			PersonalTab pTab = (PersonalTab)personalTabs.elementAt (i);
			
			if ( pTab.getFilter().equals(prefix) ) {
				
				pTab.addText(message);
				noFoundTab = false;
				
			}
		}

		// Now, if there is not found a personal tab matching our description, a new one must be made.
		if (noFoundTab) {

			PersonalTab newPersonalTab = new PersonalTab (prefix);
			personalTabs.add(newPersonalTab);

			String tabName = "Private " + prefix.substring( 0, prefix.indexOf("!") );
			// update function adding stuff to the tabbedpane?
			tabbedPane.addTab(tabName, null, newPersonalTab, "no action");
			try {
				newPersonalTab.setMaximum(true);
			} catch (Exception e) {
				System.out.println( "TabManager::distributeMessage: Error setting tab size " + e.getMessage() ); 
			}

		}
	}
	
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

	public void actionPerformed(ActionEvent e) {
		
				if (e.getSource() == write) {
			
			// First our request is added to the textArea.
			//addText(write.getText()+"\n");
			
			// Then the request is sent to the server. 
			// The answers are then put into the textarea by the message() function in IRCConnection.
			//TabManager.getConnection().writeln(write.getText());
			
			writeToLn(write.getText());
			
			write.setText("");
		}
				else if (e.getSource() == quit) {
			connection.close();
		}
	}
	/**
	 * Static function that takes the string parameter and sends to connection and its writeln function.
	 * @param msg 
	 */
	public void writeToLn(String msg) {
		connection.writeln(msg);
	}
	
    /** Returns an ImageIcon, or null if the path was invalid. */
	/*
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TabbedPaneDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	*/
	
		public void addText(String prefix, String command, String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();
        
        String test = "\nPrefix: " + prefix + "\nCommand: " + command + "\nMessage: " + msg + "\n";
		
		
		// Logic that checks if the messages from IRC-client (IRCConnection) is meant for this tabmanager. 
		try {	
			text.getStyledDocument().insertString(pos, test, null);
		} catch (BadLocationException ble) {};					
		

        //When new messages appears in the window, it scrolls down automagically.
        //Borrowed from Oyvind`s example.
        SwingUtilities.invokeLater(new Thread() {
	        public void run() {
	        	// Get the scrollbar from the scroll pane
	        	JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
	        	// Set the scrollbar to its maximum value
	        	scrollbar.setValue(scrollbar.getMaximum());
	        }
	    });
	}
	
	/*
	 * Function that calls the close function of this tabs connection.
	 */
	public void closeConnection() {
		try {
			connection.close();
		} catch (NullPointerException npe) {
			System.out.println("TabManager::closeConnection: Error closing connection " + npe.getMessage());
		}
	}

	/**
	 * Return state variable of IRCConnection
	 * @returns connection state
	 */
	public int getConnectionState() {
		return connection.getState();
	}
}
