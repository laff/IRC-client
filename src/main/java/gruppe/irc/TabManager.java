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
	// This TabManagers IRCClientFrame parent
	private IRCClientFrame parent;
	
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
	//The height offset between the IRCClientFrame and the tabs
	private final int heightOffset = 80;
	private Dimension tabDimension;
	
	
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
	
	public TabManager (IRCClientFrame prnt) {
	
        setLayout(new BorderLayout());
		setVisible(true);
		
		parent = prnt;
		tabDimension = new Dimension(0, parent.getHeight() - heightOffset);
		
        desktop = new JDesktopPane();
        serverTab = createServerTab();
        desktop.add(serverTab);
    
        add(desktop);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Server", serverTab);
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
        intFrame.setPreferredSize(tabDimension);
		
		return intFrame;
	}
	
		
	/**
	 * Function that distributes messages to appropriate tabs.
	 * @param : message containing a message.
	 * @param : command conatining a code.
     * @param : alias 
	 * @param : prefix containing the servername 
	 * @param : server The servername as received by the message
	 */
	public void distributeMessage (String prefix, String command, String alias, String server, String message) {
		String chanName, restMessage, pref;
        
		if (server.equals(serverName) && alias.equals(nick)) {
                     System.out.println("I distributeMessage er message lik: "+message);
                     System.out.println("I distributeMessage er command lik: "+command);
                     System.out.println("I distributeMessage er prefix lik: "+prefix);
			// If the command matches "PRIVMSG" we have a personal message incoming,
            // unless the message starts with a '#', in which case it is a channel-message.
			if (command.equals("PRIVMSG") && !message.startsWith("#")) {
				distributePrivate(prefix, message);
			
            // If we get a PRIVMSG command, and a channelname specified with a '#', then
            // this message is meant for a specific tab that already exists, because
            // a user cannot receive messages from a channel he has not joined.
			} else if (command.equals("PRIVMSG") && message.startsWith("#")) {
				chanName = message.substring(message.indexOf("#"), message.indexOf(" "));
                restMessage = message.substring(message.indexOf(":")+1, message.length());
				distributeChannel(prefix, chanName, restMessage, true);
			
            // A JOIN command appears when we join a new channel, or when a new
            // user joins a channel we are a member of.
			} else if (command.equals("JOIN") || command.equals("PART")) {
                pref = prefix.substring(0, prefix.indexOf("!"));
                chanName = message.substring(message.indexOf(":")+1, message.length()-1);
                // If it is us that joins or leaves a channel
                if (pref.equals(nick)){
                    if (command.equals("JOIN")) {
                        checkForNewChannel(message);
                    } else checkToLeaveChannel(message);
                  
                // But it also appears when a new user joins a channel that we already
                // are a member of, but then the prefix doesn`t include our nick.
                } else {
                    updateChannel(chanName, prefix, command);
                }

                // TODO:
                // The users with OP shall be listed first(says the project-text), 
                // so we have to sort them (using the sort-it-out-algorithm)
               
            // Command: 353 means that the output of the NAMES-command comes now.
            } else if (command.equals("353")) {
                //Variables used to split the message.
                String temp, names;
                
                temp = message.substring(message.indexOf("#"));
                chanName = temp.substring(0, temp.indexOf(" "));
                names = message.substring(message.indexOf(":")+1, message.length()-1);
                // Sends the channelname, and the result of the NAMES-command
                // (as a string) to the setChannelNames-method.
                setChannelNames(chanName, names);
            } 
            // Else add the rest to the local servertab.
             else {
				addText(message);
			}
		}
	}
    
    /**
     * Some updates has occured on the channel, and these changes must be
     * shown to the user.
     * @param msg - the channel where there has been changes.
     * @param prefix - a string where the first part is the nick of a user.
     * @param command - this will include a JOIN or PART -string.
     */
    
    private void updateChannel(String msg, String prefix, String command) {
        String newUser = prefix.substring(0, prefix.indexOf("!"));
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            if (chanTab.getFilter().equals(msg)) {
                chanTab.updateNames(newUser, command);
            }
        }
    }
    
    /**
     * Used to send the result of the NAMES-command to a specific tab.
     * @param chanName - The name of the channel this NAMES-command belongs to.
     * @param names - String including all users on the channel.
     */
    
    private void setChannelNames(String chanName, String names) {
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            // If the current tab has the corresponding channelname,
            // we can add the message to that channel.
            if (chanTab.getFilter().equals(chanName)) {
                chanTab.addNames(names);
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
     * @param prefix 
     */
    
	private void distributeChannel(String prefix, String chanName, String message, Boolean incoming) {
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            // If the current tab has the corresponding channelname,
            // we can add the message to that channel.
            if (chanTab.getFilter().equals(chanName)) {
                chanTab.addText(prefix, message, incoming);
            }
        }
	}

    
	/**
	 * Function that takes care of distributing messages to the personaltabs.
	 * @param prefix 
	 * @param message 
	 */
	private void distributePrivate(String prefix, String message) {
		//int personalCount = personalTabs.size();
		//boolean noFoundTab = true;
		//String tabName = prefix.substring(0, prefix.indexOf("!"));
        
        // Includes paramter 'true', to tell that this is an incoming message.
        checkPersonalTabs(prefix, message, true);
		// Goes through the personal tabs to find one that matches our description.
		// Sets the noFoundTab variable to false if there was found a tab that match.
	/*	for (int i = 0; i < personalCount; ++i) {

			PersonalTab pTab = (PersonalTab)personalTabs.elementAt (i);
			
			if (pTab.getFilter().equals(tabName)) {
				
				pTab.addText(prefix, message, true);
				noFoundTab = false;
			}
		}

		// Now, if there is not found a personal tab matching our description, a new one must be made.
		if (noFoundTab) {
			
			PersonalTab newPersonalTab = new PersonalTab(tabName, this,tabDimension);
			personalTabs.add(newPersonalTab);

			attachTab(tabName, newPersonalTab);

			newPersonalTab.addText(prefix, message, true);
		} */
	}
	
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    
    /**
     * When a message starting with 'JOIN' is written in the textfield, 
     * we send the text to his place, to check if a tab with that channelname already
     * exists. If it does, we set this channel to focus, if it doesn`t exists, 
     * we create a tab with that name.
     * @param outText a message, starting with 'JOIN #'.
     */
    
    private void checkForNewChannel(String message) {
        String chanName = message.substring(message.indexOf("#"), message.length()-1);
        ChannelTab cTab;
        Boolean noFoundTab = true;
        int tabs = channelTabs.size();
        
        for (int i = 0; i < tabs; i++) {
            cTab = (ChannelTab)channelTabs.elementAt(i);
            if (cTab.getFilter().equals(chanName)) {
                noFoundTab = false;
                // If the user tries to join a channel that he already has joined
                // this channel will be set as selected.
                tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(chanName));
            }   
        }
        // The channelTab is not found, let`s create it!
        if (noFoundTab) {
            createChannelTab(chanName);
        }
    }
   
    /**
     * When a 'PART #'-message is issued, we end up here to see if the user
     * has joined this channel at all. If the channelTab is open, we closes it,
     * if not, the server sends a 'no such nick' message.
     * @param outText The text the user entered in the write-field.
     */
    
    private void checkToLeaveChannel(String outText) {
        String chanName = outText.substring(outText.indexOf("#"), outText.length()-1);
        int tabs = channelTabs.size();
        ChannelTab cTab;
        
        for (int i = 0; i < tabs; i++) {
            cTab = (ChannelTab)channelTabs.elementAt(i);
            if (cTab.getFilter().equals(chanName)) {
                channelTabs.remove(i);
                releaseTab(chanName);
            }
        }
    }
    
    /**
     * When an incoming or outgoing message appears here, we want to check
     * if the personalTab already exists or not. If it doesn`t exist, we creates
     * it, and sends the message to it.
     * @param receiver This is the nick of the user communicating on the tab.
     * @param message This is the message to be shown.
     * @param incoming True if the message is incoming, false if it was sent
     * from our write-field.
     */
    
    private void checkPersonalTabs(String receiver, String message, Boolean incoming) {
        int personalCount = personalTabs.size();
        boolean noFoundTab = true;
        PersonalTab pTab;
        String tabName, sender;
        
        if (incoming) {
            tabName = receiver.substring(0, receiver.indexOf("!"));
            sender = receiver;
        } else {
            tabName = receiver;
            sender = this.nick; 
        }
        
        for (int i = 0; i < personalCount; ++i) {
            pTab = (PersonalTab)personalTabs.elementAt(i);
            if (pTab.getFilter().equals(tabName)) {
                pTab.addText(receiver, message, incoming);
                noFoundTab = false;
            }
        }
        // Now, if there is not found a personal tab matching our description, a
        // new one must be made. This is the case either if it`s a incoming or
        // outgoing private-message.
        if (noFoundTab) {
            PersonalTab newPersonalTab = new PersonalTab(tabName, this, tabDimension);
            personalTabs.add(newPersonalTab);
            attachTab(tabName, newPersonalTab);
            newPersonalTab.addText(sender, message, incoming);
        }
    }    
    
    /**
     * Takes care of sending the text the user enters to the appropriate place,
     * which is the textarea, and/or the server.
     * @param e 
     */

	public void actionPerformed(ActionEvent e) {
		String outText, temp ="", receiver="", message="";
    
        if (e.getSource() == write) {
			outText = write.getText();
            
            // Parsing out the elements from the textfield that we need for 
            // sending a PRIVMSG to either a channel or a user.
            // It might not be a PRIVMSG where these strings are used that 
            // are typed, so it might throw an exception.
            try {
                temp = outText.substring(outText.indexOf(" ")+1);
                receiver = temp.substring(0, temp.indexOf(" "));
                message = temp.substring(temp.indexOf(" ")+1, temp.length());
            } catch (StringIndexOutOfBoundsException sioobe) {};
            
            // If the text starts with 'PRIVMSG #', its destination is a channel.
            if (outText.startsWith("PRIVMSG #")) {
                // Distribute the message to the correct channel, and since this
                // is an outgoing message, we add 'false'(true = incoming).
                distributeChannel(this.nick, receiver, message, false);
            // If no '#' is detected, it is a regular personal-message.
            } else if (outText.startsWith("PRIVMSG")) {
                checkPersonalTabs(receiver, message, false);
            // If it`s just a 'regular' message, we add it to the textarea. 
            } else addText(outText+"\n");

			writeToLn(outText);
			write.setText("");
            
		} else if (e.getSource() == quit) {
			connection.close();
		}
	}
     
    /**
     * Method to create a new tab, for a channel that the user wants to join.
     * The channel is added to the Vector with all the other channeltabs, and
     * a tabbedPane is also created. At last the tab will be selected.
     * @param chanName a text that include a channel name to join.
     */
    
    private void createChannelTab(String chanName) {
        ChannelTab chanTab;
        
        channelTabs.addElement(chanTab = new ChannelTab(chanName, this, tabDimension));
        tabbedPane.addTab(chanName, null, chanTab);
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(chanName));
    }
    
    /**
     * Used when our user doubleclick a user on a channel. A personalTab with
     * this user is made, or set to focus.
     * @param tabName The username of the user we want to communicate with, and
     * also used as tabName.
     */
    
    public void createPersonalTab(String tabName) {
        int personalCount = personalTabs.size();
        PersonalTab pTab;
        Boolean noFoundTab = true;
        
        for (int i = 0; i < personalCount; ++i) {
            pTab = (PersonalTab)personalTabs.elementAt(i);
            if (pTab.getFilter().equals(tabName)) {
                
                //MISSING: Focus this tab here.
                noFoundTab = false;
            }
        }
        if (noFoundTab) {
            PersonalTab newPersonalTab = new PersonalTab(tabName, this, tabDimension);
            personalTabs.add(newPersonalTab);
            attachTab(tabName, newPersonalTab);
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
	
    public void addText (String msg) { //(String prefix, String command, String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();

      //  String test = "\nPrefix: " + prefix + "\nCommand: " + command + "\nMessage: " + msg + "\n";
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
	
	/**
	 * Method closes connection and the parent window
	 */
	public void closeConnection() {
		try {
			connection.close();
			parent.dispose();
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
	
	public void closeTab (String filter) {
		//Channel filters start with #
		if (filter.startsWith("#")) {
			int channelCount = channelTabs.size();
	        ChannelTab cTab;
	
			// Goes through the channel tabs to find one that matches our description.
			// Removes element from vector
			for (int i = 0; i < channelCount; ++i) {
	
				cTab = (ChannelTab)channelTabs.elementAt(i);
				
				if (cTab.getFilter().equals(filter)) {
                    writeToLn("PART "+filter);
                    channelTabs.remove(i);
				}
			}	
		}
		//If not channel tab it must be a personal tab
		else {	
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
		}
		releaseTab(filter);
	}
	
	
	/**
	 * Removes tab from the tabManager. Does not delete content of tab.
	 * @param filter: Filter text of tab to be removed.
	 */
	public void releaseTab(String tabName) {
		//This function might be called from different places, so we must
		// check that the tab is actually in the tabbedPane
		int tabIndex = tabbedPane.indexOfTab(tabName);
		if (tabIndex != -1) {
			tabbedPane.remove( tabIndex );
		}
	}
	
	/**
	 * Method attaches a tab to the tabManager and sets focus on 
	 *  the added tab
	 * @param prefix Filter text for the new tab
	 * @param newTab The tab to be attached to the tabManager
	 */
	public void attachTab(String tabName, GenericTab newTab) {
		tabbedPane.addTab(tabName, null, newTab);
		tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(tabName));
	}
    
    public String getNick() {
        return nick;
    }
    
    public void resizeTabs(int newHeight) {
    	tabDimension.setSize(0, newHeight - heightOffset);
    	serverTab.setSize(tabDimension);
    	int count = channelTabs.size();
    	for (int i = 0; i < count; ++i) {
    		channelTabs.get(i).setSize(tabDimension);
    	}
    	count = personalTabs.size();
    	for (int i = 0; i < count; ++i) {
    		personalTabs.get(i).setSize(tabDimension);
    	}
    }
}
