/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author HS Vikar
 */
public class PersonalTab extends GenericTab {

	JButton close, attach;
	JPanel panel;
	
	public PersonalTab (String stringF, TabManager mng) {
	
		super(stringF, mng);
		panel = new JPanel();
		close = new JButton("Close private chat", null);
		attach = new JButton("Attach/Detach window", null);
		
		close.addActionListener(new ButtonListener());
		attach.addActionListener(new ButtonListener());
		
		panel.add(attach);
		panel.add(close);
		add(panel, BorderLayout.NORTH);
		

	}
	
	
	/**
	 * ButtonListener is an action listener for the buttons
	 * associated with PersonalTab
	 * @author Anders
	 *
	 */
	class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == close) {
				manager.closeTab(filter);
			} else {
				addText("This does nothing right now");
			}
			
		}
		
	}
	
}
