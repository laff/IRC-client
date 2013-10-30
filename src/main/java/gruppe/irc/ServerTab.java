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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author HS Vikar
 */
public class ServerTab extends JInternalFrame implements ActionListener {
	
	public JPanel panel;
	//public JTextArea text;
    
    String name;
	JTextField write;
	JButton quit;
	JTextPane text;
	JScrollPane scrollPane;
	BorderLayout layout;
    
	
	public ServerTab () {
        
        super("Server"); //, true, true, true, true);
        setClosable(true);
        setResizable(true);
        
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
		text.getStyledDocument().insertString(pos, msg, null);;
    } catch (BadLocationException ble) {};
	}

  	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == write) {
			
			// First our request is added to the textArea.
			addText(write.getText()+"\n");
			
			// Then the request is sent to the server. 
			// The answers are then put into the textarea by the message() function in IRCConnection.
			TabManager.getConnection().writeln(write.getText());
			
			
			write.setText("");
		}
		
		else if (e.getSource() == quit) {
			
			TabManager.getConnection().close();
			
			// opening a new login menu.
			LoginMenu loginFrame = new LoginMenu(getLocation());
			
			dispose();
		}
	}
	
}
