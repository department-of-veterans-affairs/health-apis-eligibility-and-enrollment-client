package gov.va.api.health.queenelizabeth.ee.impl;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.queenelizabeth.Samples;
import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SoapRequesterTest {

  @Mock ConnectionProvider connectionProvider;

  @Mock SOAPConnection soapConnection;

  SoapRequester soapRequester;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(connectionProvider.getConnection()).thenReturn(soapConnection);
    soapRequester =
        new SoapRequester("https://ee.va.gov:9334/getEESummary/", "test-truststore.jks", "secret");
  }

  @SneakyThrows
  private SOAPMessage mockResults(String exampleXml) {
    InputStream is = new ByteArrayInputStream(exampleXml.getBytes());
    return MessageFactory.newInstance().createMessage(null, is);
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  @SneakyThrows
  public void requestFailedWhenSoapException() {
    Mockito.doThrow(SOAPException.class)
        .when(soapConnection)
        .call(Mockito.any(), Mockito.anyString());
    soapRequester.getSoapResponseBody(
        soapMessageGenerator().createGetEeSummarySoapRequest(), connectionProvider);
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
  @SneakyThrows
  public void successfulResponseShouldReturnWhenGoodIcn() {
    Mockito.when(soapConnection.call(Mockito.any(), Mockito.anyString()))
        .thenReturn(mockResults(Samples.create().getEeSummaryResponse()));
    String expected = Samples.create().getEeSummaryResponseBody();
    String result =
        soapRequester.getSoapResponseBody(
            soapMessageGenerator().createGetEeSummarySoapRequest(), connectionProvider);
    assertThat(expected).contains(result);
  }
}
