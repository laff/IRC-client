package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 *
 * @author HS Vikar
 */
public class ServerTab extends GenericTab {
	
	private JButton quit;
    
	/**
	 * Constructor for ServerTab
	 * @param mng Pointer to parent TabManager
	 * @param dim The dimension of the IRCClientFrame
	 */
	public ServerTab(TabManager mng, Dimension dim) {
		super("Server", mng, dim);
        		
		text.setBackground(Color.LIGHT_GRAY);
		text.setEditable(false);

		quit = new JButton("Close window");
		quit.addActionListener(new QuitListener());

		add(quit, BorderLayout.NORTH);
	}
    
    /**
     * Takes care of sending the text the user enters to the appropriate place,
     * which is the textarea in the servertab.
     */
    public void addText (String msg) {
        int pos = text.getStyledDocument().getEndPosition().getOffset();

        try {	
            text.getStyledDocument().insertString(pos, msg, null);
        } catch (BadLocationException ble) {};					

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
			writeToLn("QUIT");
			manager.closeConnection();
		}
		
	}

}
