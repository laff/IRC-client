package gruppe.irc.messageListeners;

import gruppe.irc.MessageEvent;

/**
 * A very crude message listener, it takes all messages it receives and prints out
 * the prefix, actuall command and message to standard output in a tabulator separated
 * format.
 * Not to be used as anything but a debuging tool.
 */
public class GlobalMessageListener implements MessageListener {
  /**
   * Method to receive messages from the server and print them on standard output.
   *
   * @param me a MessageEvent object containing information about the message.
   */
  public void messageReceived (MessageEvent me) {
    System.out.println (me.getPrefix()+"\t"+me.getCommand()+"\t"+me.getMessage());
            
  }
}