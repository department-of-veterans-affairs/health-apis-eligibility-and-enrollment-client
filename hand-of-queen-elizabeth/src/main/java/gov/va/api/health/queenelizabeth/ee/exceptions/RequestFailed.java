package gov.va.api.health.queenelizabeth.ee.exceptions;

/**
 * Exception thrown by the Queen Elizabeth Service when an unknown or unexpected fault or error
 * condition is encountered.
 */
public class RequestFailed extends EligibilitiesException {
  public RequestFailed(String message) {
    super(message);
  }
}
