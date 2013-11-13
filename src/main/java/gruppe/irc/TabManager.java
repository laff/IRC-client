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
	
	private JPanel serverTab;
	private Vector<GenericTab> channelTabs = new Vector<GenericTab>();
	private Vector<GenericTab> personalTabs = new Vector<GenericTab>();
	
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
	
	// These components are used within the servertab
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
        tabbedPane.addTab("Server", serverTab);//"ServerTab", null, new JPanel(), "Main window for server communication");
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
	
	public JPanel createServerTab() {
		JPanel intFrame = new JPanel();
		
        intFrame.setLayout(new BorderLayout());
        intFrame.add(scrollPane = new JScrollPane(text = new JTextPane()), BorderLayout.CENTER);
        
        write = new JTextField();
        write.addActionListener(this);
        intFrame.add(write, BorderLayout.SOUTH);

		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);
     
		quit = new JButton("Close connection");
		quit.addActionListener(this);
        
        intFrame.add(quit, BorderLayout.NORTH);
        intFrame.setVisible(true);
        intFrame.setPreferredSize(new Dimension(0, 420));
		
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
		String chanName, restMessage;
        
		if (server.equals(serverName) && alias.equals(nick)) {
                     System.out.println("I distributeMessage er message lik: "+message);
                     System.out.println("I distributeMessage er command lik: "+command);
                     System.out.println("I distributeMessage er prefix lik: "+prefix);
			// If the command matches "PRIVMSG" we have a personal message incoming,
            // unless the message starts with a '#', then it is a channel-message.
			if (command.equals("PRIVMSG") && !message.startsWith("#")) {
				distributePrivate(prefix, message);
			
            // If we get a PRIVMSG command, and a channelname specified with a '#', then
            // this message is meant for a specific tab that already exists, because
            // a user cannot receive messages from a channel he has not joined.
			} else if (command.equals("PRIVMSG") && message.startsWith("#")) {
				chanName = message.substring(message.indexOf("#"), message.indexOf(" "));
                restMessage = message.substring(message.indexOf(":")+1, message.length());
				distributeChannel(prefix, chanName, restMessage);
			
			} else if (command.equals("JOIN") || command.equals("PART")) {
            
                // TODO: If a JOIN or PART command appears here, the list of
                // users on the right side must be updated. Either did someone
                // leave the chan, or someone joined it. (And it might be us that
                // joined or leaved. If we leaved, we can just close the tab.
                // If we joined, a NAMES-command is automatically executed, 
                // (actually the command is: NAMES #channelname), this will list
                // all the users. The 'command id' for this is 353.
                // And the message looks like this: OurNick = #channelName :Ournick
                // OtherUser1 OtherUser2 etc.
                // The users with OP shall be listed first(says the project-text), 
                // and they will be listed first when the NAMES-command is issued.
                // If a user who joins AFTER us, is autopromoted OP, he also should be
                // listed over the "normal" users. So maybe the best solution is to
                // do the NAMES-command again for each JOIN/PART?
                
            
                
            // Else add the rest to the local servertab.
            } else {
				addText(message);
			}
		}
	}
	
	/**
     * Function that takes care of distributing messages to the right channeltab.
     * Goes through all the channelTabs to find the channel with the name that
     * this message is meant for.
     * At this point, the channel will always exist in the Vector.
     * @param chanName Name of the channel where the message shall be displayed.
     * @param message The message to display in a channelwindow.
     */
    
	private void distributeChannel(String prefix, String chanName, String message) {
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            // If the current tab has the corresponding channelname,
            // we can add the message to that channel.
            if (chanTab.getFilter().equals(chanName)) {
                chanTab.addText(prefix, message, true);
            }
        }
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
			
			if (pTab.getFilter().equals(prefix)) {
				
				pTab.addText(prefix, message, true);
				noFoundTab = false;
			}
		}

		// Now, if there is not found a personal tab matching our description, a new one must be made.
		if (noFoundTab) {

			PersonalTab newPersonalTab = new PersonalTab(prefix, this);
			personalTabs.add(newPersonalTab);

			attachTab(prefix, newPersonalTab);

			newPersonalTab.addText(prefix, message, true);
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
		String fromText;
        
        if (e.getSource() == write) {
			fromText = write.getText();
            // If the text inserted in the write-field starts with JOIN #, 
            // then we want to create a channeltab with that name.
            if (fromText.startsWith("JOIN #")) {
                createChannelTab(fromText);
                
           // If it`s just a 'regular' message, we add it to the textarea. 
            } else addText(fromText+"\n");

			writeToLn(fromText);
			write.setText("");
            
		} else if (e.getSource() == quit) {
			connection.close();
		}
	}
    
    /**
     * Method to create a new tab, for a channel that the user wants to join.
     * The channel is added to the Vector with all the other channeltabs, and
     * a tabbedPane is also created.
     * @param fromText a text that include a channel name to join.
     */
    
    private void createChannelTab(String fromText) {
        String chanName, tabName;
        ChannelTab chanTab;
        
        chanName = fromText.substring(fromText.indexOf("#"));
        chanTab = new ChannelTab(chanName, this);
        channelTabs.addElement(chanTab);
        tabName = chanName;
        tabbedPane.addTab(tabName, null, chanTab);
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
	
    public void addText (String msg) { //(String prefix, String command, String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();

      //  String test = "\nPrefix: " + prefix + "\nCommand: " + command + "\nMessage: " + msg + "\n";


        // Logic that checks if the messages from IRC-client (IRCConnection) is meant for this tabmanager. 
        try {	
            text.getStyledDocument().insertString(pos, msg, null);
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
	
	/**
	 * Method deletes the contents of tab and calls releaseTab to remove it from tabManager.
	 * @param filter The filter text of the tab to be closed
	 */
	
	public void closeTab(String filter) {
		
		int personalCount = personalTabs.size();
        PersonalTab pTab;

		// Goes through the personal tabs to find one that matches our description.
		// Removes element from vector
		for (int i = 0; i < personalCount; ++i) {

			pTab = (PersonalTab)personalTabs.elementAt(i);
			
			if (pTab.getFilter().equals(filter)) {
				personalTabs.remove(i);
			}
		}
		releaseTab(filter);
	}
	
	
	/**
	 * Removes tab from the tabManager. Does not delete content of tab.
	 * @param filter: Filter text of tab to be removed.
	 */
	
	public void releaseTab(String filter) {
		int temp;
		String tabName = "Private " + filter.substring( 0, filter.indexOf("!") );
        
		temp = tabbedPane.indexOfTab(tabName);
		if (temp != -1) {
			tabbedPane.remove( temp );
		}
	}
	
	/**
	 * Method attaches a tab to the tabManager
	 * @param prefix Filter text for the new tab
	 * @param newPersonalTab The tab to be attached to the tabManager
	 */
	public void attachTab(String prefix, PersonalTab newPersonalTab) {
		String tabName = "Private " + prefix.substring( 0, prefix.indexOf("!") );
		// update function adding stuff to the tabbedpane?
		tabbedPane.addTab(tabName, null, newPersonalTab);
	}
    
    public String getNick() {
        return nick;
    }
}
