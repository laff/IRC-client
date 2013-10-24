/**
 * 
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
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
	 * @param channel : Name of chat channel, displayed in top pane of window 
	 */
	ChatWindow (String channel) {
		
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
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		
		add(new JScrollPane(text));
		
		
		add(text, BorderLayout.CENTER);
		add(write, BorderLayout.SOUTH);
		add(quit, BorderLayout.NORTH);
		setVisible(true);	
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == write) {
			String temp = write.getText();
			if (!temp.equals("")) {
				text.append(temp + "\n");
				text.setVisible(true);
				System.out.println(temp);
				System.out.print(text.getText());
				
				write.setText("");
			}
		}
		else if (e.getSource() == quit) {
			dispose();
		}
	}

}
