package gruppe.irc;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import gruppe.irc.messageListeners.*;
import java.util.prefs.*;

/**
 * This class controlls the connection to a IRC server. 
 * Use this class to represent the connection, then add the listeners needed to receive those events you feel you should receive
 * from this connection. Use the classes writeln method to send commands back to the server. 
 */
public class IRCConnection implements Runnable {
  /**
   * Indicates that there is an active connection
   */
  public static final int CONNECTED     =  0;
  /**
   * Indicates that there is a connection being set up
   */
  public static final int CONNECTING    =  1;
  /**
   * Indicates that this connection is not connected
   */ 
  public static final int DISCONNECTED  =  2;
  /**
   * Indicates that there an existing connection is being brought down
   */ 
  public static final int DISCONNECTING =  3;

  private int       state = DISCONNECTED;
  private String    server;
  private int       port;
  private String    nick; 
  private String    altNick; 
  private String    username; 
  private String    fullname;
  private Socket    socket;                                  
  private String    localHost;
  private Thread    messageThread;
  private BufferedReader        input;
  private DataOutputStream      output;
  private java.util.Vector      listeners = new Vector ();
  private static final Logger logging = Logger.getLogger (IRCConnection.class.getName());

  /**
   * Constructor for setting up a new IRCConnection object.
   * The constructor takes all the details for the new connection but does not actually make the connection active. To 
   * activate a new IRCConnection object you need to call the connect method.
   *
   * @param server a string with the name of the server to connect to
   * @param port the port number on which the connection should be made
   * @param nick the users preferred nickname
   * @param altNick the users alternative nickname (ignored in this implementation)
   * @param username the users username
   * @param fullname the users full name
   */
  public IRCConnection(String server, int port, String nick, String altNick, String username, String fullname) {
    this.server     = server; 
    this.port       = port;    
    this.nick       = nick; 
    this.altNick    = altNick; 
    this.username   = username; 
    this.fullname   = fullname;
  }
/*
  /**
   * Overloaded Constructor that sets up a new object, by getting the preferences
   * from registry.
   */
 /* public IRCConnection() {
      getPrefs();
  } */
  
  /**
   * Method that finds preferences saved to current user of the computers profile,
   * from last time the program was used.
   * The port number is set to 6667 as default, unless a value is saved earlier.
   */

/*  private void getPrefs() {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
      
    this.server     = pref.get("server", "");
    this.port       = pref.getInt("port", 6667);
    this.nick       = pref.get("nick", "");
    this.altNick    = pref.get("altNick", ""); 
    this.username   = pref.get("username", "");
    this.fullname   = pref.get("fullname", "");
  } */
 
  /**
   * Saves all the user-preferences to registry under the CurrentUser-path.
   */
  
  /*private void putPrefs() {
    Preferences pref = Preferences.userNodeForPackage(this.getClass());
      
    pref.put("server", this.server);
    pref.putInt("port", this.port);
    pref.put("nick", this.nick);
    pref.put("altNick", this.altNick); 
    pref.put("username", this.username);
    pref.put("fullname", this.fullname);
  }*/

 
  /**
   * Method used to add a message listener.
   * The MessageListener object will be inserted into the list of listeners that will be traversed when trying to find 
   * a suitable listener to send a given message to.
   *
   * @param ml a MessageListener object to insert into the list of active listeners
   */
  public void addMessageListener (MessageListener ml) {
    logging.fine ("Added messageListener : "+ml.getClass().getName());
    listeners.add (ml);
  }

  /**
   * Method used to remove a message listener from the list of active listeners.
   *
   * @param ml a reference to the MessageListener object to be removed.
   */
  public void removeMessageListener (MessageListener ml) {
    logging.fine ("Removed messageListener : "+ml.getClass().getName());
    listeners.remove (ml);
  }

  /**
   * Method used to initiate the connection process.
   * Calling this method will start up a new thread that will handle this connection and initiate the connection process. 
   * The calling method will have to monitor the state of this object to find out if and when the connection process successfully completes.
   */
  public void connect () {
    messageThread = new Thread (this);
    messageThread.start ();
    logging.fine ("Started new IRCConnection thread, attempting connection");
  }

  /**
   * Method used to get the state of this connection.
   *
   * @returns an integer telling the state of this connection object. IRCConection.CONNECTED, IRCConnection.CONNECTING, IRCCOnnection.DISCONNECTING, IRCConnection.DISCONNECTED.
   */
  public int getState () {
    return state;
  }

