/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gruppe.irc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Ch
 */
public class ServerEditorWindow extends JFrame {
   private JLabel srvNmL, grpNmL, prtRngL, srvAdrL;
   private JTextField serverName, groupName, portRange, servAddr;
  // private Vector<ServerListItem> serverList;
   private JButton addItem;
    
    ServerEditorWindow() {
    
        super("EpicServerEditorWindow");    
        setSize(330,280);
        JPanel myPanel = new JPanel();
        srvNmL = new JLabel("Server name");
        grpNmL = new JLabel("Group name");
        prtRngL = new JLabel("Port(s)");
        srvAdrL = new JLabel("Server address");

        serverName = new JTextField(15);
        groupName = new JTextField(15);
        portRange = new JTextField(10);
        servAddr = new JTextField(15);
        
        addItem = new JButton("Add");

        myPanel.setLayout(null);

        srvNmL.setBounds (20, 5, 85, 20);
        grpNmL.setBounds (20,30, 85, 20);
        prtRngL.setBounds(20,55, 85, 20);
        srvAdrL.setBounds(20,80, 85, 20);


        serverName.setBounds(90, 5, 160, 20);
        groupName.setBounds	(90,30, 160, 20);
        portRange.setBounds (90,55, 160, 20);
        servAddr.setBounds  (90,80, 160, 20);
        
        addItem.setBounds(25, 120, 80, 20);
        addItem.addActionListener(new ActionListener() {
    
            public void actionPerformed (ActionEvent ae) {
                addServer();
            }
        });
        
        

        myPanel.add(srvNmL);
        myPanel.add(grpNmL);
        myPanel.add(prtRngL);
        myPanel.add(srvAdrL);
        myPanel.add(serverName);
        myPanel.add(groupName);
        myPanel.add(portRange);
        myPanel.add(servAddr);
        myPanel.add(addItem);

        setVisible(true);
        getContentPane().add(myPanel);
    }
    
    private void addServer() {
        ServerListItem s;
        
        s = new ServerListItem(LoginMenu.sli.size(), serverName.getText(), groupName.getText(), servAddr.getText(), portRange.getText());
        LoginMenu.sli.addElement(s);
        LoginMenu.writeFile();
    }
 
    
    
}