package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Christian
 */
public class ChannelTab extends GenericTab implements ActionListener {
	
    String channelName;
	JTextField write;
	JButton quit;
	JTextPane text, users;
	JScrollPane textScrollPane, usersScrollPane;
	BorderLayout layout;
    JSplitPane splitPane;
    JPanel panel;
	
    
    //TODO: We should get the name of the channel as an parameter, so that we
    //can set the title of the tab/InternalFrame with that name.
    //MORE TODO: When we exit a channel(crossing it out), we also must leave
    //that channel (LEAVE or PART #channelname)
    //AND MORE: Maybe some minimum-values should be set for the components in the splitpane?
    
	public ChannelTab (String chanName, TabManager mng) {
		//TODO: Must receive a proper filter
		
		//COMMENT: Some of the stuff in the constructor is already done by GenericTab.
		//You already have a field called "write" in SOUTH, and you could reassign the field "text" to "splitPane". Just saying...
		super(chanName, mng);
        
		//super("Server", true, true, true, true);
        setSize(300, 300);
        
        setLayout(new BorderLayout());
        
        add(textScrollPane = new JScrollPane(text = new JTextPane()), BorderLayout.WEST);
        //TODO: We want the list of users connected to the channel into this component:
        add(usersScrollPane = new JScrollPane(users = new JTextPane()), BorderLayout.EAST);
        
        //Splits the users and text components.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    textScrollPane, usersScrollPane);
        
        //We want the textpane to be the left component, we also want the left
        //component to have the highest weighting when resizing the window.
        splitPane.setLeftComponent(textScrollPane);
        splitPane.setRightComponent(usersScrollPane);
        splitPane.setResizeWeight(0.92);
        
        write = new JTextField();
        add(write, BorderLayout.SOUTH);
        add(splitPane, BorderLayout.CENTER);
		write.addActionListener(this);
        
        //TEMP: Background color set just to show the diff
        //TODO: We should not be able to edit the list of users, but right-click must
        //work, will it work now?
        users.setBackground(Color.red);
        users.setEditable(false);
        
		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);
     
		quit = new JButton("Close connection");
		quit.addActionListener(this);
        
        add(quit, BorderLayout.NORTH);
        setVisible(true);
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
		
	public void addText(String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();
        
        try {	
            text.getStyledDocument().insertString(pos, msg, null);
        } catch (BadLocationException ble) {};
        
        //When new messages appears in the window, it scrolls down automagically.
        //Borrowed from Oyvind`s example.
        SwingUtilities.invokeLater(new Thread() {
	        public void run() {
	        	// Get the scrollbar from the scroll pane
	        	JScrollBar scrollbar = textScrollPane.getVerticalScrollBar();
	        	// Set the scrollbar to its maximum value
	        	scrollbar.setValue(scrollbar.getMaximum());
	        }
	    });
	}
    
      	public void actionPerformed(ActionEvent e) {
/*
		if (e.getSource() == write) {
			
			// First our request is added to the textArea.
			addText(write.getText()+"\n");
			
			// Then the request is sent to the server. 
			// The answers are then put into the textarea by the message() function in IRCConnection.
			TabManager.getConnection().writeln(write.getText());
			
			write.setText("");
		}
		
		else if (e.getSource() == quit) {
            //Will throw an exception if not connected to a server.
			try {
                TabManager.getConnection().close();
            } catch (NullPointerException npe) {
                
            }
			// Opening a new login menu.
			//LoginMenu loginFrame = new LoginMenu(getLocation());
			
			dispose();
		}

*/
		}
	
}