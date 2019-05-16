package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.Samples;
import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.EligibilityInfo;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import javax.xml.soap.SOAPMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ValidateEligibilitiesTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Mock EligibilityInfo eligibilityInfo;

  private ValidateEligibilities validateEligibilities;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    validateEligibilities = new ValidateEligibilities(eligibilityInfo);
  }

  @Test(expected = Eligibilities.MissingIcnValue.class)
  public void emptyIdShouldReturnMissingIcnValueException() {
    validateEligibilities.request(
        SoapMessageGenerator.builder()
            .eeUsername("eeTestUsername")
            .eePassword("eeTestPassword")
            .eeRequestName("eeTestRequestName")
            .id("")
            .build());
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  public void requestFailedForFailedParse() {
    Mockito.doReturn("<not-valid-xml>")
        .when(eligibilityInfo)
        .executeSoapCall(Mockito.any(SOAPMessage.class));
    validateEligibilities.request(soapMessageGenerator());
  }

  private SoapMessageGenerator soapMessageGenerator() {
    return SoapMessageGenerator.builder()
        .eeUsername("eeTestUsername")
        .eePassword("eeTestPassword")
        .eeRequestName("eeTestRequestName")
        .id("1010101010V666666")
        .build();
  }

  @Test
  public void validIcnShouldParseAndWriteDocSuccessfully() {
    String eeValidResponse = Samples.create().getEeSummaryResponseBody();
    Mockito.doReturn(eeValidResponse)
        .when(eligibilityInfo)
        .executeSoapCall(Mockito.any(SOAPMessage.class));
    validateEligibilities.request(soapMessageGenerator());
  }
}
