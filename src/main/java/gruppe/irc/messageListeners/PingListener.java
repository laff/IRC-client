package gruppe.irc.messageListeners;

import gruppe.irc.MessageEvent;

/**
 * This class is used to listen for PING messages from the server and reply to them.
 * By attaching a PingListener object to an IRCConnection object all the PONG responses to PING
 * server requests gets handled transparantly. 
 */
public class PingListener implements MessageListener {
  /**
   * Method receives messages from IRCConnection object and checks if it is a PING request from the server.
   * If it is a PING request a PONG command is issued back to the server and the event is consumed (no further handling
   * of this event is needed/called for.)
   *
   * @param me a MessageEvent object containing information about the event.
   */
  public void messageReceived (MessageEvent me) {
    if (me.getCommand().toUpperCase().equals("PING")) {
      ((gruppe.irc.IRCConnection)me.getSource()).writeln ("PONG");
      me.consume ();
    }
  }
}