/**
 * 
 */
package gruppe.irc;

import javax.swing.JInternalFrame;

/**
 * @author Anders
 * 
 * Greetings. This is the GenericTab class, and here is a list of commands:
 * http://en.wikipedia.org/wiki/List_of_Internet_Relay_Chat_commands
 *
 */
public class GenericTab extends JInternalFrame {
	
	private String filter;
		

	public GenericTab (String tabFilter) {
		filter = tabFilter;
	}
	
	/**
	 * Function returns the filter text for the tab
	 * @return 
	 */
	public String getFilter () {
		return filter;
	}
	
	/**
	 * Function displays text to the text field
	 */
	public void addText() {
		//TODO
	}
	
	
}
