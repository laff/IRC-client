
package gruppe.irc;

import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Ch
 */
public class SimpleAttributes extends SimpleAttributeSet {
    private SimpleAttributeSet[] attrs;
    
            
    public SimpleAttributes() {
        attrs = new SimpleAttributeSet[4];

        attrs[0] = new SimpleAttributeSet();
        
        StyleConstants.setForeground(attrs[0], Color.black);

        attrs[1] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setFontFamily(attrs[0], "SansSerif");
        StyleConstants.setForeground(attrs[1], Color.green);

        attrs[2] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setForeground(attrs[2], Color.red);
       
        attrs[3] = new SimpleAttributeSet(attrs[0]);
        StyleConstants.setForeground(attrs[3], Color.blue);
    }
    
    public SimpleAttributeSet[] getAttributes() {
        return attrs;
    }
    
}
