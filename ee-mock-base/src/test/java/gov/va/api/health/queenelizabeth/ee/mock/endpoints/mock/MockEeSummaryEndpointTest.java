package gov.va.api.health.queenelizabeth.ee.mock.endpoints.mock;

import static org.junit.Assert.assertEquals;

import gov.va.api.health.queenelizabeth.ee.mock.MockEeSummarySoapServiceConfig;
import gov.va.api.health.queenelizabeth.ee.mock.endpoints.MockEeSummaryEndpoint;
import gov.va.api.health.queenelizabeth.ee.mock.faults.ServiceFaultClientException;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryRequest;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryResponse;
import gov.va.med.esr.webservices.jaxws.schemas.ObjectFactory;
import javax.xml.bind.JAXBElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/** Use a MockEeSummaryResponseImpl to test various scenarios of MockEeSummaryEndpoint. */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@ContextConfiguration(
  classes = {MockEeSummarySoapServiceConfig.class, MockEeSummaryResponseImpl.class},
  initializers = ConfigFileApplicationContextInitializer.class
)
@DirtiesContext
public class MockEeSummaryEndpointTest {

  @Autowired private MockEeSummaryEndpoint endpoint;

  /** Test a blank response using endpoint. */
  @Test(expected = ServiceFaultClientException.class)
  public void testBlankResponseUsingEndpoint() {
    JAXBElement<GetEESummaryRequest> request =
        new ObjectFactory()
            .createGetEESummaryRequest(
                GetEESummaryRequest.builder()
                    .key(MockEeSummaryResponseImpl.BLANK_RESPONSE_ICN)
                    .build());
    endpoint.getEeSummaryRequest(request).getValue();
  }

  /** Test an invalid icn using endpoint. */
  @Test(expected = ServiceFaultClientException.class)
  public void testInvalidIcnUsingEndpoint() {
    JAXBElement<GetEESummaryRequest> request =
        new ObjectFactory()
            .createGetEESummaryRequest(
                GetEESummaryRequest.builder().key(MockEeSummaryResponseImpl.INVALID_ICN).build());
    endpoint.getEeSummaryRequest(request).getValue();
  }

  /** Test a valid icn and a valid response using endpoint. */
  @Test
  public void testValidIcnAndValidResponseUsingEndpoint() {
    JAXBElement<GetEESummaryRequest> request =
        new ObjectFactory()
            .createGetEESummaryRequest(
                GetEESummaryRequest.builder().key(MockEeSummaryResponseImpl.VALID_ICN).build());
    GetEESummaryResponse response = endpoint.getEeSummaryRequest(request).getValue();
    assertEquals(MockEeSummaryResponseImpl.VALID_EES_VERSION, response.getEesVersion());
  }
}
