package gruppe.irc;

import gruppe.irc.PersonalTab.ButtonListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 *
 * @author Christian
 */
public class ChannelTab extends GenericTab {
	
	private JScrollPane usersScrollPane;
	private JSplitPane splitPane;
	private JButton close, attach;
	private JPanel panel;
	private ChannelTab self;
	private JFrame newFrame;
    private JList list;
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
        list.setBackground(Color.darkGray);

        
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
		
		self = this;
	}
    
    /**
     * Function to split the string of all the users on this channel, and add each
     * username into an array. Then all the users are added to the listModel.
     * @param names String including the result of a NAMES-command
     */
    
    public void updateNames (String names) {
        String namesSplitted[];
        namesSplitted = names.split(" ");
        
        for(int i = 0; i < namesSplitted.length; i++) {
            listModel.addElement(namesSplitted[i]);
        }
    }

	
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