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
import java.util.Vector;
import javax.swing.*;

/**
 *
 * @author HS Vikar
 * 
 * TODO: When we click a tab, the focus should be set to the InternalFrame that 
 * tab belongs to.
 */
public class TabManager extends JPanel implements ActionListener {
	
	private static ServerTab serverTab;
	private static Vector<ChannelTab> channelTabs = new Vector<ChannelTab>();
	private static Vector<PersonalTab> personalTabs = new Vector<PersonalTab>();
	
	private static IRCConnection connection;
	
	public static JTabbedPane tabbedPane;
	public static LoginMenu loginMenu;
	public JButton testButton;
    
    JDesktopPane desktop;
	
	public TabManager () {
	
        setLayout(new BorderLayout());
		setVisible(true);	
        desktop = new JDesktopPane();
        //This one should always be made.
        serverTab = new ServerTab();
        
        //TEMP:
        channelTabs.add(new ChannelTab());
        
        desktop.add(serverTab);
        
        //TEMP: Creating the tabs in the vector, for now, just for easy testing:
        for (int i = 0; i < channelTabs.size(); i++) {
            desktop.add(channelTabs.elementAt(i));
        }
        
        
        
        add(desktop);
        
        // TODO: Hardcoded adding of the tabs, this must be automated!
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ServerTab", null, serverTab.getPanel(), "no action");
        tabbedPane.addTab("ChannelTab", null, channelTabs.elementAt(0).getPanel(), "no action either");
        add(tabbedPane, BorderLayout.NORTH);
		
        
		
    
        
        //The following line enables to use scrolling tabs.

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		// Creates loginmenu, which defaults to not visible.
		//loginMenu = new LoginMenu(null);
		
		// Initial logincheck
		loginCheck();
	}
	
	public static void setConnection (IRCConnection ourConnection) {
		connection = ourConnection;
	}
    
    public static IRCConnection getConnection () {
        return connection;
    }
	
    
    //Not in use, just for testing:
	private void addPanel (Integer type) {
		
		switch (type) {
			
			case 1 : 
				// The inital server dialogue tab.

				tabbedPane.addTab("Server tab", null, serverTab.getPanel(),
                "Does nothing");
				
				break;
				
			case 2 : 
				
				channelTabs.add(new ChannelTab());
				
				tabbedPane.addTab("channel tab", null, ((ChannelTab)channelTabs.elementAt (channelTabs.size()-1)).getPanel(), "channel tab this");
				
				break;
				
			case 3 : 
				// The inital server dialogue tab.
				//PersonalTab personalTab = new PersonalTab();
				
				personalTabs.add(new PersonalTab());
				tabbedPane.addTab("channel tab", null, ((PersonalTab)personalTabs.elementAt (personalTabs.size()-1)).getPanel(), "personal tab this");
				break;
			
		}

		//tabbedPane.add(freshTab);
	}
	
	
	/**
	 * Function that sends messages to all tabs.
	 * @param : message containing a message.
	 */
	public static void sendMessage (String message) {
		
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
		serverTab.addText(message);
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
		if (e.getSource() == testButton) {
			connection.close();
		}
	}
	/**
	 * Static function that takes the string parameter and sends to connection and its writeln function.
	 * @param msg 
	 */
	public static void writeToLn(String msg) {
		connection.writeln(msg);
	}
	
	public static void loginCheck() {
		if (connection != null) {
			
			if (connection.getState() == IRCConnection.DISCONNECTED) {
				loginMenu.setVisible(true);
			} else if (connection.getState() == IRCConnection.CONNECTED) {
				loginMenu.setVisible(false);
			}
			
		} else {
			loginMenu = new LoginMenu(null);
		}
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
}
