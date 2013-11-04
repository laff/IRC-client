/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.GridLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author HS Vikar
 */
public class PersonalTab extends JInternalFrame {
	public JPanel panel;
	public JTextArea text;
	
	public PersonalTab () {
	
		panel = new JPanel(false);
		
		text = new JTextArea();
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		

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
