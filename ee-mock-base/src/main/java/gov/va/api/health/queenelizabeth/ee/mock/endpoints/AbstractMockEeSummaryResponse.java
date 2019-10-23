package gov.va.api.health.queenelizabeth.ee.mock.endpoints;

import gov.va.api.health.queenelizabeth.ee.mock.faults.ServiceFaultClientException;

/**
 * Abstract base class to Mock an EE response entity associated with a specified ICN.
 *
 * <p>If the ICN can not be found or then a PersonNotFound fault is simulated. Note that a null or
 * empty response is treated as a PersonNotFound fault.
 *
 * <p>Other faults triggered by ICN can optionally be simulated by optionally implementing checkIcn.
 */
public abstract class AbstractMockEeSummaryResponse {

  /**
   * Optional method an implementing class may trigger faults by reserved ICN values.
   *
   * @param icn ICN to trigger a fault.
   */
  protected void checkIcn(final String icn) {
    // Intentionally left blank.
  }

  /**
   * Perform an EE Summary request and return a response as a string.
   *
   * @param icn The ICN for the request.
   * @return String containing the response.
   */
  public String getEeSummaryRequest(final String icn) {
    checkIcn(icn);
    return validateResponse(obtainResponse(icn));
  }

  /**
   * Implementing class must implement this method to obtain a response associated with an ICN.
   *
   * @param icn The ICN for the request.
   * @return String containing the response.
   */
  protected abstract String obtainResponse(final String icn);

  /**
   * Ensure response indicates that person was found. This simple implementation just checks the
   * response is not null and not blank but extending class could add additional validation.
   *
   * @param response Response to check.
   * @return Response if valid.
   */
  protected String validateResponse(final String response) {
    if ((response == null) || response.isBlank()) {
      // Emulate the fault by the service if person is not found.
      throw new ServiceFaultClientException(
          "PERSON_NOT_FOUND",
          "Unable to invoke SOA ESR due to: Unable to invoke service with EntityFinder arg: "
              + "HealthAPISvcUsr:CommunityCareInfo due to exception message: "
              + "Invalid VPID and root cause stack trace: "
              + "gov.va.med.person.idmgmt.exceptions.VPIDException: "
              + "Invalid VPID checkdigit: 123456");
    }
    return response;
  }
}
