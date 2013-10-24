/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import gruppe.irc.messageListeners.GlobalMessageListener;
import gruppe.irc.messageListeners.PingListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author John
 */
public class LoginMenu extends JFrame {
	
	// The variables for logging in.
	private String serverVar, nickVar, altnickVar, usernameVar, fullnameVar;
	private Integer	portVar;
	
	// Main initiates the login menu "A menu for login".
	public static void main(String[] args) {
		LoginMenu loginFrame = new LoginMenu();
	}
	
	// The panel
	JPanel panel = new JPanel();		

	// Labels
	JLabel serverL = new JLabel("Server:");
	JLabel portL = new JLabel("Port:");
	JLabel nickL = new JLabel("Nick 1:");
	JLabel altnickL = new JLabel("Nick 2:");
	JLabel usernameL = new JLabel("Username:");
	JLabel fullnameL = new JLabel("Full Name:");
	
	
	// Input fields
	JTextField server = new JTextField(32);
	JTextField port = new JTextField(4);
	JTextField nick = new JTextField(32);
	JTextField altnick = new JTextField(32);
	JTextField username = new JTextField(32);
	JTextField fullname = new JTextField(32);
	
	// Buttons
	JButton login = new JButton("Login");	// The login button.
	JButton exit = new JButton("Exit");		// Button to exit program.


	
	LoginMenu(){
		super("A menu for login");
		setSize(330,280);
		setLocation(700,500);
		panel.setLayout (null); 
		
		// Position Labels
		serverL.setBounds	(40,30,70,20);
		portL.setBounds		(40,55,70,20);
		nickL.setBounds		(40,80,70,20);
		altnickL.setBounds	(40,105,70,20);
		usernameL.setBounds	(40,130,70,20);
		fullnameL.setBounds	(40,155,70,20);
		
		// Position input fields
		server.setBounds	(110,30,160,20);
		port.setBounds		(110,55,160,20);
		nick.setBounds		(110,80,160,20);
		altnick.setBounds	(110,105,160,20);
		username.setBounds	(110,130,160,20);
		fullname.setBounds	(110,155,160,20);
		
		// Position buttons
		login.setBounds(40,180,115,20);
		exit.setBounds(155,180,115,20);

		// Adding elements
		panel.add(serverL);
		panel.add(portL);
		panel.add(nickL);
		panel.add(altnickL);
		panel.add(usernameL);
		panel.add(fullnameL);
		panel.add(server);
		panel.add(port);
		panel.add(nick);
		panel.add(altnick);
		panel.add(username);
		panel.add(fullname);
		panel.add(login);
		panel.add(exit);

		getContentPane().add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		actionlogin();
	}

	
	/*
	 * Actions for our buttons.
	 */
	public void actionlogin(){
		login.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent ae) {
				
				serverVar = server.getText();
				portVar = Integer.parseInt(port.getText());
				nickVar = nick.getText();
				altnickVar = altnick.getText();
				usernameVar = username.getText();
				fullnameVar = fullname.getText();
				
				// Checking if variables is empty.
				// A silly way of doing it. Make it better someone!
				if (serverVar.length() != 0 && portVar != 0 && nickVar.length() != 0 && altnickVar.length() != 0 && usernameVar.length() != 0 && fullnameVar.length() != 0) {
					
					login();
				
				} else {
					
					JOptionPane.showMessageDialog(null,"You left out some information there stud.");
				}
				
				/*if() {

				// else if perhaps shit is going to hell or jesus walks.
				} else {
				
					JOptionPane.showMessageDialog(null,"Yes I got your message, what do you want?");
					
					// Clear textfields or get saved info from file?
					server.setText("");
					port.setText("");
					nick.setText("");
					altnick.setText("");
					username.setText("");
					fullname.setText("");
				
					// focus the first inputfield
					server.requestFocus();
				}
				*/
			}
		});
	
		// add actionlistener for exit button?
		/*****CODE*****/
	}
	
	/*
	 * The magnificent function that logs in.
	 * No arguments given, uses the variables set in main.
	 */
	public void login () {
	  
		IRCConnection connection = new IRCConnection (
			  
			  serverVar,		// server
			  portVar,			// port
			  nickVar,			// nick
			  altnickVar,		// altnick
			  usernameVar,		// username
			  fullnameVar		// fullname
			  
			  );
	//Preferences stuff:   IRCConnection forbindelse = new IRCConnection();

	connection.addMessageListener (new GlobalMessageListener ());
	connection.connect();
	connection.addMessageListener (new PingListener ());

	while (connection.getState() != IRCConnection.CONNECTED) {
	  try {
			System.out.println("Please wait...");
			Thread.currentThread().sleep (100);
		  
	  } catch (Exception e) { }
	}
	connection.writeln ("JOIN #IRC-clientTest");
	//Preferences stuff forbindelse.putPrefs();
	connection.close();
	
  }
}