package gov.va.api.health.queenelizabeth.ee.exceptions;

import javax.xml.soap.SOAPMessage;

/**
 * Exception thrown by the Queen Elizabeth Service when the getEeSummary service responds with a
 * person not found fault.
 */
public class PersonNotFound extends EligibilitiesException {
  public PersonNotFound(SOAPMessage soapMessage, String message) {
    super(soapMessage.toString() + " Reason: " + message);
  }
}
