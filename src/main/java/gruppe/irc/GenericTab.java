/**
 * 
 */
package gruppe.irc;

import javax.swing.JInternalFrame;

/**
 * @author Anders
 *
 */
public class GenericTab extends JInternalFrame {
	
	private String filter;
	
	
	public void GenericTab (String tabFilter) {
		String filter = tabFilter;
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
