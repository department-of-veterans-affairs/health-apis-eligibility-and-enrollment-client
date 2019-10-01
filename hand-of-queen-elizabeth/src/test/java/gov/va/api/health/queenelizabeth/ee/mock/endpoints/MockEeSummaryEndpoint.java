package gov.va.api.health.queenelizabeth.ee.mock.endpoints;

import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryRequest;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryResponse;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Mock eeSummary.getEESummaryRequest SOAP Service endpoint that uses an MockEeSummaryResponse to
 * get soap message xml strings to unmarshall into an GetEESummaryResponse.
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Endpoint
public class MockEeSummaryEndpoint {

  private static final String NAMESPACE_URI = "http://jaxws.webservices.esr.med.va.gov/schemas";

  private final MockEeSummaryResponse eeResponseEntityMock;

  /**
   * Simulate a SOAP Service getEeSummaryRequest endpoint.
   *
   * @param request GetEESummaryRequest.
   * @return GetEESummaryResponse
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEESummaryRequest")
  @ResponsePayload
  @SneakyThrows
  public JAXBElement<GetEESummaryResponse> getEeSummaryRequest(
      @RequestPayload JAXBElement<GetEESummaryRequest> request) {
    return JAXBContext.newInstance(GetEESummaryResponse.class)
        .createUnmarshaller()
        .unmarshal(
            new StreamSource(
                new StringReader(
                    eeResponseEntityMock.getEeSummaryRequest(request.getValue().getKey()))),
            GetEESummaryResponse.class);
  }
}
