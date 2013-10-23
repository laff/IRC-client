package no.hig.okolloen.irc.messageListeners;

import no.hig.okolloen.irc.MessageEvent;

/**
 * This interface must be implemented by all message listeners to be attached to an IRCConnection object.
 * The method defined by this interface is the method that will be called to notify the listener that 
 * a new message is comming from the IRCConnection object.
 */
public interface MessageListener {
  /**
   * Method to be called to notify the listener of a new message from the IRCConnection object.
   *
   * @param me a MessageEvent object containing information about the message.
   */
  public void messageReceived (MessageEvent me);
}