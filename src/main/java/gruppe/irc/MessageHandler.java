package gruppe.irc;

/**
 * Class made for handling all message-events that occurs in our client
 * @author Christian
 */
public class MessageHandler {
    private String pref;
    private String restMessage;
    private String chanName;
    private TabManager manager;
    
    /**
     * Constructor for our MessageHandler.
     * @param mng our TabManager, for access to tab-functions.
     */
    MessageHandler(TabManager mng) {
        manager = mng;
    }
    
    /**
     * Message to a specific chan, channelname and the message is parsed out, and
     * sent to the method that distributes messages to channels. A user can not
     * receive messages from a channel that he not is a member of, so the tab
     * will always exist.
     * @param prefix Prefixmessage, passed to distributeChannel.
     * @param message the target-channel and the actual message is in this string.
     */
    public void handlePrivForChan(String prefix, String message) {
        chanName = message.substring(message.indexOf("#"), message.indexOf(" "));
        restMessage = message.substring(message.indexOf(":")+1, message.length());
        manager.distributeChannel(prefix, chanName, restMessage, true);
    }

    /**
     * A join or part command appears when we join a new channel, or when a new
     * user joins/parts a channel we are a member of.
     * @param prefix The standard prefix-message.
     * @param message message includes chan where the join/parted action happended.
     * @param nick The nick of the user who joined/parted.
     * @param cmd Will always be PART or JOIN.
     */
    public void handleJoinAndPart(String prefix, String message, String nick, String cmd) {
        pref = prefix.substring(0, prefix.indexOf("!"));
        chanName = message.substring(message.indexOf(":")+1, message.length()-1);
        
        //If it was us that joined/parted.
        if (pref.equals(nick)){
            if (cmd.equals("JOIN")) {
                manager.checkForNewChannel(message);
            } else manager.checkToLeaveChannel(message);
        } else {
            manager.updateChannel(chanName, prefix, cmd);
        }
    }
    
    /**
     * Handling a QUIT-command issued by us or a user on a channel we
     * are a memeber of.
     * @param prefix The whole prefix-string.
     * @param message The user might typed a quit-message.
     * @param nick contains a nick, either our nick, or an other users nick.
     */
    void handleQuit(String prefix, String message, String nick) {
        pref = prefix.substring(0, prefix.indexOf("!"));
        restMessage = message.substring(message.indexOf(":")+1);
                
        if (pref.equals(nick)) { 
            manager.closeAllTabs();
        } else manager.someoneQuit(pref, restMessage);   
    }
    
    /**
     * Used when a MODE-command occurs. That is change of rights in a chan.
     * @param prefix - Standard prefix-message, passed directly to updateChannel.
     * @param message includes a channel-name the change occured, and what mode 
     * that was set, for which user.
     */
    void handleMode(String prefix, String message) {
        chanName = message.substring(message.indexOf("#"), message.indexOf(" "));
        restMessage = message.substring(message.indexOf(" ")+1, message.length());

        manager.updateChannel(chanName, prefix, restMessage);
    }

    /**
     * Finds the channelname and the result of the NAMES-command
     * (as a string), for a channel. Sends this to the setChannelNames-method.
     * @param message a message including channelname and all the users on the chan.
     */
    void handleNames(String message) {
        String temp, names;
                
        temp = message.substring(message.indexOf("#"));
        chanName = temp.substring(0, temp.indexOf(" "));
        names = message.substring(message.indexOf(":")+1, message.length()-1);
        
        manager.setChannelNames(chanName, names);
    }

    /**
     * Function that takes care of distributing messages to personal-tabs.
     * 'True' is added, since this is an incoming message.
     * @param prefix whole prefix, passed to tab-check.
     * @param message the whole message, also just passed on.
     */
    void handlePriv(String prefix, String message) {
        manager.checkPersonalTabs(prefix, message, true);
    }
}
