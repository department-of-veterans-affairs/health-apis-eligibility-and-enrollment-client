package gov.va.api.health.queenelizabeth.ee.exceptions;

import javax.xml.soap.SOAPMessage;

/**
 * Exception thrown by the Queen Elizabeth Service when an unknown or unexpected fault or error
 * condition is encountered.
 */
public class RequestFailed extends EligibilitiesException {
  public RequestFailed(String message) {
    super(message);
  }

  public RequestFailed(SOAPMessage soapMessage, String message) {
    this(soapMessage.toString() + " Reason: " + message);
  }
}
