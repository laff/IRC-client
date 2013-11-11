package gruppe.irc;

import gruppe.irc.messageListeners.GlobalMessageListener;
import gruppe.irc.messageListeners.PingListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.prefs.Preferences;

/**
 *
 * @author John
 */
public class LoginMenu extends JFrame implements ItemListener {
	
	/**
	 * Overrides the overlying default close operation to 
	 * EXIT_ON_CLOSE
	 */
	@Override
	public void setDefaultCloseOperation(int operation) {
		
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// The variables for logging in.
	private String serverVar, nickVar, altnickVar, usernameVar, fullnameVar;
	private Integer	portVar;
	/*
	// Main initiates the login menu "A menu for login".
	public static void main(String[] args) {
		LoginMenu loginFrame = new LoginMenu(null);
	}
	*/
    
    private Vector<String> networks = new Vector<String>();
    private Vector<String> serverList = new Vector<String>(); 
    private Vector<String> groups = new Vector<String>();
    
    Vector<ServerListItem> sli = new Vector<ServerListItem>();

	// The panel
	JPanel panel = new JPanel();		

	// Labels
	JLabel serverL = new JLabel("Server:");
	JLabel portL = new JLabel("Port:");
	JLabel nickL = new JLabel("Nick 1:");
	JLabel altnickL = new JLabel("Nick 2:");
	JLabel usernameL = new JLabel("Username:");
	JLabel fullnameL = new JLabel("Full Name:");
	JLabel autologL = new JLabel("Auto login:");
	
	// Input fields
	//JTextField server = new JTextField(32);
	JTextField port = new JTextField(4);
	JTextField nick = new JTextField(32);
	JTextField altnick = new JTextField(32);
	JTextField username = new JTextField(32);
	JTextField fullname = new JTextField(32);
    
    JComboBox server, network;
	
	// Buttons
	JButton login = new JButton("Login");	// The login button.
	JButton clear = new JButton("Clear");		// Button to exit program.
	
	// Check box
	JCheckBox autologin = new JCheckBox();


	/**
	 * Constructor for our initial login window.
	 * Currently receives an object that is either null or the point of a window.
	 * That is probably not a good idea though. Suggestions?
	 */
	LoginMenu(Object location) {
		super(IRCClient.messages.getString("loginMenu.header"));
		setSize(330,280);
		
		// Sets location based on passed variable.
		try {	
			setLocation((Point) location);
		} catch (NullPointerException npel) {
			setLocationRelativeTo(null);
		}
        
        //Initiate the server-list
        try {
            initiateServerlist();
        } catch (MalformedURLException mue) {};
        //Create a ComboBox, and add the server-names to it. And
        //make it possible to insert a servername not on the list.
        server = new JComboBox(serverList);
        server.setEditable(true);
        server.setMaximumRowCount(4);
        server.addItemListener(this);
        network = new JComboBox(networks);
        network.addItemListener(this);
        network.setMaximumRowCount(4);
		
		panel.setLayout (null); 
		
		// Position Labels
		serverL.setBounds	(40,30,70,20);
		portL.setBounds		(40,55,70,20);
		nickL.setBounds		(40,80,70,20);
		altnickL.setBounds	(40,105,70,20);
		usernameL.setBounds	(40,130,70,20);
		fullnameL.setBounds	(40,155,70,20);
		autologL.setBounds  (40,200,70,20);
		
		// Position input fields
        network.setBounds(110, 5, 160, 20);
        
		server.setBounds	(110,30,160,20);
		port.setBounds		(110,55,160,20);
		nick.setBounds		(110,80,160,20);
		altnick.setBounds	(110,105,160,20);
		username.setBounds	(110,130,160,20);
		fullname.setBounds	(110,155,160,20);
		
		// Position buttons
		login.setBounds(40,180,115,20);
		clear.setBounds(155,180,115,20);
		
		// Position autologin
		autologin.setBounds(110, 200, 20, 20);

		// Adding elements
		panel.add(serverL);
		panel.add(portL);
		panel.add(nickL);
		panel.add(altnickL);
		panel.add(usernameL);
		panel.add(fullnameL);
		panel.add(autologL);
        panel.add(network);                 
		panel.add(server);
		panel.add(port);
		panel.add(nick);
		panel.add(altnick);
		panel.add(username);
		panel.add(fullname);
		panel.add(login);
		panel.add(clear);
		panel.add(autologin);

		getContentPane().add(panel);
		setVisible(true);

		getPrefs();
		actionlogin();      
	}
    /**
     * Method that updates the Combobox with servers belonging to the network that
     * is chosen in the network-combobox.
     * @param e 
     */
    
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == network) {
            server.removeAllItems();
            
            for(int i = 0; i < sli.size(); i++) {
                if(sli.elementAt(i).getGroup().equals(network.getSelectedItem())) {
                    
                    //TODO: Should display servername, instead of the server-address.
                    server.addItem(sli.elementAt(i).getRelName());
                }
            }
        }
    }
    
    

	/**
	* Method that finds preferences saved to current user of the computers profile,
	* from last time the program was used.
	* The port number is set to 6667 as default, unless a value is saved earlier.
	*/

	private void getPrefs() {
		
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
      
        server.setSelectedItem(pref.get("server", ""));
		port.setText(Integer.toString(pref.getInt("port", 6667)));
		nick.setText(pref.get("nick", ""));
		altnick.setText(pref.get("altNick", ""));
		username.setText(pref.get("username", ""));
		fullname.setText(pref.get("fullname", ""));
	}
 
	/**
	 * Method that either (true) saves the current preferences, 
	 * or (false) clears the preferences.
	 */
	private void putPrefs(Boolean choice) {
		Preferences pref = Preferences.userNodeForPackage(this.getClass());

		if (choice) {
			
			pref.put("server", serverVar);
			pref.putInt("port", portVar);
			pref.put("nick", nickVar);
			pref.put("altNick", altnickVar); 
			pref.put("username", usernameVar);
			pref.put("fullname", fullnameVar);
			
		} else {
			
			pref.put("server", "");
			pref.putInt("port", 6667);
			pref.put("nick", "");
			pref.put("altNick", ""); 
			pref.put("username", "");
			pref.put("fullname", "");
			
		}
	}
	
	/**
	 * Method that sets visible to false or most definitely
	 * @param really : Boolean.
	 */
	public void showem(Boolean really) {
		setVisible(really);
	}
	
	/*
	 * Actions for our buttons.
	 */
	public void actionlogin(){
		login.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent ae) {
				
				//serverVar = server.getText();
                
                serverVar = (String)server.getSelectedItem();
				
				// Going to make a check for you little man.
				portVar = Integer.parseInt(port.getText());
				
				nickVar = nick.getText();
				altnickVar = altnick.getText();
				usernameVar = username.getText();
				fullnameVar = fullname.getText();
				
				
				//**********//
				//*CREATE*A*//
				//*FAILSAFE*//
				//**********//
				login();

			}
		});
	
		// Action for Clear button. Add "remove last prefs" aswell?
		clear.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent ae) {
				
				// Clear preferences
				putPrefs(false);
				
				// Clear textfields.
//				server.setText("");
				port.setText("");
				nick.setText("");
				altnick.setText("");
				username.setText("");
				fullname.setText("");

				// focus the first inputfield
				server.requestFocus();
			}
		});
	}
	
	/*
	 * The magnificent function that logs in.
	 * No arguments given, uses the variables set in main.
	 * 
	 * Variables that work like a charm:
			"irc.homelien.no",      // server
			6667,                   // port
			"ourtestnick",			// nick
			"ourtestnick",			// altnick
			"ourtest",				// username
			"ourtest nick"			// fullname
	 * 
	 */
	public void login () {
		putPrefs(true);
	  
		IRCConnection connection = new IRCConnection (
			  
			  serverVar,		// server
			  portVar,			// port
			  nickVar,			// nick
			  altnickVar,		// altnick
			  usernameVar,		// username
			  fullnameVar		// fullname
			  
			  );

		connection.addMessageListener (new GlobalMessageListener ());
		connection.connect();
		
		// Opens the server dialogue window, sends the location of loginmenu.
		//connection.connectedDialogue(getLocation());
		
		connection.addMessageListener (new PingListener ());
		
		long timeStart = System.currentTimeMillis();
		long timeUsed = 0;
		while (connection.getState() != IRCConnection.CONNECTED && timeUsed < 50000) {
		  try {
				System.out.println("Please wait...");
				Thread.currentThread().sleep (100);
				timeUsed = System.currentTimeMillis() - timeStart;

		  } catch (Exception e) { }
		}
		//In case connection failed
		if (connection.getState() != IRCConnection.CONNECTED) {
			connection.abortLogin();
		}

		//TabManager.setConnection(connection);
		
	}
    
    /*
     * Function that reads the IRC 'servers.ini' file from an URL.
     * 
     * OBS: The function is called from the constructor. Don`t know the 'policy' of throwing
     * exceptions and stuff in a constructor. 
     * OBS2: Maybe we want to use a local file instead of the 'online' version, but this
     * one is always up to date!
     * 
     * TODO: The networks and the servers should be added in two seperate lists/vectors, and
     * then be displayed in JComboBoxes or something. We can find the list of networks after the
     * heading [networks] in the file, the servers are located under the heading [servers] (belive it or not).
     * 
     */
    private void initiateServerlist() throws MalformedURLException {
        BufferedReader bReader;    
        String temp, network, srv;
        URL servers;
        
        
        
        
        try {
            servers = new URL("http://www.mirc.com/servers.ini");
            bReader = new BufferedReader(new InputStreamReader(servers.openStream()));
            while ((temp = bReader.readLine()) != null) {
                if(temp.equals("[networks]")) {
                    //Here we have found the [networks]-heading, then we can get the names,
                    //and add the networks to our list.
                    while(!(temp = bReader.readLine()).equals("")) {
                        network = temp.substring(temp.indexOf("=")+1, temp.length());
                        networks.add(network);  
                    }
                }
                
                if(temp.equals("[servers]")) {
                    while((temp = bReader.readLine()) != null) {
                        int start, end;
                        String name, group;
                        ServerListItem tmp;
                        
                        //Find the actual name of the server, not the address.
                        name = temp.substring(temp.indexOf("=")+1, temp.indexOf("SERVER"));
                        //Finding the first occation of colon, and save the
                        //position of the characater after it.
                        start = temp.indexOf(":")+1;
                        //Find the first occasion of colon after the first one.
                        end = temp.indexOf(":", start);
                        //Take out the string between the two colons.
                        srv = temp.substring(start, end);
                        //Add the servername to the serverList.
                        serverList.add(srv);
                        //Find which group(network) the server belongs to.
                        group = temp.substring(temp.indexOf("GROUP")+6, temp.length());
                        //Add the server to a list of server-objects.
                        sli.add(tmp = new ServerListItem(name, group, srv));
                        
                        
                        //If the group not already exists, create it!
                        //if(!groups.contains(group)) {
                        //    groups.addElement(group);
                        //}
                    }
                }
            }
            bReader.close();
        } catch (IOException ioe) {};
    }
    
    //TEMP: Just to check the elements in the networks-list
    public void displayNetworks() {
        for(int i = 0; i < networks.size(); i++) {
          //  System.out.println(networks.elementAt(i));
        }
        // for(int i = 0; i < serverList.size(); i++) {
           // System.out.println(serverList.elementAt(i));
       // }
    }
    
}