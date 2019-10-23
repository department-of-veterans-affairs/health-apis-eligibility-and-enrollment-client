package gov.va.api.health.queenelizabeth.ee.mock.endpoints.mock;

import gov.va.api.health.queenelizabeth.ee.mock.endpoints.AbstractMockEeSummaryResponse;
import org.springframework.stereotype.Component;

/**
 * Simplistic MockEeSummaryResponse to mock possible request/response scenarios to drive unit tests.
 */
@Component
public class MockEeSummaryResponseImpl extends AbstractMockEeSummaryResponse {

  /** Fake ICN for valid ICN scenarios. */
  public static final String VALID_ICN = "valid icn";

  /** Fake ICN for invalid ICN scenarios. */
  public static final String INVALID_ICN = "invalid icn";

  /** Fake ICN that doubles as a blank response. */
  public static final String BLANK_RESPONSE_ICN = "    ";

  /** Fake version value used to validate a minimal valid response. */
  public static final String VALID_EES_VERSION = "valid ees version";

  /** Fake getEESummaryResponse payload to test minimal unmarshallable response. */
  public static final String VALID_RESPONSE =
      "<getEESummaryResponse xmlns=\"http://jaxws.webservices.esr.med.va.gov/schemas\"><eesVersion>"
          + VALID_EES_VERSION
          + "</eesVersion></getEESummaryResponse>";

  @Override
  protected String obtainResponse(final String icn) {
    if (icn.equals(VALID_ICN)) {
      // Return a valid response if a valid ICN.
      return VALID_RESPONSE;
    } else if (icn.equals(INVALID_ICN)) {
      // An invalid ICN will result in a person not found fault.
      return null;
    }
    // Return the requested ICN as a response to forcefully test bad responses.
    return icn;
  }
}
