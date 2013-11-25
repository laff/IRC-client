package gruppe.irc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Simple window used for adding a server to the 'servers.ini'-file.
 * @author Ch
 */
public class ServerEditorWindow extends JFrame {

   private JTextField serverName, groupName, portRange, servAddr;
   private JPanel myPanel; 
   private JLabel srvNmL, grpNmL, prtRngL, srvAdrL;
   private JButton addItem, close;
  
    ServerEditorWindow() {
    
        super(IRCClient.messages.getString("srvEd.windTitle"));    
        setSize(330,280);
        
        myPanel = new JPanel();
        srvNmL = new JLabel(IRCClient.messages.getString("srvEd.srvName"));
        grpNmL = new JLabel(IRCClient.messages.getString("srvEd.grpName"));
        prtRngL = new JLabel(IRCClient.messages.getString("srvEd.prtRnge"));
        srvAdrL = new JLabel(IRCClient.messages.getString("srvEd.srvAdr"));

        setLocationRelativeTo(null);
        
        serverName = new JTextField(15);
        groupName = new JTextField(15);
        portRange = new JTextField(10);
        servAddr = new JTextField(15);
        
        addItem = new JButton(IRCClient.messages.getString("srvEd.add"));
        close = new JButton(IRCClient.messages.getString("srvEd.close"));

        myPanel.setLayout(null);

        srvNmL.setBounds (15, 5, 85, 20);
        grpNmL.setBounds (15,30, 85, 20);
        prtRngL.setBounds(15,55, 85, 20);
        srvAdrL.setBounds(15,80, 85, 20);


        serverName.setBounds(90, 5, 160, 20);
        groupName.setBounds	(90,30, 160, 20);
        portRange.setBounds (90,55, 160, 20);
        servAddr.setBounds  (90,80, 160, 20);
        addItem.setBounds(55, 120, 80, 20);
        close.setBounds(145, 120, 80, 20);
        
        addItem.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent ae) {
                addServer();
            }
        });
        
        close.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent ae) {
                dispose();
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
        myPanel.add(close);

        setVisible(true);
        getContentPane().add(myPanel);
    }
    
    /**
     * Adding the values from the input-fields to the serverlist in LoginMenu.
     * Also writing the added server to the server-file, so it is saved for next
     * time the application is run.
     */
    private void addServer() {
        ServerListItem s;
        
        if (!serverName.getText().isEmpty() && !groupName.getText().isEmpty() && 
                !servAddr.getText().isEmpty() && !portRange.getText().isEmpty()) {
            s = new ServerListItem(LoginMenu.sli.size(), serverName.getText(), groupName.getText(),
                    servAddr.getText(), portRange.getText());
            LoginMenu.sli.addElement(s);
            LoginMenu.writeFile();
            
        } else
            JOptionPane.showMessageDialog(ServerEditorWindow.this, IRCClient.messages.getString("srvEd.empty"), 
                    null, JOptionPane.ERROR_MESSAGE);
    }
}
