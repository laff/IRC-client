package gruppe.irc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class for the personalTabs, basic functionality inherited from GenericTab.
 * @author Olaf
 */
public class PersonalTab extends GenericTab {

	private JButton close, attach;
	private PersonalTab self;
	private JFrame newFrame;
    private JPanel panel;
	
	public PersonalTab (String stringF, TabManager mng, Dimension dim) {
	
		super(stringF, mng, dim);
        
		panel = new JPanel();
		close = new JButton(IRCClient.messages.getString("pTab.close"), null);
		attach = new JButton(IRCClient.messages.getString("pTab.detach"), null);
		
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
					attach.setText(IRCClient.messages.getString("pTab.attach"));
					
					//Removes tab from tabManager
					manager.releaseTab(filter);
					isAttached = false;
				} else {
					manager.attachTab(filter, self);
					newFrame.remove(self);
					newFrame.dispose();
					attach.setText(IRCClient.messages.getString("pTab.detach"));
					
					isAttached = true;
				}
			}		
		}
	}
}
