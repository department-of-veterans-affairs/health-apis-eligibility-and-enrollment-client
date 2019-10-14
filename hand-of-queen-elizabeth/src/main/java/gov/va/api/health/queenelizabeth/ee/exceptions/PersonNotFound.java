package gov.va.api.health.queenelizabeth.ee.exceptions;

/**
 * Exception thrown by the Queen Elizabeth Service when the getEeSummary service responds with a
 * person not found fault.
 */
public class PersonNotFound extends EligibilitiesException {
  public PersonNotFound(String message) {
    super(message);
  }
}
