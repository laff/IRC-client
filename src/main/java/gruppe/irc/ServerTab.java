package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 * Inherits properties from GenericTab, and is the tab where the server-messages
 * are displayed.
 * @author Anders, Christian and Olaf.
 */
public class ServerTab extends GenericTab {
    private static final Logger logging = Logger.getLogger (ServerTab.class.getName());
    private JButton quit;
	/**
	 * Constructor for ServerTab
	 * @param mng Pointer to parent TabManager
	 * @param dim The dimension of the IRCClientFrame
	 */
	public ServerTab(TabManager mng, Dimension dim) {
		super(IRCClient.messages.getString("srvTab.server"), mng, dim);
        		
		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);

		quit = new JButton(IRCClient.messages.getString("srvTab.close"));
		quit.addActionListener(new QuitListener());

		add(quit, BorderLayout.NORTH);
    }
    
    /**
     * Takes care of sending the text the user enters to the appropriate place,
     * which is the textarea in the servertab.
	 * 
	 * Uses the style with index 2 aka server when applying new text elements.
     */
    public void addText (String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();

        try {	
            doc.insertString(pos, msg, IRCClient.attrs.returnAttribute(2));
        } catch (BadLocationException ble) {
            logging.log(Level.SEVERE, IRCClient.messages.getString("badLoc"+": "+ble.getMessage()));
        }				

        //When new messages appears in the window, it scrolls down automagically.
        //Borrowed from Oyvind`s example.
        SwingUtilities.invokeLater(new Thread() {
            @Override
            public void run() {
                // Get the scrollbar from the scroll pane
                JScrollBar scrollbar = scrollPane.getVerticalScrollBar();
                // Set the scrollbar to its maximum value
                scrollbar.setValue(scrollbar.getMaximum());
            }
        });
    }
	
	/**
	 * ActionListener for the quit-button
	 * Sends quit-message to server and calls closeConnection
	 * from TabManager
	 */
	class QuitListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (manager.isInit()) {
				try {
					writeToLn("QUIT");
				} catch (Exception exc) {
					logging.log(Level.SEVERE, IRCClient.messages.getString("srvTab.quitExc"+": "+exc.getMessage()));
				}
				manager.closeConnection();
			}

		}
	}
}
