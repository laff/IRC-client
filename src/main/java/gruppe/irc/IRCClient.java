/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author HS Vikar
 */
public class IRCClient {
	
	// Vector to store the tab managers.
	private static Vector<IRCClientFrame> ircFrames =  new Vector<IRCClientFrame>();
	
	// The loginmenu!
	public static LoginMenu loginMenu;
	
	public static void main(String[] args) {
		
		//Sets look and feel to system default
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException f) {
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
	public void sendInfo(String prefix, String command, String message) {
		for (int i = 0; i < ircFrames.size(); i++) {
			((IRCClientFrame)ircFrames.elementAt (i)).thisTab.sendMessage(prefix, command, message);
		}
	}
	
	/**
	 * Method that sends stuff to the servers?
	 * @param prefix : NOT IMPLEMENTED.
	 * @param command : NOT IMPLEMENTED.
	 * @param message : raw message atm.
	 */
	public static void writeInfo(String message) {
		for (int i = 0; i < ircFrames.size(); i++) {
			((IRCClientFrame)ircFrames.elementAt (i)).thisTab.writeToLn(message);
		}
	}
	/**
	 * Method that recieves connections from IRCConnection,
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
	
/* THIS IS PRETTY LOGIC MADE BY OLAF. Used to work in TabManager, but has now emigrated over the seas.
	public static void loginCheck() {
		if (connection != null) {
			
			if (connection.getState() == IRCConnection.DISCONNECTED) {
				loginMenu.setVisible(true);
			} else if (connection.getState() == IRCConnection.CONNECTED) {
				loginMenu.setVisible(false);
			}
			
		} else {
			loginMenu = new LoginMenu(null);
		}
	}
*/
}
