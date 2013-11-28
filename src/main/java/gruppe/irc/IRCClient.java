package gruppe.irc;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This class is the main class of the IRC - Client.
 * It contains all the vital parts of the program and keeps track of everything.
 * 
 * @author Anders, Christian and Olaf.
 */
public class IRCClient {
    
    private static final Logger logging = Logger.getLogger (IRCClient.class.getName());
	
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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException f) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("ircClient.lookAndFeelError"+": "+f.getMessage()));
		} catch (InstantiationException f) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("ircClient.lookAndFeel2"+": "+f.getMessage()));
		} catch (IllegalAccessException f) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("ircClient.lookAndFeel3"+": "+f.getMessage()));
		} catch (UnsupportedLookAndFeelException f) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("ircClient.lookAndFeel3"+": "+f.getMessage()));
		}

		// Initiate the first server.
		newServer();
		
		// Initiate the g'old loginMenu.
		loginMenu = new LoginMenu();
		
		// Initate the AttributeChooser.
		attrC = new AttributeChooser();
    }
	
    /**
     * Creates the frame containing the first server.
     */
	public static void newServer() {
        try {
			ircFrames.add(new IRCClientFrame());
		} catch (NullPointerException npe) {
			logging.log(Level.SEVERE, IRCClient.messages.getString("nullPointer"+": "+npe.getMessage()));
		}
	}
	
	/**
	 * Function that removes the IRCCFrame when close() has been called in IRCCOnnection.
	 * This ensures that it is possible to log into a server multiple times without 
	 * being stopped by the checkExists() function.
	 */
	public static void destroyServer(String serverName, String alias) {
		Integer frameCount = ircFrames.size();
		String frameTitle = serverName + " : " + alias;
		IRCClientFrame tmpFrame;
		
		for (int i = 0; i < frameCount; i++) {
			tmpFrame = ((IRCClientFrame)ircFrames.elementAt(i));
			
			if (frameTitle.equals(tmpFrame.getFrameTitle())) {
				try {
					ircFrames.remove(i);
				} catch (NullPointerException npe) {
					logging.log(Level.SEVERE, IRCClient.messages.getString("nullPointer"+": "+npe.getMessage()));
				}
			}
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
			((IRCClientFrame)ircFrames.elementAt(i)).thisTab.distributeMessage(prefix, command, alias, serverName, message);
		}
	}
    
    /**
     * Checks if we are already connected to a server with this name, using this nick.
     * A message-dialog pops up if this is the case.
     * @param serverName - The name of the server to check.
     * @param alias - The nick to check
     * @return true if server and nick exists.
     */
    public Boolean checkIfExists(String serverName, String alias) {
        Integer frameCount = ircFrames.size();
        String frameTitle = serverName + " : " + alias;
        Boolean exists  = false;
        IRCClientFrame tmpFrame;
        
        for (int i = 0; i < frameCount; i++) {
            tmpFrame = ((IRCClientFrame)ircFrames.elementAt(i));
            exists = (frameTitle.equals(tmpFrame.getFrameTitle()));
        }

        // If there is already an ircclient frame with the same server AND nickname.
        if (exists) {
            JOptionPane.showMessageDialog(
                null, 
                messages.getString("connectionExist"), 
                messages.getString("error"), 
                JOptionPane.ERROR_MESSAGE
            );
        }
        return exists;
    }
    
	/**
	 * Method that receives connections from IRCConnection,
	 *
	 * First checking if there are any empty frames (like the ones when starting the ircclient).
	 * If there are no empty frames, a new one is created.
	 */
	public void newConnection(IRCConnection newConnection, String serverName, String alias) {
	
        String frameTitle = serverName + " : " + alias;
        
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
            changeFrame.updateTitle(frameTitle);

		} catch (NullPointerException npe) {
			logging.log(Level.SEVERE, IRCClient.messages.getString("nullPointer"+": "+npe.getMessage()));
		}
	}
	
	/**
	 * Checks if a connection is in aborted state, if it is, the window is closed
	 */
	protected void checkAborted() {
		int count = 0;
		IRCClientFrame thisFrame = null;
        
		try {
			count = ircFrames.size();
		} catch (NullPointerException e) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("ircClient.nullVector"+": "+e.getMessage()));
		}
		for (int i = 0; i < count; ++i) {
			try {
				thisFrame = ((IRCClientFrame)ircFrames.elementAt(i));
			} catch (Exception e) {
				logging.log(Level.SEVERE, IRCClient.messages.getString("ircClient.noVectorMember"+": "+e.getMessage()));
			}
			//If the IRCframe does exist and the connection is in aborted state -> close window
			if (thisFrame != null && thisFrame.thisTab.getConnectionState() == IRCConnection.ABORTED) {
				thisFrame.dispose();
			}
		}
	}
}
