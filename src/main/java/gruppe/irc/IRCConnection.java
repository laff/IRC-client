package gruppe.irc;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import gruppe.irc.messageListeners.*;

/**
 * This class controls the connection to a IRC server. 
 * Use this class to represent the connection, then add the listeners needed to receive those events you feel you should receive
 * from this connection. Use the classes writeln method to send commands back to the server. 
 */
public class IRCConnection extends IRCClient implements Runnable {
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
  /**
   * Indicates that connection has time out
   */
  public static final int ABORTED = 4;

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
	
	newConnection(this, server);
  }

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

  // This method is used internally to parse an incoming message into its separate parts.
  private void message (String message) {
    String prefix="", command="";
  
	
	// Sending prefix, command and message to IRCClient.java
	
	
    if (message.startsWith (":")) {
        prefix = message.substring (1, message.indexOf(" "));
        message = message.substring (message.indexOf(" ")+1);
    }
        command = message.substring (0, message.indexOf(" "));
        message = message.substring (message.indexOf(" ")+1);

    sendInfo(prefix, command, nick, server, message + "\n");
    logging.finest ("New message arriver : "+command+" | "+message);

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
   * that continually listens to transmissions from the server.
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
        message(message);
      } 
    } catch (Exception e) {
		  if (state != DISCONNECTING) {
	      e.printStackTrace();
			}
    }
    close();
  }

  // Method used internally to try to perform a clean shutdown of the client/server communication if something "bad" happens.
  //
  // Making this public so that I can play with it while developing - Olaf.
  public void close() {
	  System.out.println("close function ran once.");
    logging.fine ("Closing connection to server");
    if (state == CONNECTED) {
		System.out.println("close function ran once while connected");
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
		
		// Trying to call a logincheck each time Disconnected is set.
		// !!!
		// This function no longer works, but should be replaced in some way.
		// content of loginCheck moved to IRCClient.java.
		// !!!
		//TabManager.loginCheck();
		
    }
  }

  /**
   * Method that checks if connected.
   */
  /*
  public boolean connectionStatus () {
	  
	while (state != CONNECTED) {
	  try {
			System.out.println("Please wait...");
			Thread.currentThread().sleep (100);

	  } catch (Exception e) { }
	}
	TabManager.setConnection(this);
	return (state == CONNECTED) ? true : false;
  }
  */
  
  /**
   * Method that returns the state of the connection
   * @returns an integer telling the state of this connection object. IRCConection.CONNECTED, IRCConnection.CONNECTING, IRCCOnnection.DISCONNECTING, IRCConnection.DISCONNECTED.
   */
  public int getState() {
	  return state;
  }

	String getNick() {
		return nick;
	}

	String getAltNick() {
		return altNick;
	}

	String getServerName() {
		return server;
	}
  
  // This class is used as a listener only to detect when an actual connection is established.
  private class LoggedOnDetector implements MessageListener {
    // If a message is received with the command 375 or 001 then we assume that the connection is established and alters the state to reflect
    // that fact. When state is altered this listener is no longer needed so it is removed.
    public void messageReceived (MessageEvent me) {
      logging.fine ("Message arrived from server, we have a connection");
      if (me.getCommand().equals ("375") || me.getCommand().equals("001")) {
        state = CONNECTED;
		
		// letting the loginmenu know it should be shown.
		loginMenu.showem(false);
		
        removeMessageListener (this);
      }
    }
  }
  
  public void abortLogin() {
	  state = ABORTED;
	  checkAborted();
  }
  
}