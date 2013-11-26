package gruppe.irc;

import gruppe.irc.messageListeners.GlobalMessageListener;
import gruppe.irc.messageListeners.PingListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private static final Logger logging = Logger.getLogger (LoginMenu.class.getName());

	// The variables for logging in.
    private static String serverfilePath;
	private String serverVar, nickVar, altnickVar, usernameVar, fullnameVar;
	private Integer	portVar;
	
    private Vector<String> serverList = new Vector<String>(); 
    private Vector<String> groups = new Vector<String>();
    
    public static Vector<ServerListItem> sli = new Vector<ServerListItem>();

	private JPanel panel;
    private JLabel networkL, serverL, portL, nickL, altnickL, usernameL, 
                   fullnameL, autologL;
	private JTextField port, nick, altnick, username, fullname;
    private JComboBox server, network;
	// Buttons for login and clear all fields.
    private JButton login, clear;
	private JCheckBox autologin;

	/**
	 * Constructor for our initial login window.
	 * Currently receives an object that is either null or the point of a window.
	 * That is probably not a good idea though. Suggestions?
	 */
	LoginMenu(Object location) {
        
		super(IRCClient.messages.getString("loginM.header"));
		setSize(330,280);
		
		// Sets location based on passed variable.
		try {	
			setLocation((Point) location);
		} catch (NullPointerException npel) {
			setLocationRelativeTo(null);
		}
        
        panel = new JPanel();
        
        networkL = new JLabel(IRCClient.messages.getString("loginM.nwLabel"));
        serverL = new JLabel(IRCClient.messages.getString("loginM.srvLabel"));
        portL = new JLabel(IRCClient.messages.getString("loginM.portLabel"));
        nickL = new JLabel(IRCClient.messages.getString("loginM.nickLabel"));
        altnickL = new JLabel(IRCClient.messages.getString("loginM.altNickLabel"));
        usernameL = new JLabel(IRCClient.messages.getString("loginM.usNameLabel"));
        fullnameL = new JLabel(IRCClient.messages.getString("loginM.fullNameLabel"));
        autologL = new JLabel(IRCClient.messages.getString("loginM.autoLogLabel"));

        port = new JTextField(4);
        nick = new JTextField(32);
        altnick = new JTextField(32);
        username = new JTextField(32);
        fullname = new JTextField(32);

        login = new JButton(IRCClient.messages.getString("loginM.loginButton"));
        clear = new JButton(IRCClient.messages.getString("loginM.clearButton"));
        autologin = new JCheckBox();
        

        //Create a ComboBox, and add the server-names to it. And
        //make it possible to insert a servername not on the list.
        server = new JComboBox(serverList);
        server.setEditable(true);
        server.setMaximumRowCount(6);
        server.addItemListener(LoginMenu.this);
        network = new JComboBox(groups);
        network.addItemListener(LoginMenu.this);
        network.setMaximumRowCount(6);
		
		panel.setLayout (null); 
		
		// Position Labels
        networkL.setBounds   (40,5,70,20);
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
        panel.add(networkL);
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
		readFile();
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
                    server.addItem(sli.elementAt(i).getAddress());
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
		
		serverfilePath = pref.get("serverfilePath", null);
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
			
			pref.put("serverfilePath", serverfilePath);
			
			
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

                Thread queryThread = new Thread() {
                    @Override
                    public void run() {
                        login();
                    }
                };
                queryThread.start();

            }
        });
	
        // Action for Clear button. Add "remove last prefs" aswell?
        clear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                // Clear preferences
                putPrefs(false);

                // Clear textfields.
    //			server.setText("");
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
    
    /**
     * Method that prompts the user to select a directory where the 'servers.ini'
     * is found.
     */
    public void importServers() {
        JFileChooser chooser;
 
        chooser = new JFileChooser(new File("."));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         // The user can cancel if he wants to, no action then!
         if (chooser.showOpenDialog(LoginMenu.this) == JFileChooser.CANCEL_OPTION)
             return;

         serverfilePath = chooser.getSelectedFile().getPath();
    }
    
    /**
     * Used when the user wants to add a new server to the servers-list. Creates
     * a new window with some functionality in it.
     */
    public void addServer() {
        ServerEditorWindow servEdit = new ServerEditorWindow();
    }

    /**
     * When the user has added server(s) to the 'servers.ini'-file, the changes
     * are written to the file, so it is saved for the next startup of the
     * application.
     */
    public static void writeFile() {
        BufferedWriter bw;
        
        try {
            bw = new BufferedWriter(new FileWriter(serverfilePath, true));
            bw.write(sli.lastElement().toString());
            bw.flush();
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("ioException"+": "+e.getMessage()));
          }
    }
    
    /**
     * On first start up, or when 'Import servers' is chosen, the application 
     * will try to read servernames from a file. The handling of this functionality
     * happens here.
     */
    private void readFile() {
        String temp;
        BufferedReader bReader = null;
		FileReader f;
        
		try {
			if (serverfilePath == null) {
                importServers();
            }
            
			f  = new FileReader(serverfilePath);
			bReader = new BufferedReader(f);

		} catch (FileNotFoundException fnfe) {
			System.out.println(IRCClient.messages.getString("fileNotFound")+": "+fnfe.getMessage());
		}
		
        try { 
            while ((temp = bReader.readLine()) != null) {
                if (temp.equals("[servers]")) {
                    while((temp = bReader.readLine()) != null) {
                        int start, end;
                        String name, group, prtRange, srv;
                        //Parsing to find the strings we are looking for in the
                        // servers.ini file. Name of the server, address of the
                        // server, port-range, and group-name.
                        name = temp.substring(temp.indexOf("=")+1, temp.indexOf("SERVER"));
                        start = temp.indexOf(":")+1;
                        end = temp.indexOf(":", start);
                        srv = temp.substring(start, end);
                        //Add the servername to the serverList.
                        serverList.add(srv);
                        prtRange = temp.substring(end+1, temp.indexOf("GROUP"));
                        group = temp.substring(temp.indexOf("GROUP")+6, temp.length());
                        //Add the server to a list of server-objects.
                        sli.add(new ServerListItem(sli.size(), name, group, srv, prtRange));
                        
                        //If the group not already exists, create it!
                        if(!groups.contains(group)) {
                            groups.addElement(group);
                        }
                    }
                }
            }
            bReader.close();
            
        }  catch (IOException ioe) {
            System.err.println(IRCClient.messages.getString("ioException")+": "+ioe.getMessage());
        } catch (NullPointerException npe) {
            System.out.println(IRCClient.messages.getString("nullPointer")+": "+npe.getMessage());
        } 
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
				Thread.currentThread().sleep (1000);
				timeUsed = System.currentTimeMillis() - timeStart;

		  } catch (Exception e) { }
		}
		//In case connection failed
		if (connection.getState() != IRCConnection.CONNECTED) {
			connection.abortLogin();
		}

		//TabManager.setConnection(connection);
		
	}
}