
package gruppe.irc;

import java.awt.Color;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * This is the class where our color and font options are stored.
 * 
 * 
 * @author Ch
 */
public class SimpleAttributes extends SimpleAttributeSet {
    
	// This is a static set of SimpleAttributeSets.
	private static SimpleAttributeSet[] attributes;
	
	// Vector that contains information about the Styles.
	private Vector<StyleItem> styles = new Vector<StyleItem>();
	
	// This is the amount of attributeSets available.
    private final static Integer attributeAmount = 10;
	
	// Declaring preferences variable.
	private Preferences pref;
	// Boolean that says if preferences are set or not.
	private Boolean customA;
	
	// This array contains the default colors.
	// They are arranged in the order they appear inside the Color object/class.
	// Except light_grey, as it is used as a background in our frames.
    private final Color defaultColors[] = {
		Color.BLACK,
		Color.BLUE,
		Color.CYAN,
		Color.DARK_GRAY,
		Color.GRAY,
		Color.GREEN,
		Color.MAGENTA,
		Color.ORANGE,
		Color.PINK,
		Color.RED
	};
	
	// This array contains the stylenames
	private final String styleName[] = {
		
		IRCClient.messages.getString("styleN.private"),
		IRCClient.messages.getString("styleN.channel"),
		IRCClient.messages.getString("styleN.server"),
		IRCClient.messages.getString("styleN.random1"),
		IRCClient.messages.getString("styleN.random2"),
		IRCClient.messages.getString("styleN.random3"),
		IRCClient.messages.getString("styleN.random4"),
		IRCClient.messages.getString("styleN.random5"),
		IRCClient.messages.getString("styleN.random6"),
		IRCClient.messages.getString("styleN.random7")
		
	};
	

	
	/*
	 * This is the constructor that initiates all Attribute action
	 * 
	 * Notes:
	 * - This is an example of how font and color are set:
	 *
	 *		StyleConstants.setFontFamily(attrs[0], "SansSerif");
	 *		StyleConstants.setForeground(attrs[0], Color.black);
	 *		 
	**/
    public SimpleAttributes() {
		
		// Creating the array of SimpleAttributeSet.
        attributes = new SimpleAttributeSet[attributeAmount];
		
		// Calling functoin that initiates the different styles.
		initiateStyles();
		
		// Makes sure preferences are available to this class.
		pref = Preferences.userNodeForPackage(this.getClass());
		
		defaultCheck();
		
    }
	
    /**
	 * Initiates the styles by creating new StyleItems and giving them appropriate names.
	 */
	public void initiateStyles() {
		
		for (int i = 0; i < attributeAmount; i++) {
			styles.add(new StyleItem());
			((StyleItem)styles.elementAt(i)).setStyleName(styleName[i]);
		}
	}
	
	/**
	 * Function that figures out if defaults or preferred are to be set.
	 * 
	 */
	private void defaultCheck() {
		
		// Trying to ternary if a node is found.
		// if the node is not null, there is custom prefs.
		// Catching fault by setting default to false.
		try {
			customA = (pref.get("customAttr", null) != null) ? true : false;
			
		} catch (Exception e) {
			customA = false;
		}
		
		// gets preferred attributes if there are customs.
		// In either case the initiating of attributes continue.
		if (customA) {
			getPreferredAttributes();
		}
		initiateAttributes();
	}
 
	/*
	 * Function that picks up the preferences regarding attributes
	 * and puts them inside our array of information about the styles.
	 */
	private void getPreferredAttributes() {
		
		// Goes through each of the attributesets
		for (int i = 0; i < attributeAmount; i++) {
			
			StyleItem tmpStyle = (StyleItem)styles.elementAt(i);
			
			// sets tmpColor to either the customColor or the default color.
			Integer tmpColor = pref.getInt("color"+tmpStyle.getStyleName(), defaultColors[i].getRGB());
			
			// Tries to find a color whos names equals tmpColor, and sets it to Style.colorName;
			// If it fails, customColor[i] stil is set with our default color.
			try {
				
				
				System.out.println((Color) Color.getColor(null, tmpColor));
				
				tmpStyle.setColorType((Color) Color.getColor(null, tmpColor));
			} catch (Exception e) {
				tmpStyle.setColorType(defaultColors[i]);
			}
		}
	}

	/**
	 * Function that updates the attributes by going through the styleItem vector
	 * 
	 * BE AWARE!
	 *		- The functionality within this function exist in other functions within this class.
	 *		Probably should look into a better solution? - Olaf.
	 */
	public void updateAttributes() {
		
		for (int i = 0; i < attributeAmount; i++) {
			
			StyleItem tmpStyle = (StyleItem)styles.elementAt(i);
			
			// Update Colors
			StyleConstants.setForeground(attributes[i], tmpStyle.getColorType());

		}
		// Saving the preferences.
		savePreferences();
	}
	
	/**
	 * Saving preferences using the vector of styleItems.
	 * 
	 * Gets the colorname (string) from styleItem and saves it with
	 * "color" as prefix.
	 */
	public void savePreferences() {
		
		String colorPrefix = "color";
		
		for (int i = 0; i < attributeAmount; i++) {
			
			StyleItem tmpStyle = (StyleItem)styles.elementAt(i);
			
			pref.putInt(colorPrefix+tmpStyle.getStyleName(), tmpStyle.getColorName());
			
		}
		
		//Declaring that custom colors have been set.
		//customA = (pref.nodeExists("customAttr")) ? false : true;
		pref.put("customAttr", "true");
		
	}
	/*
	 * Function that either initiates the default or custom attributes to the attributeSet.
	 * Fonts are not set by default.
	 */
	private void initiateAttributes() {

		// Currently only goes through eventual colors set.
		for (int i = 0; i < attributeAmount; i++) {
			
			StyleItem tmpStyle = (StyleItem)styles.elementAt(i);
			
			Color tmpColor = (customA) ? tmpStyle.getColorType() : defaultColors[i];
			// Add to attributes
			attributes[i] = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes[i], tmpColor);
			
			// Add color to styleitem
			tmpStyle.setColorType(tmpColor);
		}
	}
	
	/**
	 * Function that sets the Color type of a specific style item.
	 * 
	 */
	public void setAttributeColor(Integer index, Color type) {
		
		StyleItem tmpStyle = (StyleItem)styles.elementAt(index);
		tmpStyle.setColorType(type);
	}
	
	
	/**
	 * Below are multiple functions that return useful information to mostly other classes.
	 * 
	 * @return 
	 */
    public SimpleAttributeSet returnAttribute(Integer index) {
        return attributes[index];
    }
    
	public Integer returnAttributeAmount() {
		return attributeAmount;
	}
	
	public String returnAttributeStyleName (Integer index) {
		
		StyleItem tmpStyle = (StyleItem)styles.elementAt(index);
		return tmpStyle.getStyleName();
	}
	
	public Color returnAttributeColor (Integer index) {
		
		StyleItem tmpStyle = (StyleItem)styles.elementAt(index);
		return tmpStyle.getColorType();
		
	}
}
