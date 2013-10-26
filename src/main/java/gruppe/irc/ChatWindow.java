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
	BorderLayout layout;
	
	/**
	 * Overloaded constructor which creates a new chat window for IRC client
	 * @param channel : Name of chat channel, displayed in top pane of window.
	 * @param location : The point of a window, now passed as the location of the last.
	 */
	//TODO: Connection to channel should be moved to this class
	ChatWindow (String channel, Object location) {
		
		write = new JTextField();
		write.addActionListener(this);
		text = new JTextArea();
		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);
		quit = new JButton("Close connection");
		quit.addActionListener(this);
		
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
		
		
		add(text, BorderLayout.CENTER);
		add(write, BorderLayout.SOUTH);
		add(quit, BorderLayout.NORTH);
		setVisible(true);	
	}


	/**
	 * Action listener for the write field and quit button.
	 * Input in write field is published to channel and field is cleared.
	 * Quit closes connection and shuts down thread.
	 * 
	 * At the moment closing a chat window will close the window and open the login menu.
	 * Should either disconnect from channel or server.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == write) {
			String temp = write.getText();
			if (!temp.equals("")) {
				text.append(temp + "\n");
				write.setText("");
			}
		}
		else if (e.getSource() == quit) {
			
			// opening a new login menu.
			LoginMenu loginFrame = new LoginMenu(getLocation());
			
			dispose();
		}
	}

}
