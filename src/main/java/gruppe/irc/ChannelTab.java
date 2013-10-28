/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author HS Vikar
 */
public class ChannelTab extends JPanel {
	
	public JPanel panel;
	public JTextArea text;
	
	public ChannelTab () {
	
		panel = new JPanel(false);
		
		text = new JTextArea("HELLALE1111O");
		

        panel.setLayout(new GridLayout(1, 1));
        panel.add(text);
 
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
		
	public void addText(String msg) {
		
		text.append(msg);
		
	}
	
	
}