  /**
   * Method used to send a message to the server.
   * All the method does is append a carriage return and newline to the given string and send that to the server.
   *
   * @param message a string to be sendt as a command to the server.
   */
  public void writeln(String message) {
    try {
      output.writeBytes(message+"\r\n");
    } catch (Exception e) { }
    logging.fine ("Sent message to server : "+message);
  }

  // This method is used internaly to parse an incoming message into its separate parts.
  private void message (String message) {
    String prefix="", command="";
    
    if (message.startsWith (":")) {
      prefix = message.substring (1, message.indexOf(" "));
      message = message.substring (message.indexOf(" ")+1);
    }
    command = message.substring (0, message.indexOf(" "));
    message = message.substring (message.indexOf(" ")+1);

    logging.finest ("New message arriver : "+command+"|"+message);

    MessageEvent me = new MessageEvent (prefix, command, message, this);
    for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
      MessageListener listener = ((MessageListener)e.nextElement());
      listener.messageReceived(me);
      if (me.isConsumed()) {
        logging.finest ("Message handled by "+listener.getClass().getName());
        break;
      }
    }
  }

  /**
   * Method not to be called directly. 
   * This method contains the code that actually connects to the server and handles the client server interaction. Contains a loop
   * that continualy listens to transmissions from the server.
   */
  public void run() {

    addMessageListener (new LoggedOnDetector ());
    state = CONNECTING;

    try {
      socket = new Socket(server,port); 
	    localHost = socket.getLocalAddress().getHostName();
    } catch (Exception e) { // UnknownHostException or IOException
      close();
      return;
    }

    // Contact with server established

    try {
      logging.fine ("Opening connection to server");

      input = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
      output = new DataOutputStream(socket.getOutputStream());

      logging.fine ("Sending nick to server");
      writeln( "NICK "+nick+"\r\n"); 

      logging.fine ("Sending username to server");
      writeln( "USER "+username+" "+localHost+" "+server+" :"+fullname+"\r\n");
    } catch (Exception e) { // IOException
      close();
      return;
    }

    try {
      String message;  
      while ((message = input.readLine()) != null) {
        message (message);
      } 
    } catch (Exception e) {
		  if (state != DISCONNECTING) {
	      e.printStackTrace();
			}
    }
    close();
  }

  // Method used internally to try to perform a clean shutdown of the client/server communication if something "bad" happens.
  private void close() {
    logging.fine ("Closing connection to server");
    if (state == CONNECTED) {
      state = DISCONNECTING;

      try { 
        output.writeBytes("QUIT"); 
			} catch (Exception e) {}

      try { 
        socket.close(); 
      } catch (Exception e) {}
      socket = null;

      try {
        messageThread.join();
      } catch (InterruptedException e) {
      }

      state = DISCONNECTED;
    }
  }

  /**
   * The main method, implemented for testing purposes.
   * A new IRCConnection object is initiated and a connection towards irc.homelien.no is attempted. A object of the GlobalMessageListener
   * class is added, this object outputs everything from the server to standard output. A ping listener is also attached to automatically 
   * reply to ping requests from the server. After a connection is established a command is sent to the server that tells the server to send
   * messages to the channel #norge to this connection.
   *
   * @params args[] an array of command line arguments.
   */
  public static void main (String args[]) {
	  
	  ChatWindow window = new ChatWindow("Foo");

    IRCConnection forbindelse = new IRCConnection (
			"irc.homelien.no",         // server
			6667,                   // port
			"ourtestnick",		// nick
			"ourtestnick",		// altnick
			"ourtest",		// username
			"ourtest nick"		// fullname
			);

 //Preferences stuff:   IRCConnection forbindelse = new IRCConnection();
    
    forbindelse.addMessageListener (new GlobalMessageListener ());
    forbindelse.connect();
    forbindelse.addMessageListener (new PingListener ());

    while (forbindelse.state != IRCConnection.CONNECTED) {
      try {
        Thread.currentThread().sleep (100);
      } catch (Exception e) { }
    }
    forbindelse.writeln ("JOIN #IRC-clientTest");
    //Preferences stuff forbindelse.putPrefs();
    forbindelse.close();
  }

  // This class is used as a listener only to detect when an actual connection is established.
  private class LoggedOnDetector implements MessageListener {
    // If a message is received with the command 375 or 001 then we assume that the connection is established and alters the state to reflect
    // that fact. When state is altered this listener is no longer needed so it is removed.
    public void messageReceived (MessageEvent me) {
      logging.fine ("Message arrived from server, we have a connection");
      if (me.getCommand().equals ("375")||me.getCommand().equals("001")) {
        state = CONNECTED;
        removeMessageListener (this);
      }
    }
  }
}

