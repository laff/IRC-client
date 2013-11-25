/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author John
 */
public class ChannelList extends JFrame implements DocumentListener {
	
	private TabManager manager;
	
	private DefaultListModel channelListModel;
	private JList channelList;
	private JScrollPane listPane;
	private JTextField searchField;
	
	private List channels;
	
	private Integer widthish = 300;
	private Integer heightish = 400;
	
	private String titleName;

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
		channelList.addMouseListener(new MouseClick());
		
		listPane = new JScrollPane(channelList);
		
		searchField = new JTextField();
		searchField.getDocument().addDocumentListener(this);
		
		add(searchField, BorderLayout.NORTH);
		add(listPane, BorderLayout.CENTER);	
	}
	
	public void addChannels(Vector<String> kanels) {

		channels = kanels;

	}
	

	
    public void insertUpdate(DocumentEvent ev) {
        search();
    }
     
    public void removeUpdate(DocumentEvent ev) {
        search();
    }

	public void changedUpdate(DocumentEvent de) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	/**
	 * Function that gets the current string, with our without content,
	 * and sends it with # as prefix.
	 */
    public void search() {
       
		// Action to deselect list item?
         
        String s = searchField.getText();

		assembleList(s);
    }
	
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
	 * 
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
                System.out.println(IRCClient.messages.getString("nullPointer")+": "+npe.getMessage());
           }
        }
   };
}
