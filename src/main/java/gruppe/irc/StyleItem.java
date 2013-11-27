package gruppe.irc;

import java.awt.Color;
import java.awt.Font;

/**
 * This class contains information about each style.
 * This information is style name (based on the en_US.properties), 
 * color, the colors name and the font name.
 * @author Olaf
 */
public class StyleItem {
	
	// Private class variables.
	private String styleName, fontName;
	private Integer colorName, fontSize;
	private Color colorType;
	private Font fontSpec;
	
	/**
	 * Constructor
	 */
	public StyleItem () {
		styleName = "";
		colorName = 0;
		colorType = null;
		fontSpec = null;
		fontSize = 0;
		fontName = "";
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
	 * Function receiving this item's new font component.
	 * Storing both the component and its fontname and size.
	 * @param font 
	 */
	public void setFontSpec(Font font) {
		fontSpec = font;
		
		fontName = fontSpec.getFontName();
		fontSize = fontSpec.getSize();
	}
	
	/**
	 * Function that sets the size and name of the font.
	 * It does not bother with setting the fontSpec, 
	 * as that variable is only used when choosing font.
	 * only used
	 * @param size
	 * @param name 
	 */
	public void setFont(Integer size, String name) {
		fontName = name;
		fontSize = size;
	}
	
	
	/**
	 * Boolean that checks if fontSpec is set.
	 * @return 
	 */
	public Boolean areFont() {
		return (fontName.equals("")) ? false : true;
	}
	
	/**
	 * Returns the font size.
	 * @return 
	 */
	public Integer getFontSize() {
		return fontSize;
	}
	
	/**
	 * Returns the family name of font.
	 * @return 
	 */
	public String getFontName() {
		return fontName;
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
