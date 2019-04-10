package gov.va.api.health.queenelizabeth.controller;

import gov.va.api.health.queenelizabeth.Samples;
import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class QueenElizabethV1ApiControllerTest {

  @Mock Eligibilities eligibilities;

  private QueenElizabethV1ApiController controller;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    controller = new QueenElizabethV1ApiController(eligibilities);
  }

  @Test
  public void searchesAreSentToEe() {
    Mockito.when(eligibilities.request(Mockito.any()))
        .thenReturn(Samples.create().getEeSummaryResponse());
    controller.requestEligibility(
        "1010101010V666666", "eeTestUsername", "eeTestPassword", "eeTestRequestName");
    Mockito.verify(eligibilities).request(soapMessageGenerator());
  }

  private SoapMessageGenerator soapMessageGenerator() {
    return SoapMessageGenerator.builder()
        .eeUsername("eeTestUsername")
        .eePassword("eeTestPassword")
        .eeRequestName("eeTestRequestName")
        .id("1010101010V666666")
        .build();
  }
}
