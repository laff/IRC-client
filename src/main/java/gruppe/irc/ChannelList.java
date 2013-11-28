package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class is for the listing of  channels.
 * It recieves channels from /list and shows them based on
 * what you put in the searchField.
 * 
 * @author Anders, Christian and Olaf.
 */
public class ChannelList extends JFrame implements DocumentListener {
	
    private static final Logger logging = Logger.getLogger (ChannelList.class.getName());
	private TabManager manager;
	
	private DefaultListModel channelListModel;
	private JList channelList;
	private JScrollPane listPane;
	private JTextField searchField;
	
	private List channels;
	
	private Integer widthish = 300;
	private Integer heightish = 400;
	
	private String titleName;

	/**
	 * Constructor.
	 * @param server : this it the title name shown in the frame.
	 * @param mng  : the tab manager in charge of this listing.
	 */
	public ChannelList(String server, TabManager mng) {
			
		titleName = server;
		manager = mng;
	}
	
	/**
	 * Function that ensures that the GUI is created,
	 * and list is safely displayed.
	 */
	public void updateList() {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				createUI();
				assembleList("");
			}
		});
	}
	
	/**
	 * Function that creates the gui.
	 */
	public void createUI() {
		setTitle(titleName);
		setLayout(new BorderLayout());
		setSize(widthish, heightish);
		setVisible(true);
		
		
		channelListModel = new DefaultListModel();
		channelList = new JList(channelListModel);
		channelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		channelList.setFixedCellWidth(50);
        channelList.setBackground(Color.GRAY);
		channelList.addMouseListener(new MouseClick());
		
		listPane = new JScrollPane(channelList);
		
		searchField = new JTextField();
		searchField.getDocument().addDocumentListener(this);
		
		add(searchField, BorderLayout.NORTH);
		add(listPane, BorderLayout.CENTER);	
	}
	
	/**
	 * Function that adds the vector of channels to the List Channels.
	 * @param chans : Vector of channels.
	 */
	public void addChannels(Vector<String> chans) {
		channels = chans;
	}

	/**
	 * action for when entering a new character in searchField.
	 * @param ev 
	 */
    public void insertUpdate(DocumentEvent ev) {
        search();
    }
    
	/**
	 * Action for when removing a character in searchField.
	 * @param ev 
	 */
    public void removeUpdate(DocumentEvent ev) {
        search();
    }

	/**
	 * Functionality required as default by DocumentListener.
	 * Not implemented.
	 * @param de 
	 */
	public void changedUpdate(DocumentEvent de) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * Function that gets the current string, with our without content,
	 * and sends it with # as prefix.
	 */
    public void search() {
        String s = searchField.getText();

		assembleList(s);
    }
	
	/**
	 * Function that creates a list to be shown based on the parameter given.
	 * @param s : the text from the searchField.
	 */
	private void assembleList(String s) {
		
		String searchString = "#"+s;
		
		channelListModel.removeAllElements();
		
		for (int i = 0; i < channels.size(); i++) {
			
			String found = null;
			String shortFound = null;

			found = channels.get(i).toString();
			
			if (found.length() >= searchString.length()) {
				shortFound = found.substring(0, searchString.length());
			}
			
			if (shortFound != null) {
				if (shortFound.equals(searchString)) {
					channelListModel.addElement(found);

				}
			}	
		}
	}
	
	/**
	 * Mouseaction for the channellist. Doubleclick on a channel will join this
     * chan.
	 */
   private class MouseClick extends MouseAdapter {
        
       @Override
       public void mouseClicked(MouseEvent e) {
           try {
                if (e.getClickCount() == 2) {
                    String selected = channelList.getSelectedValue().toString();
             
					manager.writeToLn("JOIN "+selected);
                    channelList.clearSelection();
                 }
     
           } catch (NullPointerException npe) {
                logging.log(Level.SEVERE, IRCClient.messages.getString("nullPointer"+": "+npe.getMessage()));
           }
        }
   };
}
