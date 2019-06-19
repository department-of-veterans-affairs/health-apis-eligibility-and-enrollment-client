package gov.va.api.health.queenelizabeth.ee.impl;

import static gov.va.api.health.queenelizabeth.util.XmlDocuments.getSoapBodyAsString;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.EligibilityInfo;
import java.net.URL;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class SoapRequester implements EligibilityInfo {
  private final String endpointUrl;

  private final String eeTruststorePath;

  private final String eeTruststorePassword;

  @Override
  @SneakyThrows
  public String executeSoapCall(SOAPMessage soapRequestMessage) {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(new URL(endpointUrl), eeTruststorePath, eeTruststorePassword);
    String response = getSoapResponseBody(soapRequestMessage, connectionProvider);
    connectionProvider.disconnect();
    return response;
  }

  /** Sends the SOAP Request and retrieves the Body of the SOAP Response. */
  public String getSoapResponseBody(
      SOAPMessage soapRequestMessage, ConnectionProvider connectionProvider) {
    try {
      /* Lets get us a SOAP Response. */
      SOAPMessage soapResponse =
          connectionProvider.getConnection().call(soapRequestMessage, endpointUrl);
      String body = getSoapBodyAsString(soapResponse);
      if (body.contains("java.lang.ClassNotFoundException")) {
        throw new Eligibilities.RequestFailed("Failed to send/receive from EE");
      } else {
        return body;
      }
    } catch (SOAPException e) {
      throw new Eligibilities.RequestFailed(soapRequestMessage, "Failed to send/receive from EE");
    }
  }
}
