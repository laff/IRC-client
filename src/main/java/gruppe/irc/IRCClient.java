package gruppe.irc;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Olaf, Anders
 */
public class IRCClient {
	
	// Vector to store the tab managers.
	private static Vector<IRCClientFrame> ircFrames =  new Vector<IRCClientFrame>();
    public static ResourceBundle messages;
    public static Locale currentLocale;
	
	// The loginmenu!
	public static LoginMenu loginMenu;
	
	// The attribute chooser!
	public static AttributeChooser attrC;
	
	// Attributes
	public static SimpleAttributes attrs;
	

	
	public static void main(String[] args) {
        
        currentLocale = new Locale("en", "US");
        messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
		
		attrs = new SimpleAttributes();
		
		//Sets look and feel to system default
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException f) {
			//System.out.println(messages.getString("ircClient.lookAndFeelError "));
            System.out.println("Could not find system look and feel. Error: ");
			f.printStackTrace();
		} catch (InstantiationException f) {
			System.out.println("Could not use system look and feel. Error: ");
			f.printStackTrace();
		} catch (IllegalAccessException f) {
			System.out.println("Could not access system look and feel. Error: ");
			f.printStackTrace();
		} catch (UnsupportedLookAndFeelException f) {
			System.out.println("Unsupported look and feel. Error: ");
			f.printStackTrace();
		}
		
		
		// Initiate the first server.
		newServer();
		
		// Initiate the g'old loginMenu.
		loginMenu = new LoginMenu(null);
		
		// Initate the AttributeChooser.
		attrC = new AttributeChooser();
		
    }
	
	public static void newServer() {
		try {
			System.out.println("newserver called once");
			ircFrames.add(new IRCClientFrame());
		} catch (NullPointerException ohno) {
			System.out.println("newserver called once more i guess");
		}
	}
	/**
	 * Method that receives all info from the IRCConnections. 
	 * Then sends it to all the TabManagers via their frames.
	 * @param prefix
	 * @param command
	 * @param message
	 */
	public void sendInfo(String prefix, String command, String alias, String serverName, String message) {
		for (int i = 0; i < ircFrames.size(); i++) {
			((IRCClientFrame)ircFrames.elementAt (i)).thisTab.distributeMessage(prefix, command, alias, serverName, message);
		}
	}

	/**
	 * Method that receives connections from IRCConnection,
	 * and gives them to TabManagers.. ?
	 */
	public void newConnection(IRCConnection newConnection, String serverName) {
	
		try {
			// count up how many frames in the ircFrames vector.
			Integer frameCount = ircFrames.size();

			// define local variables
			IRCClientFrame tmpFrame, changeFrame = null;
			Boolean needsServerName;

			// Go through vector to find a frame with no servername (meaning no connection).
			for (int i = 0; i < frameCount; i++) {

				tmpFrame = ((IRCClientFrame)ircFrames.elementAt (i));
				needsServerName = tmpFrame.noServerName();

				// Set the "empty frame" variable with the index of the
				// ircFrame with no title.
				if (needsServerName) {
					changeFrame = ((IRCClientFrame)ircFrames.elementAt (i));
				}
			}

			// If an "empty frame" was not found, create a new frame.
			// Then set the "empty frame" index to the last one created.
			if (changeFrame == null) {
				newServer();
				changeFrame = ((IRCClientFrame)ircFrames.elementAt (frameCount));
			}

			// Give connection to the tab inside the frame
			// And give the servername to the frame.
			changeFrame.thisTab.setConnection(newConnection);
			changeFrame.updateTitle(serverName);
			System.out.println("newframes function sucess!");

		} catch (NullPointerException npe) {
			System.out.println("New frames function didnt get to run properly it seems");
		}
	}
	
	/**
	 * 
	 */
	protected void checkAborted() {
		int count = 0;
		IRCClientFrame thisFrame = null;
		try {
			count = ircFrames.size();
		} catch (NullPointerException e) {
			System.out.println("IRCClient::checkAborted:  Vector not initialized");
		}
		for (int i = 0; i < count; ++i) {
			try {
				thisFrame = ((IRCClientFrame) ircFrames.elementAt(i));
			} catch (Exception e) {
				System.out.println("IRCClient::checkAborted:  Could not retrieve vector member\n");
			}
			//If the IRCframe does exist and the connection is in aborted state -> close window
			if (thisFrame != null && thisFrame.thisTab.getConnectionState() == IRCConnection.ABORTED) {
				thisFrame.dispose();
			}
		}
	}
}
