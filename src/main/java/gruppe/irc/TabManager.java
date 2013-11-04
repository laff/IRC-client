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
	 */
	private final int maxNickLength = 9;
	// The server name this TabManager is connected to.
	private String serverName, nick, altNick;
	
	public JTabbedPane tabbedPane;
	public JButton testButton;
    
    JDesktopPane desktop;
	

	JTextField write;
	JButton quit;
	JTextPane text;
	JScrollPane scrollPane;
	BorderLayout layout;
	
	public TabManager () {
	
        setLayout(new BorderLayout());
		setVisible(true);	
        desktop = new JDesktopPane();
        //This one should always be made.
        serverTab = createServerTab();
   
        //TEMP:
        //channelTabs.add(new ChannelTab());
        
        desktop.add(serverTab);
        
        //TEMP: Creating the tabs in the vector, for now, just for easy testing:
        //for (int i = 0; i < channelTabs.size(); i++) {
        //    desktop.add(channelTabs.elementAt(i));
        //}
        
        
        
        add(desktop);
		
        // TODO: Hardcoded adding of the tabs, this must be automated!
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ServerTab", null, new JPanel(), "no action");
		
        //tabbedPane.addTab("ChannelTab", null, channelTabs.elementAt(0).getPanel(), "no action either");
        add(tabbedPane, BorderLayout.NORTH);
		
        
		
    
        
        //The following line enables to use scrolling tabs.

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		// Creates loginmenu, which defaults to not visible.
		//loginMenu = new LoginMenu(null);
		
		// Initial logincheck
		// !!!
		// This function no longer works, but should be replaced in some way.
		// content of loginCheck moved to IRCClient.java.
		// !!!
		//loginCheck();
	}
	
	
	public void setConnection (IRCConnection ourConnection) {
		connection = ourConnection;
		serverName = connection.getServerName();
		nick = connection.getNick();
		altNick = connection.getAltNick();
	}
    
    public IRCConnection getConnection () {
        return connection;
    }
	
	public JInternalFrame createServerTab() {
		JInternalFrame intFrame = new JInternalFrame("ServerTab", true, false, true, true);
		
		//Set maximum
        intFrame.setSize(300, 300);
		
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
		
		return intFrame;
	}
	
		
	/**
	 * Function that sends messages to all tabs.
	 * @param : message containing a message.
	 * @param : command conatining a code.
	 * @param : prefix containing the servername
	 */
	public void sendMessage (String prefix, String command, String alias, String message) {
		
		
		// The amount of tabs:
		int channelCount = channelTabs.size();
		int personalCount = personalTabs.size();
		
		
		
		for (int i = 0; i < channelCount; i++) {
			((ChannelTab)channelTabs.elementAt (i)).addText(message);
			tabbedPane.getComponentAt(i);
		}
		
		for (int i = 0; i < personalCount; i++) {
			((PersonalTab)personalTabs.elementAt (i)).addText(message);
			tabbedPane.getComponentAt(i);
		}
		addText(prefix, command, alias, message);
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
				else if (e.getSource() == testButton) {
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
	
		public void addText(String prefix, String command, String alias, String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();
		
		
		// Logic that checks if the messages from IRC-client (IRCConnection) is ment for this tabmanager.
		if (prefix.equals(serverName) && alias.equals(nick)) { 

			try {	
				text.getStyledDocument().insertString(pos, msg, null);
			} catch (BadLocationException ble) {};					
			
		}

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
}
