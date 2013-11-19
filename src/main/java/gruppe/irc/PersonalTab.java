package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author HS Vikar
 */
public class PersonalTab extends GenericTab {

	private JButton close, attach;
	private JPanel panel;
	private PersonalTab self;
	private JFrame newFrame;
	
	public PersonalTab (String stringF, TabManager mng, Dimension dim) {
	
		super(stringF, mng, dim);
		panel = new JPanel();
		close = new JButton("Close private chat", null);
		attach = new JButton("Detach tab", null);
		
		close.addActionListener(new ButtonListener());
		attach.addActionListener(new ButtonListener());
		
		panel.add(attach);
		panel.add(close);
		add(panel, BorderLayout.NORTH);
		self = PersonalTab.this;
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
