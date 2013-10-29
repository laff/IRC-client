/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author HS Vikar
 */
public class IRCClient {
	
	public static void main(String[] args) {
		
		//Sets look and feel to system default
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException f) {
			System.out.println("Could not find system look and feel. Error: ");
			f.printStackTrace();
		} catch (InstantiationException f) {
			System.out.println("Could not use system look and feel. Error: ");
			f.printStackTrace();
		} catch (IllegalAccessException f) {
			System.out.println("Could not access system look and feel. Error: ");
			f.printStackTrace();
		} catch (UnsupportedLookAndFeelException f) {
			System.out.println("Unsupported look and feel. Error: ");
			f.printStackTrace();
		}

		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		/*
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
		*/
		LoginMenu loginMenu = new LoginMenu(null);
		
		
    }
		
    /**
     * Create the GUI for the TabManager and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("IRC Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add content to the window.
		
        frame.add(new TabManager(), BorderLayout.CENTER);
        
        //Display the window.
        //frame.pack();
		frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
