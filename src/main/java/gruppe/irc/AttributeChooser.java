/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author John
 */
public class AttributeChooser extends JFrame {
	
	private SimpleAttributes theAttributes;
	
	private DefaultListModel styleListModel;
	private JList styleList;
	private JButton changeFont, changeColor, confirm;
	
	private Integer selectedRow;
	
	private Integer listHeight;
	private Integer elementHeight = 20;
	private Integer frameSpace = 20;
	
	private Integer attrAmount;
	
	private Color selectedColor;
	
	
	/**
	 * Constructor.
	 * 
	 * This frame is created in IRCClient, but is set to visibility false.
	 */
	public AttributeChooser() {
	
		theAttributes = IRCClient.attrs;
		
		calcListLength();
		
		// Settings related to the JFrame.
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setSize(300, listHeight);
		setVisible(false);
		
		// The list
		styleListModel = new DefaultListModel();
		styleList = new JList(styleListModel);
		styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// The buttons
		changeFont = new JButton(IRCClient.messages.getString("attrC.changeFont"));
		changeColor = new JButton(IRCClient.messages.getString("attrC.changeColor"));
		confirm = new JButton(IRCClient.messages.getString("attrC.apply"));
		
		// Oh I know you did'nt.
		fillList();
		
		// Adding listeners with this function
		listenUp();
		
		// addings elements to the JFrame.
		add(styleList, BorderLayout.NORTH);
		add(changeFont, BorderLayout.WEST);
		add(changeColor, BorderLayout.CENTER);
		add(confirm, BorderLayout.EAST);
		
	}
	/**
	 * Grand function adding listeners to desverving items.
	 */
	private void listenUp() {
		
		changeFont.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent ae) {
				
				//Do stuff
				
			}
			
		});
		
		changeColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				Integer selectedStyle = styleList.getSelectedIndex();
				
				if (selectedStyle >= 0) {
					
					JFrame jCC = new JFrame();
					String message = IRCClient.messages.getString("attrC.choose");
					Color initialColor = theAttributes.returnAttributeColor(selectedStyle);
					
					// Using the color previously set as default for the dialog.
					selectedColor = JColorChooser.showDialog(jCC, message, initialColor);
					
					selectedColor = (selectedColor != null) ? selectedColor : initialColor;
					
					// Setting the chosen color
					theAttributes.setAttributeColor(selectedStyle, selectedColor);
				}	
			}
		});
		
		/**
		 * Action when pressing the "Confirm".
		 * 
		 * First updates the attributes (so that they are immediately used).
		 * Then saves these attributes to preferences.
		 */
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				theAttributes.updateAttributes();
			}
		});
	}
	
	/**
	 * 
	 * @param really 
	 */
	public void ShowFrame(Boolean really) {
		setVisible(really);
	}
	
	/**
	 * 
	 */
	private void fillList() {
		for (int i = 0; i < attrAmount; i++) {
			styleListModel.addElement(findStyleName(i));
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return 
	 */
	private String findStyleName(Integer index) {
		return theAttributes.returnAttributeStyleName(index);
	}
	
	/**
	 * Method for calculating the height of our frame.
	 * This way it is dynamic incase the amount of attributes change.
	 */
	private void calcListLength() {
		attrAmount = theAttributes.returnAttributeAmount();
		listHeight = ((attrAmount * elementHeight) + (frameSpace * 2)); 
	}	
}