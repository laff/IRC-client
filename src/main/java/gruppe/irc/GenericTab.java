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
		

	public GenericTab (String tabFilter, TabManager mng) {
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
        String test = msg;
		String sender;
        String filtered;
        
        filtered = this.filter;
        
        //msg som kommer inn her, har nick!~asdasda.... PRIVMSG mottakernick :melding
        //filter stopper før PRIVMSG.
        //Prioriter å få det riktig med privatemessage først.
        
        //For en melding til kanal så er filter kanalnavnet, og meldingen er KUN det etter:
        //Vi må altså ikke "kaste" avsendernicket FØR vi kommer inn hit.
        
        
        System.out.println("Filtered: "+filtered);
        //sender = filtered.substring(filtered.indexOf("!"));
        
        //System.out.println("Fant jeg avsender her?: "+sender);
        System.out.println("melding: "+filter+":<-Here stops the filter "+msg);
		try {	
			text.getStyledDocument().insertString(pos, filter+": "+test, null);
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
        
        // If some text is added in the text-field, and the user
        // push 'Enter'.
		if (e.getSource() == write) {
            //We fetch text from the field, and then add it to the textArea.
            fromText = write.getText();
            addText(manager.getNick()+": "+fromText+"\n");
           // addText(fromText+"\n");
            //Then we send the message to the server aswell.
            writeToLn("PRIVMSG "+filter+" :"+fromText);
            write.setText("");
		}
	}
}
