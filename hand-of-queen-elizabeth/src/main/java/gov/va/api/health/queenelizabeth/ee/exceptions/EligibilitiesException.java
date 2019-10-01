package gov.va.api.health.queenelizabeth.ee.exceptions;

import javax.xml.ws.ProtocolException;

/** Base exception class for any custom exceptions thrown by the Queen Elizabeth Service. */
class EligibilitiesException extends ProtocolException {
  public EligibilitiesException(String message) {
    super(message);
  }
}
