package gruppe.irc;

/**
 * Class used to pass information about a message between the IRCConnection class and its listeners.
 * Objects of this class contains information about the message and also whether or not the message 
 * has been consumed by any of the listeners it has been passed trough.
 */
public class MessageEvent {
  private String prefix, command, message;
  private boolean consumed = false;
  private Object source;

  /**
   * Constructor for creating new MessageEvent objects.
   *
   * @param prefix the prefix given for this command
   * @param command the actual command (what is this message trying to achieve)
   * @param message the message attached to this command
   * @param source a reference to the IRCConnection object sending this messageEvent
   */
  public MessageEvent (String prefix, String command, String message, Object source) {
    this.prefix = prefix;
    this.command = command;
    this.message = message;
    this.source = source;
  }

  /**
   * Method called to let this MessageEvent object consume the event.
   * Consuming and event prevents it from being passed to the others listeners in que, ie. the event has performed its function
   * and there are no need to keep passing it around.
   */
  public void consume () {
    consumed = true;
  }

  /**
   * Method to check wheter this event has been consumed or not.
   *
   * @returns boolean true if the event has been consumed, false otherwise
   */
  public boolean isConsumed () {
    return consumed;
  }

  /**
   * Method returns the prefix of the command for this event
   * 
   * @returns the prefix of the command for this event
   */
  public String getPrefix () {
    return prefix;
  }

  /**
   * Method returns the command generating this event
   * 
   * @returns the command that generated this event
   */
  public String getCommand () {
    return command;
  }

  /**
   * Method returns the string attached to the command generating this event
   * 
   * @returns the string attached to the command generating this event
   */
  public String getMessage () {
    return message;
  }

  /**
   * Method returns the source object for this event
   *
   * @returns the object which generated this event
   */
  public Object getSource () {
    return source;
  }

  /**
   * Method returns a simplified string representation of this event
   *
   * @returns a simplified string representing this event
   */
  public String toString () {
    return prefix+"*"+command+"*"+message;
  }
}