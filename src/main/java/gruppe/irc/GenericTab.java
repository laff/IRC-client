/**
 * 
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 * @author Anders
 * 
 * Greetings. This is the GenericTab class, and here is a list of commands:
 * http://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands
 *
 */
public class GenericTab extends JPanel implements ActionListener {
	
	protected TabManager manager;
	protected String filter;
	protected BorderLayout layout;
	protected JScrollPane scrollPane;
	protected JScrollBar scrollBar;
	protected JTextPane text;
	protected JTextField write;
	protected boolean isAttached;

	public GenericTab (String tabFilter, TabManager mng, Dimension dim) {
		filter = tabFilter;
		manager = mng;
		
		isAttached = true;
		
		layout = new BorderLayout();		
		setLayout(layout);
		
		text = new JTextPane();
		text.setEditable(false);
		text.setBackground(Color.LIGHT_GRAY);
		
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
	 * @return 
	 */
	public String getFilter () {
		return filter;
	}
	

	/**
	 * Function displays text to the text field.
	 */
	public void addText(String prefix, String msg, Boolean incoming) {

        int pos = text.getStyledDocument().getEndPosition().getOffset();
		String sender, message;
        
        // If it`s an incoming message.
        if(incoming) {
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
			text.getStyledDocument().insertString(pos, sender+": "+message, null);
		} catch (BadLocationException ble) {};					
		
        //When new messages appears in the window, it scrolls down automagically.
        //Borrowed from Oyvind`s example.
        SwingUtilities.invokeLater(new Thread() {
	        public void run() {
	        	// Set the scrollbar to its maximum value
	        	scrollBar.setValue(scrollBar.getMaximum());
	        }
	    });
	}

	
	/**
	 * Static function that takes the string parameter and sends to connection and its writeln function.
	 * @param msg 
	 */
	public void writeToLn(String msg) {
		manager.getConnection().writeln(msg);
	}
	/**
     * Getting the text from the textfield, when the user push 'Enter'-button.
     * @param e The actual event.
     */
	
	public void actionPerformed(ActionEvent e) {
		String fromText;
        
        // If some text is added in the text-field, and the user push 'Enter'.
		if (e.getSource() == write) {
            // We fetch text from the field, and then add it to the textArea.
            fromText = write.getText();
            
            // This is a outgoing message, therefore we send 'false'.            
            addText(manager.getNick(), fromText+"\n", false);
            // Then we send the message to the server aswell.
            writeToLn("PRIVMSG "+filter+" :"+fromText);
            write.setText("");
		}
	}
}
