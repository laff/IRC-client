package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * 
 * Greetings. This is the GenericTab class, and here is a list of commands:
 * http://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands
 * 
 * @author Anders, Christian and Olaf.
 *
 */
public class GenericTab extends JPanel implements ActionListener {
	
	private static final Logger genericlogging = Logger.getLogger (GenericTab.class.getName());
	
	protected TabManager manager;
	protected String filter;
	protected BorderLayout layout;
	protected JScrollPane scrollPane;
	protected JScrollBar scrollBar;
	protected JTextPane text;
	protected JTextField write;
	protected boolean isAttached;
    protected AbstractDocument doc;
    protected StyledDocument styledDoc;
    
    /**
	 * Constructor that contains functionality and variables that the personal, channel and server tabs use.
	 * @param tabFilter : A string that decides if mesages are to be shown here.
	 * @param mng : The tabmanager that contains the tab.
	 * @param dim : D
	 */
	public GenericTab (String tabFilter, TabManager mng, Dimension dim) {
		filter = tabFilter;
		manager = mng;
		
		isAttached = true;
		
		layout = new BorderLayout();		
		setLayout(layout);
		
		text = new JTextPane();
		text.setEditable(false);
		text.setBackground(Color.LIGHT_GRAY);
        
        
        styledDoc = text.getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            doc = (AbstractDocument)styledDoc;
        } else {
        	genericlogging.log(Level.FINE, IRCClient.messages.getString("gen.noStyle"));
        }
		
		scrollPane = new JScrollPane(text);
    	scrollBar = scrollPane.getVerticalScrollBar();
		
		write = new JTextField();
		write.addActionListener(GenericTab.this);
		
		add(scrollPane, BorderLayout.CENTER);
		add(write, BorderLayout.SOUTH);
		setVisible(true);
		
		setPreferredSize(dim);
	}
	
	
	/**
	 * Function returns the filter text for the tab
	 * @return Name of the tab
	 */
	public String getFilter () {
		return filter;
	}
    
	/**
	 * Function displays text to the text field.
	 */
	public void addText(String prefix, String msg, Boolean incoming, Integer style) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();
		String sender, message;
        
        // If it`s an incoming message.
        if (incoming) {
        // We must find out who the sender of the message is, and then clean the 
        // rest of the message, so only the actual text is left.
            sender = prefix.substring(0, prefix.indexOf("!"));
            message = msg.substring(msg.indexOf(":")+1);
        }
        // If the message is outgoing, we set the sender as the nick who comes
        // in as 'prefix', and the message is the plain text from the textfield.
        else {
            sender = prefix;
            message = msg;
        }

		try {	
            doc.insertString(pos, sender+": "+message, IRCClient.attrs.returnAttribute(style));
		} catch (BadLocationException ble) {
			genericlogging.log(Level.SEVERE, IRCClient.messages.getString("BadLocation")+": "+ble.getMessage());
		}			
		
        //When new messages appears in the window, it scrolls down automagically.
        //Borrowed from Oyvind`s example.
        SwingUtilities.invokeLater(new Thread() {
            @Override
	        public void run() {
	        	// Set the scrollbar to its maximum value
	        	scrollBar.setValue(scrollBar.getMaximum());
	        }
	    });
	}
	
	/**
	 * Static function that takes the string parameter and sends to connection and its writeln function.
	 * @param msg String sent to communicate with the server.
	 */
	public void writeToLn(String msg) {
		manager.getConnection().writeln(msg);
	}
    
    /**
     * Getting the text from the textfield when the user push 'Enter'.
     * If the text entered starts with an '/', it indicates that this is a 
     * command to the server. Then we parse out the actual to command to send
     * to the server, and finally we find the message that follows the command.
     * If it is not a command, the nick of the sender is displayed in the textarea
     * together with the written message, and the message is also sent to the server.
     * @param e The actual event.
     */
	public void actionPerformed(ActionEvent e) {
		String fromText, command ="", message="", receiver="", temp="";
    
        if (e.getSource() == write) {
			fromText = write.getText();
            
            if (fromText.startsWith("/privmsg ") || fromText.startsWith("/PRIVMSG")) {
                
                try {
                    temp = fromText.substring(fromText.indexOf(" ")+1, fromText.length());
                    command = fromText.substring(fromText.indexOf("/")+1, fromText.indexOf(" "));
                    receiver = temp.substring(0, temp.indexOf(" "));
                    message = temp.substring(temp.indexOf(" ")+1, temp.length());
                
                
					if (receiver.startsWith("#")) {
						manager.distributeChannel(manager.getNick(), receiver, message+"\n", false);
					} else {
						manager.checkPersonalTabs(receiver, message+"\n", false);
					}

					writeToLn(command.toUpperCase()+" "+receiver+" :"+message);
					
				} catch (StringIndexOutOfBoundsException sioobe) {
					
					addText(manager.getNick(), IRCClient.messages.getString("gen.noText")+"\n", false, 3);
				} 
				
			} else if (fromText.startsWith("/")) {
                
                try {
                    command = fromText.substring(fromText.indexOf("/")+1, fromText.indexOf(" "));
                    message = fromText.substring(fromText.indexOf(" ")+1, fromText.length());
                } catch (StringIndexOutOfBoundsException sioobe) {
                	genericlogging.log(Level.SEVERE, IRCClient.messages.getString("exception")+": "+sioobe.getMessage());
                }
                
                // A special case if the user types only /part into a channel-textfield.
                if (command.equals("")) {
                    try {
                        command = fromText.substring(fromText.indexOf("/")+1, fromText.length());
                        message = this.getFilter();
                    } catch (StringIndexOutOfBoundsException sioobe) {
                    	genericlogging.log(Level.SEVERE, IRCClient.messages.getString("exception")+": "+sioobe.getMessage());
                    }
                }
                writeToLn(command.toUpperCase()+" "+message);
            
            // This is an outgoing message(from our user), therefore we send 'false'.
            } else {
                addText(manager.getNick(), fromText+"\n", false, 3);
                writeToLn("PRIVMSG "+filter+" :"+fromText);
            }
            write.setText("");    
		}
	}
    
	/**
	 * Function that changes the foreground of the tab (not the frame).
	 * @param c
	 * @param co 
	 */
    public void changeForeground(Component c, Color co) {
        c.setForeground(co);
    }
}
