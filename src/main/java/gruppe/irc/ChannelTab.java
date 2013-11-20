package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


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
    private JMenu modes;
    private DefaultListModel listModel;
	

    //TODO: Maybe some minimum-values should be set for the components in the splitpane?
    
	public ChannelTab (String chanName, TabManager mng, Dimension dim) {
		super(chanName, mng, dim);
          
        // Adding some elements to the list, the hard way.
        listModel = new DefaultListModel();
   
        add(scrollPane, BorderLayout.WEST);
        add(usersScrollPane = new JScrollPane(list = new JList(listModel)), BorderLayout.EAST);
        
        //Splits the users and text components.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    scrollPane, usersScrollPane);
        
        // Setting some values for our list.
        list.setVisible(true);
        list.setBackground(Color.LIGHT_GRAY);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseClick());
        list.setSize(dim.height, 30);
        list.setFixedCellWidth(30);
                
        
        
     //   ListSelectionModel listSelectionModel = list.getSelectionModel();
      //  listSelectionModel.addListSelectionListener(new ListListener());
        
      //  list.setSelectionModel(listSelectionModel);


        //We want the textpane to be the left component, we also want the left
        //component to have the highest weighting when resizing the window.
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(usersScrollPane);
        splitPane.setResizeWeight(0.92);

        add(splitPane, BorderLayout.CENTER);
        
        //TEMP: Background color set just to show the diff
        
		panel = new JPanel();
		close = new JButton(IRCClient.messages.getString("chan.close"), null);
		attach = new JButton(IRCClient.messages.getString("chan.detach"), null);
		
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
            addText(this.filter, newUser+" has joined the channel\n", false, 2);
        } 
        else if (command.equals("PART")) {
            addText(this.filter, newUser+" has left the channel\n", false, 2);
        }
        else if(command.startsWith("+") || command.startsWith("-")) {
            addText(this.filter, newUser+" sets mode: "+command, false, 2);
        }
        writeToLn("NAMES "+this.filter);      
    }
    
    /**
     * Function to split the string of all the users on this channel, and add each
     * username into an array. Then all the users are added to the listModel.
     * @param names String including the result of a NAMES-command
     * 
     * OBS: Must test this sorting-thing, haven`t tested what happens when someone
     * is voiced on the channel. But OP`s is listed at the top!
     */
    public void addNames (String names) {
        String namesSplitted[];
        ArrayList sorted;
        
        
        namesSplitted = names.split(" ");
        sorted = sortNames(namesSplitted);
        
        listModel.removeAllElements();
        
       // listModel.add(sorted);
        for(int i = 0; i < sorted.size(); i++) {
            listModel.addElement(sorted.get(i));
        }
    }
    
    private ArrayList sortNames (String [] names) {
        ArrayList<String> sorted = new ArrayList<String>();
        ArrayList<String> op = new ArrayList<String>();
        ArrayList<String> voice = new ArrayList<String>();
        ArrayList<String> regular = new ArrayList<String>();
        char firstChar;
        
        for (int i = 0; i < names.length; i++) {
             firstChar = names[i].charAt(0);
            
            switch (firstChar) {
                
                case '@' :
                    op.add(names[i]);
                        break;
                case '+' : 
                    voice.add(names[i]);
                        break;
                default :
                    regular.add(names[i]);
                        break;
            }
        }
        
        Collections.sort(op);
        Collections.sort(voice);
        Collections.sort(regular);
        
        sorted.addAll(op);
        sorted.addAll(voice);
        sorted.addAll(regular);
        
       return sorted; 
    } 
    
    /**
     * When using whois, we must send the nickname as parameter. If a user
     * is OP or voiced we have their nickname with a '@' or '+' in our list, so
     * this must be removed before processing the WHOIS.
     */
    private void whois(String user) {
        String whoIsUser = user;
        
        if(user.startsWith("@") || user.startsWith("+")) {
            whoIsUser = user.substring(1);
        }       
        writeToLn("WHOIS "+whoIsUser); 
    }
    
    /**
     * When someone quits IRC, without any part being issued to our channel, we
     * must find which user that left, and if he was a member of our chan.
     * @param nickName The nick of the user.
     * @param message The user might left a quit-message.
     */
    public void quit(String nickName, String message) {
        int users = listModel.size();
        
        for (int i = 0; i < users; i++) {
            if (nickName.equals(listModel.getElementAt(i))) {
                addText(nickName+" has quit IRC", message, false, 2);
                writeToLn("NAMES "+this.filter);
                break;
            }
        }
    }

    /**
     * Set up the different items in the popup-menu when rightclicking.
     */
    private void setupItems() {    
        popUp = new JPopupMenu();
        
        popUp.add(item = new JMenuItem(IRCClient.messages.getString("popUp.whois")));

        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    String temp =  list.getSelectedValue().toString();
                    whois(temp);
                    System.out.println("ActionPerformed on target: "+temp);
                } catch (NullPointerException npe) {}
            }
        });
        
        popUp.add(item = new JMenuItem(IRCClient.messages.getString("popUp.query")));
 
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                try {
                    if(selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                manager.createPersonalTab(selected);
                } catch (NullPointerException npe) {}
            }
        });
        
        popUp.addSeparator();
        modes = new JMenu(IRCClient.messages.getString("popUp.modes"));
        modes.add(item = new JMenuItem(IRCClient.messages.getString("popUp.voice")));
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if(selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setVoice(selected, true);
            }
        });
        
        modes.add(item = new JMenuItem(IRCClient.messages.getString("popUp.deVoice")));
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if(selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setVoice(selected, false);
            }
        });
        
        modes.add(item = new JMenuItem(IRCClient.messages.getString("popUp.op")));
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if (selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setOp(selected, true);
            }
        });
        
        modes.add(item = new JMenuItem(IRCClient.messages.getString("popUp.deOp")));
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if (selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setOp(selected, false);
            }
        });
        
        popUp.add(modes);
    }
    
    private void setVoice(String selectedUser, Boolean bool) {
        
        String mode = bool ? " +v " : " -v ";
        writeToLn("MODE "+this.filter+mode+selectedUser);
    }
    
    private void setOp(String selectedUser, Boolean bool) {
        
        String mode = bool ? " +o " : " -o ";
        writeToLn("MODE "+this.filter+mode+selectedUser);
    }
    
    /**
     * MouseListener for our list of users. On doubleclick a PersonalTab with
     * that user is opened. On right-click a popupmenu shows up on the mouseovered
     * users.
     */
   private class MouseClick extends MouseAdapter {
        
       @Override
       public void mouseClicked(MouseEvent e) {
           try {
                if (e.getClickCount() == 2) {
                    String selected = list.getSelectedValue().toString();
                    // If the user we want to chat with is OP or voiced, we must
                    // remove the first letter when creating a personaltab.
                    if (selected.startsWith("@") || selected.startsWith("+")) {
                        selected = selected.substring(1);
                    }
                    manager.createPersonalTab(selected);
                    list.clearSelection();
                 }
                else if (e.isMetaDown()) {
                    list.setSelectedIndex(list.locationToIndex(e.getPoint()));
                    System.out.println(list.getSelectedValue() +" selected" );
                    setupItems();
                    popUp.show(list, e.getX(), e.getY());
                }


                //Test det her med flere brukere inne på chan:
                if (!list.getCellBounds(list.getSelectedIndex(), list.getSelectedIndex()).contains(e.getPoint())){
                        list.removeSelectionInterval(list.getSelectedIndex(),list.getSelectedIndex());
                }      
           } catch (NullPointerException npe) {};
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
					attach.setText(IRCClient.messages.getString("chan.attach"));
					//Removes tab from tabManager
					manager.releaseTab(filter);
					
					isAttached = false;
				} else {
					manager.attachTab(filter, self);
					newFrame.remove(self);
					newFrame.dispose();
					attach.setText(IRCClient.messages.getString("chan.detach"));
					
					isAttached = true;
				}
			}		
		}
	} 
}

   