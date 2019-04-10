package gov.va.api.health.queenelizabeth.ee.impl;

import static gov.va.api.health.queenelizabeth.util.XmlDocuments.getSoapBodyAsString;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.EligibilityInfo;
import java.net.URL;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SoapRequester implements EligibilityInfo {

  private final String endpointUrl;

  private final String eeTruststorePath;

  private final String eeTruststorePassword;

  /** Constructor. */
  @Autowired
  @SneakyThrows
  public SoapRequester(
      @Value("${ee.endpoint.url}") String endpointUrl,
      @Value("${ee.truststore.path}") String eeTruststorePath,
      @Value("${ee.truststore.password}") String eeTruststorePassword) {
    this.endpointUrl = endpointUrl;
    this.eeTruststorePath = eeTruststorePath;
    this.eeTruststorePassword = eeTruststorePassword;
  }

  @Override
  @SneakyThrows
  public String executeSoapCall(SOAPMessage soapRequestMessage) {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(new URL(endpointUrl), eeTruststorePath, eeTruststorePassword);
    String response = getSoapResponseBody(soapRequestMessage, connectionProvider);
    connectionProvider.disconnect();
    return response;
  }

  /** Sends the SOAP Request and retreives the Body of the SOAP Response. */
  public String getSoapResponseBody(
      SOAPMessage soapRequestMessage, ConnectionProvider connectionProvider) {
    try {
      /* Lets get us a SOAP Response. */
      SOAPMessage soapResponse =
          connectionProvider.getConnection().call(soapRequestMessage, endpointUrl);
      return getSoapBodyAsString(soapResponse);
    } catch (SOAPException e) {
      throw new Eligibilities.RequestFailed(soapRequestMessage, "Failed to send/receive from EE");
    }
  }
}
