package gruppe.irc;

/**
 * A class that holds information about one line in the servers.ini file.
 * @author Anders, Christian and Olaf.
 */
public class ServerListItem {
    private String serverName, group, address, portRange;
    private int index;

	/**
	 * Constructor receiving variables used in this list item.
	 * @param ind
	 * @param serv
	 * @param grp
	 * @param adr
	 * @param prtRange 
	 */
    public ServerListItem(int ind, String serv, String grp, String adr, String prtRange) {
        this.index = ind;
        this.serverName = serv;
        this.group = grp;
        this.address = adr;
        this.portRange = prtRange;
    } 
    
	/**
	 * @return index of this item. 
	 */
    public int getIndex() {
        return this.index;
    }
    
	/**
	 * @return name of this items group. 
	 */
    public String getGroup() {
        return this.group;
    }
    
	/**
	 * @return name of this items server. 
	 */
    public String getServerName() {
        return this.serverName;
    }
    
	/**
	 * @return name of this items address.
	 */
    public String getAddress() {
        return this.address;
    }
    
	/**
	 * @return port range of this item. 
	 */
    public String getPortRange() {
        return this.portRange;
    }
    
	/**
	 * @param ind index to be set for this item.
	 */
    public void setIndex(int ind) {
        this.index = ind;
    }
    
	/**
	 * @param grp group name ot be set for this item. 
	 */
    public void setGroup(String grp) {
        this.group = grp;
    }
    
	/**
	 * @param srvName server name to be set for this item.
	 */
    public void setServerName(String srvName) {
        this.serverName = srvName;
    }
    
	/**
	 * @param adr address to be set for this item.
	 */
    public void setAddress(String adr) {
        this.address = adr;
    }
    
	/**
	 * @param prt ports range to be set for this item.
	 */
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
