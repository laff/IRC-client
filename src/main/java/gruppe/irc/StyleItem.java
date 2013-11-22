/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.Color;

/**
 * This class contains information about each style.
 * This information is style name (based on the en_US.properties), 
 * color, the colors name and the font name.
 * @author John
 */
public class StyleItem {
	
	// Private class variables.
	private String styleName, fontName;
	private Integer colorName;
	private Color colorType;
	
	/**
	 * Constructor
	 */
	public StyleItem () {
		styleName = "";
		colorName = 0;
		fontName = "";
		colorType = null;
	}
	
	/**
	 * This sets the style name.
	 * @param name : name of this style.
	 */
	public void setStyleName (String name) {
		styleName = name;
	}
	
	/**
	 * This returns the style name.
	 * @return : style name.
	 */
	public String getStyleName () {
		return styleName;
	}

	/**
	 * Function setting the type of the color, but also sets the color's name.
	 * 
	 * OBS! using setColorName might very well be excessive.
	 * @param type 
	 */
	public void setColorType (Color type) {
		colorType = type;
		setColorName(colorType.getRGB());
	}
	
	/**
	 * Returns the color.
	 * @return 
	 */
	public Color getColorType () {
		return colorType;
	}
	
	/**
	 * function that sets the name this item's color.
	 * OBS! This might very well be excessive.
	 * @param name 
	 */
	public void setColorName(Integer name) {
		colorName = name;
	}
	
	/**
	 * Returns the name of this items color.
	 * @return 
	 */
	public Integer getColorName() {
		return colorName;
	}
}
