/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.Color;

/**
 *
 * @author John
 */
public class StyleItem {
	
	private String styleName, colorName, fontName;
	private Color colorType;
	
	public StyleItem () {
	
		styleName = "";
		colorName = "";
		fontName = "";
		colorType = null;
		
	}
	
	public void setStyleName (String name) {
		styleName = name;
	}
	
	public String getStyleName () {
		return styleName;
	}
	// OBS! using setColorName might very well be excessive.
	public void setColorType (Color type) {
		colorType = type;
		setColorName(colorType.toString());
	}
	
	public Color getColorType () {
		return colorType;
	}
	
	// OBS! This might very well be excessive.
	public void setColorName(String name) {
		colorName = name;
	}
	
	public String getColorName() {
		return colorName;
	}
}
