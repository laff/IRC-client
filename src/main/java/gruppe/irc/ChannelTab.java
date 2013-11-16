package gruppe.irc;

import gruppe.irc.PersonalTab.ButtonListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultListModel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


/**
 *
 * @author Christian
 */
public class ChannelTab extends GenericTab  {
	
	private JScrollPane usersScrollPane;
	private JSplitPane splitPane;
	private JButton close, attach;
	private JPanel panel;
	private ChannelTab self;
	private JFrame newFrame;
    private JList list;
    private JMenuItem item;
    private JPopupMenu popUp;
    private DefaultListModel listModel;
	

    //TODO: Maybe some minimum-values should be set for the components in the splitpane?
    
	public ChannelTab (String chanName, TabManager mng, Dimension dim) {
		//TODO: Must receive a proper filter
		super(chanName, mng, dim);
          
        // Adding some elements to the list, the hard way.
        listModel = new DefaultListModel();
   
        //add(textScrollPane = new JScrollPane(text = new JTextPane()), BorderLayout.WEST);
        add(scrollPane, BorderLayout.WEST);
        add(usersScrollPane = new JScrollPane(list = new JList(listModel)), BorderLayout.EAST);
        
        //Splits the users and text components.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    scrollPane, usersScrollPane);
        
        // Setting some values for our list.
        list.setVisible(true);
        list.setBackground(Color.LIGHT_GRAY);
        list.addMouseListener(new MouseClick());

        //We want the textpane to be the left component, we also want the left
        //component to have the highest weighting when resizing the window.
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(usersScrollPane);
        splitPane.setResizeWeight(0.92);

        add(splitPane, BorderLayout.CENTER);
        
        //TEMP: Background color set just to show the diff
        
		panel = new JPanel();
		close = new JButton("Close channel", null);
		attach = new JButton("Detach tab", null);
		
		close.addActionListener(new ButtonListener());
		attach.addActionListener(new ButtonListener());
		
		panel.add(attach);
		panel.add(close);
		add(panel, BorderLayout.NORTH);
		
		self = ChannelTab.this;
	}
    
    /**
     * Used to update the list of names on the right side of the client.
     * @param newUser Name of the user that has joined/parted the channel.
     * @param command the command the server has sent to us(JOIN or PART).
     */
    
    public void updateNames (String newUser, String command) {
        
        if (command.equals("JOIN")) {
            addText(this.filter, newUser+" has joined the channel", false);
        } else  {
            addText(this.filter, newUser+" has left the channel", false);
        }
      writeToLn("NAMES "+this.filter);      
    }
    
    /**
     * Function to split the string of all the users on this channel, and add each
     * username into an array. Then all the users are added to the listModel.
     * @param names String including the result of a NAMES-command
     */
    
    public void addNames (String names) {
        String namesSplitted[];
        namesSplitted = names.split(" ");
        listModel.removeAllElements();
        
        for(int i = 0; i < namesSplitted.length; i++) {
            listModel.addElement(namesSplitted[i]);
        }
    }
    
    /**
     * For future use we have to remove the '@' or '+' before sending
     * the username to the server.
     */
    
    private void whois(String user) {
        System.out.println("This is the selected user: "+user);
        writeToLn("WHOIS "+user);
    }
    
    /**
     * Set up the different items in the popup-menu when rightclicking.
     */
    
    private void setupItems() {    
        popUp = new JPopupMenu();
        popUp.add(item = new JMenuItem(IRCClient.messages.getString("chanTab.popUp.whois")));

        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    String temp =  list.getSelectedValue().toString();
                    whois(temp);
                    System.out.println("ActionPerformed YES SIR! "+temp);
                } catch (NullPointerException npe) {};
            }
        });
    }
    
    /**
     * MouseListener for our list of users. On doubleclick a PersonalTab with
     * that user is opened. On right-click a popupmenu shows up on the mouseovered
     * users.
     * TOFIX: If the user we doubleclick is OP or Voiced,
     * then the tab we creates will contain '@' or '+' before the nick.
     */
      
   private class MouseClick extends MouseAdapter {
        
       @Override
       public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                //We have a bug here, 
                manager.createPersonalTab(list.getSelectedValue().toString());
             }
            else if (e.isMetaDown()) {
                //And the same bug will happen here.
                list.setSelectedIndex( list.locationToIndex(e.getPoint()) );
                System.out.println(list.getSelectedValue() +" selected" );
                setupItems();
                popUp.show(list, e.getX(), e.getY());
            }
        }
   };
	
	/**
	 * ButtonListener is an action listener for the buttons
	 * associated with ChannelTab
	 * @author Anders
	 *
	 */
	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == close) {
				manager.closeTab(filter);
				if (isAttached == false) {
					newFrame.dispose();
				}
			} else if (e.getSource() == attach) {
				if (isAttached == true) {
					newFrame = new JFrame();
					newFrame.setPreferredSize(new Dimension(400, 500));
					newFrame.setMinimumSize(new Dimension(300, 300));
					newFrame.add(self);
					newFrame.setVisible(true);
					attach.setText("Attach window");
					//Removes tab from tabManager
					manager.releaseTab(filter);
					
					isAttached = false;
				} else {
					manager.attachTab(filter, self);
					newFrame.remove(self);
					newFrame.dispose();
					attach.setText("Detach tab");
					
					isAttached = true;
				}
			}		
		}
	} 
}