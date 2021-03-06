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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Anders, Christian and Olaf.
 */

public class TabManager extends JPanel {
    
    private static final Logger logging = Logger.getLogger (TabManager.class.getName());
	
	private Vector<GenericTab> channelTabs = new Vector<GenericTab>();
	private Vector<GenericTab> personalTabs = new Vector<GenericTab>();
    private ServerTab serverTab;
	
	// vector containing channelames
	private Vector<String> channelVector = new Vector<String>();
	
    private MessageHandler mh;
    
	// This TabManager's IRCConnection
	private IRCConnection connection;
	// This TabManagers IRCClientFrame parent
	private IRCClientFrame parent;
    
	//The height offset between the IRCClientFrame and the tabs
	private static int heightOffset = 80;
	private Dimension tabDimension;
    
	// The server name this TabManager is connected to.
	private String serverName, nick, channelNames = "";
	
    // Tabbedpane for organizing our tabs.
	private JTabbedPane tabbedPane;
	
	/**
	 * Constructor for the Tabmanager.
	 * creates GUI and adds the initial tab for server communication.
	 * @param prnt 
	 */
	public TabManager (IRCClientFrame prnt) {
	
        setLayout(new BorderLayout());
		setVisible(true);
		
		parent = prnt;
		tabDimension = new Dimension(parent.getWidth(), parent.getHeight() - heightOffset);
		
        serverTab = new ServerTab(TabManager.this, tabDimension);

        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener( new TabChangeListener() );
        tabbedPane.addTab(IRCClient.messages.getString("tabM.server"), serverTab);
        add(tabbedPane, BorderLayout.NORTH);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        mh = new MessageHandler(TabManager.this);
	}
	
	/**
	 * Sets connection.
	 * Uses the connection to set name of server and nick.
	 * @param ourConnection 
	 */
	public void setConnection (IRCConnection ourConnection) {
		connection = ourConnection;
		serverName = connection.getServerName();
		nick = connection.getNick();
	}
    
	/**
	 * @return connection associated with this tab manager. 
	 */
    public IRCConnection getConnection () {
        return connection;
    }
		
	/**
	 * Function that distributes messages to appropriate tabs.
	 * @param  message containing a message.
	 * @param  command containing a code.
     * @param  alias 
	 * @param  prefix containing the servername 
	 * @param  server The servername as received by the message
	 */
	public void distributeMessage (String prefix, String command, String alias, String server, String message) {
        String msg="", chanName;
		if (server.equals(serverName) && alias.equals(nick)) {

			if (command.equals("PRIVMSG") && !message.startsWith("#")) {
                mh.handlePriv(prefix, message);
                
            } else if (command.equals("PRIVMSG") && message.startsWith("#")) {
                mh.handlePrivForChan(prefix, message);
			
            } else if (command.equals("JOIN") || command.equals("PART")) {
                mh.handleJoinAndPart(prefix, message, this.nick, command);
            
            } else if (command.equals("QUIT")) {
                mh.handleQuit(prefix, message, this.nick);
            
            } else if (command.equals("MODE")) {
                mh.handleMode(prefix, message);
            
            // 353 means that the output of the NAMES-command comes now.
            } else if (command.equals("353")) {       
				mh.handleNames(message, false);
            
			// Handles the end of /names list
			} else if (command.equals("366")) {
				mh.handleNames(message, true);
            // 482 is the command received when missing permission to do an action.
            } else if (command.equals("482")) {
                mh.handleNotOp(message);
                
            } else if (command.equals("KICK")) {
                mh.handleKick(prefix, message);
			
			// This action is for the /list command.
			// It required some more logic than the others.
			// Might be able to make this prettier.
			} else if (command.equals("322")) {
				try  {
					msg = message.substring(message.indexOf("#"), message.length());
					chanName = msg.substring(0, msg.indexOf(" "));
					channelVector.addElement(chanName);
				} catch (StringIndexOutOfBoundsException sioobe) {
					logging.log(Level.SEVERE, IRCClient.messages.getString("exception")+": "+sioobe.getMessage());
				}
			
			// This commands indicates the end of /list.
			} else if (command.equals("323")) {
				mh.handleList(prefix, channelVector);

                // Else add the rest to the local servertab.
            } else {
				serverTab.addText(message);
			}
		}
        
	}
    
