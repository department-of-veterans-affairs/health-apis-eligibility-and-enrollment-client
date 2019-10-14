package gov.va.api.health.queenelizabeth.ee.exceptions;

/**
 * Exception thrown by the Queen Elizabeth Service when the getEeSummary service is called with a
 * null or blank ICN.
 */
public class MissingIcnValue extends EligibilitiesException {
  public MissingIcnValue(String message) {
    super(message);
  }
}
