/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author HS Vikar
 */
public class PersonalTab extends TabManager {
	public JPanel panel;
	public JTextArea text;
	
	public PersonalTab () {
	
		panel = new JPanel(false);
		
		text = new JTextArea("HELLALE1111oooooO");
		

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
