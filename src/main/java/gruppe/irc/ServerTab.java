/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 *
 * @author HS Vikar
 */
public class ServerTab extends JInternalFrame implements ActionListener {
	
	public JPanel panel;
    
    String serverName;
	JTextField write;
	JButton quit;
	JTextPane text;
	JScrollPane scrollPane;
	BorderLayout layout;
    
	
	public ServerTab () {
        //Creates the servertab, and sets it resizeable, non-closable, maximizable
        // and minimizable.
        super("Server", true, false, true, true);
        setSize(300, 300);
        
        setLayout(new BorderLayout());
        add(scrollPane = new JScrollPane(text = new JTextPane()), BorderLayout.CENTER);
        write = new JTextField();
        add(write, BorderLayout.SOUTH);
 
		write.addActionListener(this);

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
    
    public String getName() {
        return "irc.homelien.no";
    }
	
    //For text to be appended in a textPane, we need to know the position
    //of the last printed text.
	
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
	        	JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
	        	// Set the scrollbar to its maximum value
	        	scrollbar.setValue(scrollbar.getMaximum());
	        }
	    });
	}
    
    
    	    

  	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == write) {
			
			// First our request is added to the textArea.
			addText(write.getText()+"\n");
			
			// Then the request is sent to the server. 
			// The answers are then put into the textarea by the message() function in IRCConnection.
			//TabManager.getConnection().writeln(write.getText());
			
			//IRCClient.writeInfo(write.getText());
			
			write.setText("");
		}
		/*
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
