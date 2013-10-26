/**
 * 
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Anders
 *
 */
public class ChatWindow extends JFrame implements ActionListener {
	String name;
	JTextField write;
	JButton quit;
	JTextArea text;
	JScrollPane scrollPane;
	BorderLayout layout;
	IRCConnection thisConnection;
	
	/**
	 * Overloaded constructor which creates a new chat window for IRC client
	 * @param channel : Name of chat channel, displayed in top pane of window.
	 * @param location : The point of a window, now passed as the location of the last.
	 * @param connection : This is the connection ment for this window. Not sure what to do when each window gets a channel...
	 */
	//TODO: Connection to channel should be moved to this class
	ChatWindow (String channel, Object location, IRCConnection connection) {
		
		write = new JTextField();
		write.addActionListener(this);
		text = new JTextArea();
		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		quit = new JButton("Close connection");
		quit.addActionListener(this);
		
		// Sets the connection relevant for this window.
		thisConnection = connection;
		
		
		/*
		//Frame for the text input and output area
		JPanel frame = new JPanel();
		JLabel txtPanel = new JLabel();
		txtPanel.add(text);
		frame.setLayout(new BorderLayout());
		frame.add(txtPanel, BorderLayout.NORTH);
		frame.add(write, BorderLayout.SOUTH);
		*/
		layout = new BorderLayout();
		setLayout(layout);
		
		setTitle(channel);
		final int xDim = 300;
		final int yDim = 400;
		setSize(xDim, yDim);
		
		// Sets location based on passed variable.
		setLocation((Point) location);
		
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		
		add(new JScrollPane(text));
		
		
		
		add(new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		add(write, BorderLayout.SOUTH);
		add(quit, BorderLayout.NORTH);
		setVisible(true);	
	}

	/**
	 * Method for adding text to the text area.
	 * This is used both by actionperformed and when adding text from messagelistener.
	 */
	public void addText(String nuText) {
		
		String temp = nuText;
		
		if (!temp.equals("")) {
			
			text.append(temp + nuText);
			
		}
	}

	/**
	 * Action listener for the write field and quit button.
	 * Input in write field is published to channel and field is cleared.
	 * Quit closes connection and shuts down thread.
	 * 
	 * At the moment closing a chat window will close the window and open the login menu.
	 * It also closes the connection. however, when there is channels in these windows, a simple /quit should be executed.
	 */
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == write) {
			
			// First our request is added to the textArea.
			addText(write.getText()+"\n");
			
			// Then the request is sent to the server. 
			// The answers are then put into the textarea by the message() function in IRCConnection.
			thisConnection.writeln(write.getText());
			
			
			write.setText("");
		}
		
		else if (e.getSource() == quit) {
			
			thisConnection.close();
			
			// opening a new login menu.
			LoginMenu loginFrame = new LoginMenu(getLocation());
			
			dispose();
		}
	}

}
