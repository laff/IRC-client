package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

/**
 * The layout of a channeltab, it extends GenericTab to get some of the mutual
 * functions for all tabs, but it also need some specificed functionality.
 * @author Christian
 */
public class ChannelTab extends GenericTab  {
	
	private JButton close, attach;
    private JPanel panel;
	private JFrame newFrame;
    private JList list;
    private JMenuItem item;
    private JPopupMenu popUp;
    private JMenu modes;
    private JSplitPane splitPane;
    private JScrollPane usersScrollPane;
    private ChannelTab self;
    private DefaultListModel listModel;
	private ArrayList sortedNames;
    
    
	public ChannelTab (String chanName, TabManager mng, Dimension dim) {
		super(chanName, mng, dim);
          
		//Magic numbers
		int width = 30;
		double resizeWeight = 0.92;

        listModel = new DefaultListModel();
   
        add(scrollPane, BorderLayout.WEST);
        list = new JList(listModel);
        usersScrollPane = new JScrollPane(list);
        add(usersScrollPane, BorderLayout.EAST);
        
        //Splits the users and text components.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    scrollPane, usersScrollPane);
        
        // Setting some values for our list.
        list.setVisible(true);
        list.setBackground(Color.LIGHT_GRAY);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseClick());
        list.setSize(dim.height, width);
        list.setFixedCellWidth(width);

        //We want the textpane to be the left component, we also want the left
        //component to have the highest weighting when resizing the window.
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(usersScrollPane);
        splitPane.setResizeWeight(resizeWeight);

        add(splitPane, BorderLayout.CENTER);
        
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
            addText(this.filter, newUser+" "+IRCClient.messages.getString("chan.join")+"\n", false, 2);
            
        } else if (command.equals("PART")) {
            addText(this.filter, newUser+" "+IRCClient.messages.getString("chan.part")+"\n", false, 2);
            
        } else if (command.startsWith("+") || command.startsWith("-")) {
            addText(this.filter, newUser+" "+IRCClient.messages.getString("chan.setMode")+" "+command, false, 2);
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
		
		sortedNames = sortNames(namesSplitted);
		
		updateNameList();
    }
    
    /**
     * The fully sorted 'sortedNames'-array is added to the list-model, one by one.
     * Run as an own thread, to prevent issues when a lot of users to be added.
     */
	public void updateNameList() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				listModel.removeAllElements();

				for(int i = 0; i < sortedNames.size(); i++) {
					listModel.addElement(sortedNames.get(i));
				}
			}
		});
	}
	
    /**
     * Sorting the nicks of the users on a chan. First divided into Op`ed, Voiced,
     * and normal users. Then these lists are sorted separately, before they are
     * merget into the sorted-array, which we return in this method.
     * @param names String-array of all users on channel.
     * @return The same users, sorted.
     */
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
                addText(nickName+" "+IRCClient.messages.getString("chan.quit"), message, false, 2);
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
        
        item = new JMenuItem(IRCClient.messages.getString("popUp.whois"));
        popUp.add(item);

        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    String temp =  list.getSelectedValue().toString();
                    whois(temp);
                } catch (NullPointerException npe) {}
            }
        });
        
        item = new JMenuItem( IRCClient.messages.getString("popUp.query") );
        popUp.add(item);
 
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
        item = new JMenuItem(IRCClient.messages.getString("popUp.voice"));
        modes.add(item);
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if(selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setVoice(selected, true);
            }
        });
        
        item = new JMenuItem(IRCClient.messages.getString("popUp.deVoice"));
        modes.add(item);
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if(selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setVoice(selected, false);
            }
        });
        
        item = new JMenuItem(IRCClient.messages.getString("popUp.op"));
        modes.add(item);
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if (selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setOp(selected, true);
            }
        });
        
        item = new JMenuItem(IRCClient.messages.getString("popUp.deOp"));
        modes.add(item);
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if (selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                setOp(selected, false);
            }
        });
        
        modes.add(item = new JMenuItem(IRCClient.messages.getString("popUp.kick")));
        
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String selected = list.getSelectedValue().toString();
                
                if (selected.startsWith("@") || selected.startsWith("+")) {
                    selected = selected.substring(1);
                }
                kickUser(selected);
            }
        });
        
        popUp.add(modes);
    }
    
    /**
     * Used to send command for setting voice or devoice.
     * @param selectedUser target of the action.
     * @param bool wether it`s a voice or devoice-action.
     */
    private void setVoice(String selectedUser, Boolean bool) {
        String mode = bool ? " +v " : " -v ";
        writeToLn("MODE "+this.filter+mode+selectedUser);
    }
    
    /**
     * Used to issue the command to give someone Op, or Deop someone.
     * @param selectedUser the selected target of the action.
     * @param bool true is Op, false is Deop
     */
    private void setOp(String selectedUser, Boolean bool) {
        String mode = bool ? " +o " : " -o ";
        writeToLn("MODE "+this.filter+mode+selectedUser);
    }
    
    /**
     * Part of the action when our user rightclicks, and choose 'Kick' on 
     * the selected user.
     * @param selectedUser Username of the selected user.
     */
    private void kickUser(String selectedUser) {
        writeToLn("KICK "+this.filter+" "+selectedUser);
    }

    /**
     * If someone was kicked from the channel, the action is displayed, and also
     * the list of names is updated.
     * @param sender Who is kicking someone.
     * @param target Who is being kicked.
     */
    public void updateKick(String sender, String target) {
        addText(this.filter, target+" "+IRCClient.messages.getString("chan.kick")+" "+sender, false, 2);
        writeToLn("NAMES "+this.filter); 
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
           } catch (NullPointerException npe) {
                System.out.println(IRCClient.messages.getString("nullPointer")+": "+npe.getMessage());
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
					//Magic numbers
					int width = 400;
					int height = 500;
					int minDim = 300;
					
					newFrame = new JFrame();
					newFrame.setPreferredSize(new Dimension(width, height));
					newFrame.setMinimumSize(new Dimension(minDim, minDim));
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

   