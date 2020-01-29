package gov.va.api.health.queenelizabeth.ee;

import static org.junit.Assert.assertEquals;

import gov.va.api.health.queenelizabeth.ee.exceptions.MissingIcnValue;
import gov.va.api.health.queenelizabeth.ee.exceptions.PersonNotFound;
import gov.va.api.health.queenelizabeth.ee.exceptions.RequestFailed;
import gov.va.api.health.queenelizabeth.ee.impl.QueenElizabethConfig;
import gov.va.api.health.queenelizabeth.ee.mock.MockEeSummarySoapServiceWsConfigurerAdapter;
import gov.va.api.health.queenelizabeth.ee.mock.endpoints.MockEeSummaryEndpoint;
import gov.va.api.health.queenelizabeth.ee.mock.endpoints.MockEeSummaryResponse;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Perform service integration test with a mock SOAP server to test nominal and fault responses. The
 * SpringBootTest is specified to instantiate an actual service for the integration test to use the
 * defined port (pulled from application.properties).
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration
@ContextConfiguration(
    classes = {
      QueenElizabethConfig.class,
      MockEeSummarySoapServiceWsConfigurerAdapter.class,
      MockEeSummaryEndpoint.class,
      MockEeSummaryResponse.class
    },
    initializers = ConfigFileApplicationContextInitializer.class)
public class QueenElizabethServiceIntegrationTest {

  @Autowired private QueenElizabethService service;

  /** Test when a blank icn is requested. */
  @Test(expected = MissingIcnValue.class)
  public void requestBlankIcn() {
    service.getEeSummary("    ");
  }

  /** Test when an empty icn is requested. */
  @Test(expected = MissingIcnValue.class)
  public void requestEmptyIcn() {
    service.getEeSummary("");
  }

  /** Test a connection refused fault condition. */
  @Test(expected = RequestFailed.class)
  public void requestFailed() {
    service.getEeSummary(MockEeSummaryResponse.FaultEnum.CONNECTION_REFUSED.getKey());
  }

  /** Test a nominal response for a known sample. */
  @Test
  public void requestNominal() {
    GetEESummaryResponse response = service.getEeSummary("getEeSummaryResponseBody");
    assertEquals(
        response
            .getSummary()
            .getCommunityCareEligibilityInfo()
            .getEligibilities()
            .getEligibility()
            .get(0)
            .getVceDescription(),
        "Mileage eligibility for the Veterans Choice Program");
  }

  /** Test nominal responses for a known list of samples. */
  @Test
  public void requestNominalList() {
    List<GetEESummaryResponse> responseList =
        service.getEeSummary(Arrays.asList("getEeSummaryResponseBody", "getEeSummaryResponseBody"));
    for (GetEESummaryResponse response : responseList) {
      assertEquals(
          response
              .getSummary()
              .getCommunityCareEligibilityInfo()
              .getEligibilities()
              .getEligibility()
              .get(0)
              .getVceDescription(),
          "Mileage eligibility for the Veterans Choice Program");
    }
  }

  /** Test when a null icn is requested. */
  @Test(expected = MissingIcnValue.class)
  public void requestNullIcn() {
    service.getEeSummary((String) null);
  }

  /** Test when an unknown icn is requested. */
  @Test(expected = PersonNotFound.class)
  public void requestPersonNotFound() {
    service.getEeSummary("0000");
  }
}
