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
		

	public GenericTab (String tabFilter, TabManager mng) {
		filter = tabFilter;
		manager = mng;
		
		layout = new BorderLayout();		
		setLayout(layout);
		
		text = new JTextPane();
		text.setEditable(false);
		text.setBackground(Color.LIGHT_GRAY);
		
		scrollPane = new JScrollPane(text);
    	scrollBar = scrollPane.getVerticalScrollBar();
		
		write = new JTextField();
		write.addActionListener(this);
		
		add(scrollPane, BorderLayout.CENTER);
		add(write, BorderLayout.SOUTH);
		setVisible(true);
		
		setPreferredSize(new Dimension(0, 420));
	}
	
	
	/**
	 * Function returns the filter text for the tab
	 * @return 
	 */
	public String getFilter () {
		return filter;
	}
	
	
	
	/**
	 * Function displays text to the text field
	 */
	public void addText(String msg) {

        int pos = text.getStyledDocument().getEndPosition().getOffset();
        
        String test = msg + "\n";
		
		
		// Logic that checks if the messages from IRC-client (IRCConnection) is meant for this tabmanager. 
		try {	
			text.getStyledDocument().insertString(pos, test, null);
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
	
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == write) {

		writeToLn(write.getText());
		
		write.setText("");
		}
	}
	
}
