package gruppe.irc;

/**
 * A class that holds information about one line in the servers.ini file.
 * @author Ch
 */
public class ServerListItem {
    String serverName, group, address, portRange;
    int index;

    public ServerListItem(int ind, String serv, String grp, String adr, String prtRange) {
        this.index = ind;
        this.serverName = serv;
        this.group = grp;
        this.address = adr;
        this.portRange = prtRange;
    } 
    
    public int getIndex() {
        return this.index;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getPortRange() {
        return this.portRange;
    }
    
    public void setIndex(int ind) {
        this.index = ind;
    }
    
    public void setGroup(String grp) {
        this.group = grp;
    }
    
    public void setServerName(String srvName) {
        this.serverName = srvName;
    }
    
    public void setAddress(String adr) {
        this.address = adr;
    }
    
    public void setPortRange(String prt) {
        this.portRange = prt;
    } 
    
    /**
     * toString-method that adds serveritems to the file, with the same
     * format as the original servers.ini
     * @return a line that holds information about one server.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("n").append(this.index).append("=");
        sb.append(getServerName()).append("SERVER:");
        sb.append(getAddress()).append(":");
        sb.append(getPortRange()).append("GROUP:");
        sb.append(getGroup());
        return sb.toString();
    }
}
