package gruppe.irc;

/**
 *
 * @author Ch
 */
public class ServerListItem {
    String serverName, group, relName;

    public ServerListItem(String serv, String grp, String rel) {
        this.serverName = serv;
        this.group = grp;
        this.relName = rel;
    } 
    
    public String getGroup() {
        return this.group;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public String getRelName() {
        return this.relName;
    }
}