    /**
     * Someone other than us quit a channel we are a member of. This command
     * from the server does not tell us which channel the user did quit from, so
     * we have to find the right chan(s).
     * @param nick Nickname of the user who quit.
     * @param message This might be an empty string, timeout message, or something
     * the user wrote himself.
     */
    public void someoneQuit(String nick, String message) {
        int nrTabs = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < nrTabs; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            chanTab.quit(nick, message);
        } 
    }
    
    /**
     * Closing all tabs, called when we type QUIT. The quit-command handles
     * leaving the channels, so we just have to close the tabs here.
     */
    public void closeAllTabs() {
        int nrTabs = channelTabs.size();
        ChannelTab cTab;
        
        for (int i = 0; i < nrTabs; i++) {
            cTab = (ChannelTab)channelTabs.elementAt(i);
            releaseTab(cTab.getFilter());
        }
    }
    
    /**
     * Some updates have occurred on the channel, and these changes must be
     * shown to the user.
     * @param msg - the channel where there has been changes.
     * @param prefix - a string where the first part is the nick of a user. (Or 
     * for a MODE-event, the new mode, and the nick of the target.)
     * @param command - this will include a JOIN/PART or MODE -string.
     */
    public void updateChannel(String msg, String prefix, String command, String trgt) {
        String newUser = "";
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        try {
            newUser = prefix.substring(0, prefix.indexOf("!"));
        } catch (StringIndexOutOfBoundsException sioobe) {
        	logging.log(Level.SEVERE, IRCClient.messages.getString("exception")+": "+sioobe.getMessage());
        }
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            if (chanTab.getFilter().equals(msg)) {
                chanTab.updateNames(newUser, command);
            }
        }
    }
    
    /**
     * Does basically the same as the previous method, but some
     * more other variables and information are needed when a kick occurs.
     * So that the variable-names makes sense, we created a new method.
     * @param chan - Name of the channel the where the kick appeared.
     * @param sender - Who did kick someone.
     * @param target - Who was kicked, might have been us.
     * @param restMessage - If there was any reason for the kick.
     */
    
    public void updateOnKick(String chan, String sender, String target, String restMessage) {
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            if (chanTab.getFilter().equals(chan)) {   
                if (target.equals(this.nick)) {
                    closeTab(chan);
                } else chanTab.updateKick(sender, target);
            }   
        }
    }
    
    /**
     * Used to send the result of the NAMES-command to a specific tab.
     * @param chanName - The name of the channel this NAMES-command belongs to.
     */
    public void setChannelNames(String chanName) {
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
            // If the current tab has the corresponding channelname,
            // we can add the message to that channel.
            if (chanTab.getFilter().equals(chanName)) {
				
				chanTab.addNames(channelNames);
			}
        }
		channelNames = "";
    }
    
    /**
     * Used for putting all occasions of the /names-command for one channel together.
     * If it`s many users on the channel, the string including the names is splitted
     * in multiple messages.
     * @param names A string containing users on a channel
     */
	public void createChannelNameString(String names) {
		if (channelNames.length() > 0) {
			channelNames = channelNames + " " + names;
		} else {
			channelNames = names;
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
	public void distributeChannel(String prefix, String chanName, String message, Boolean incoming) {
        int chans = channelTabs.size();
        ChannelTab chanTab;
        
        for (int i = 0; i < chans; i++) {
            chanTab = (ChannelTab)channelTabs.elementAt(i);
			
            // If the current tab has the corresponding channelname,
            // we can add the message to that channel.
            if (chanTab.getFilter().equals(chanName)) {
                chanTab.addText(prefix, message, incoming, 1);
				
                //If message recipient is not the currently selected
                // tab, the tab name changes color to red
                int index = tabbedPane.indexOfTab(chanName);
				
                if (tabbedPane.getSelectedIndex() != index) {
                	tabbedPane.setForegroundAt(index, Color.RED);
                }
            }
        }
    }
	    
    /**
     * When a message starting with 'JOIN' is written in the textfield, 
     * we send the text to his place, to check if a tab with that channelname already
     * exists. If it does, we set this channel to focus, if it doesn`t exists, 
     * we create a tab with that name.
     * @param message Message, starting with 'JOIN #'.
     */
    public void checkForNewChannel(String message) {
        String chanName = message.substring(message.indexOf("#"), message.length()-1);
        ChannelTab cTab;
        Boolean noFoundTab = true;
        int tabs = channelTabs.size();
        
        for (int i = 0; i < tabs; i++) {
            cTab = (ChannelTab)channelTabs.elementAt(i);
            if (cTab.getFilter().equals(chanName)) {
                noFoundTab = false;
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
     * has joined this channel at all. If the channelTab is open, we close it,
     * if not, the server sends a 'no such nick' message.
     * @param outText The text the user entered in the write-field.
     */
    public void checkToLeaveChannel(String outText) {
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
    public void checkPersonalTabs(String receiver, String message, Boolean incoming) {
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
                pTab.addText(sender, message, incoming, 0);
                noFoundTab = false;
              //If message recipient is not the currently selected
                // tab, the tab name changes color to red
                int index = tabbedPane.indexOfTab(tabName);
                if (tabbedPane.getSelectedIndex() != index ) {
                	tabbedPane.setForegroundAt(index, Color.RED);
                }
            }
        }
        // Now, if there is not found a personal tab matching our description, a
        // new one must be made. This is the case either if it`s a incoming or
        // outgoing private-message.
        if (noFoundTab) {
            PersonalTab newPersonalTab = new PersonalTab(tabName, this, tabDimension);
            personalTabs.add(newPersonalTab);
            attachTab(tabName, newPersonalTab);
            newPersonalTab.addText(sender, message, incoming, 0);
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
        
        chanTab = new ChannelTab(chanName, this, tabDimension);
        channelTabs.addElement(chanTab);
        tabbedPane.addTab(chanName, null, chanTab);
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(chanName));
        chanTab.addText(chanName, IRCClient.messages.getString("tabM.nowTalk")+" "+chanName+"\n", false, 2);
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
                tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(tabName));
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
	 * @param msg message to be sent to the server.
	 */
	public void writeToLn(String msg) {
		connection.writeln(msg);
	}
    
	/**
	 * Function that checks if this is a tabManager that has not been initiated.
	 */
	public Boolean isInit() {
		return !parent.noServerName();
	}
	
	/**
	 * Method closes connection and the parent window.
	 * only allows closing of the window if it has been initated.
	 */
	public void closeConnection() {
		
		if (isInit()) {
			try {
				connection.close();
			} catch (NullPointerException npe) {
				logging.log(Level.SEVERE, IRCClient.messages.getString("errorClosing")+": "+npe.getMessage());
			}
			parent.setVisible(false);
			parent.dispose();
		}
	}

	/**
	 * Return state variable of IRCConnection
	 * @return connection state
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
					try {
						writeToLn("PART "+filter);
					} catch (Exception exc) {
                        logging.log(Level.SEVERE, IRCClient.messages.getString("tabM.closeChan")+": "+exc.getMessage());
					}
                    channelTabs.remove(i);
                    break;
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
                    break;
				}
			}
		}
		releaseTab(filter);
	}
	
	
	/**
	 * Removes tab from the tabManager. Does not delete content of tab.
	 * @param tabName Filter text of tab to be removed.
	 */
	public void releaseTab(String tabName) {
		//This function might be called from different places, so we must
		// check that the tab is actually in the tabbedPane
		int tabIndex = tabbedPane.indexOfTab(tabName);
		if (tabIndex != -1) {
			tabbedPane.remove(tabIndex);
		}
	}
	
	/**
	 * Method attaches a tab to the tabManager and sets focus on 
	 *  the added tab
	 * @param tabName Filter text for the new tab
	 * @param newTab The tab to be attached to the tabManager
	 */
	public void attachTab(String tabName, GenericTab newTab) {
		tabbedPane.addTab(tabName, null, newTab);
		tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(tabName));
	}
    
    public String getNick() {
        return nick;
    }
    
    public String getServer() {
        return serverName;
    }
    
    /**
     * Method changes dimension for all tabs
     * @param newHeight The new height of the IRCClientFrame
     */
    public void resizeTabs(int newHeight) {
    	tabDimension.setSize(tabDimension.getHeight(), newHeight - heightOffset);
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
    
    /**
     * ChangeListener for the tabbed pane, used to sense when
     * a tab change occurs and set the color of that tab to default.
     */
    class TabChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent arg0) {
			int i = tabbedPane.getSelectedIndex();
			tabbedPane.setForegroundAt(i, Color.BLACK);
		}
    }
}
