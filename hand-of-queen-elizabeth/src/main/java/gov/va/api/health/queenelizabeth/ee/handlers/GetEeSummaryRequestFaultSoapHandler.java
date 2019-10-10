package gov.va.api.health.queenelizabeth.ee.handlers;

import gov.va.api.health.queenelizabeth.ee.exceptions.PersonNotFound;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Custom soap handler to handle faults received from the EE Summary Request soap service. */
@Slf4j
@NoArgsConstructor
public class GetEeSummaryRequestFaultSoapHandler extends BaseFaultSoapHandler {

  private static final String PERSON_NOT_FOUND_FAULT = "PERSON_NOT_FOUND";

  /**
   * Check fault for person not found, otherwise perform default fault handling.
   *
   * @param faultString Fault string.
   * @throws PersonNotFound Exception if person not found.
   */
  @Override
  protected void checkFaultString(final String faultString) throws PersonNotFound {

    if (faultString.equals(PERSON_NOT_FOUND_FAULT)) {
      throw new PersonNotFound(faultString);
    }
    super.checkFaultString(faultString);
  }
}
