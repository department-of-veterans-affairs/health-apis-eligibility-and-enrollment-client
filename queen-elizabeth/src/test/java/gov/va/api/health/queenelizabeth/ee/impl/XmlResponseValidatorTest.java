package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.Samples;
import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import gov.va.api.health.queenelizabeth.util.XmlDocuments;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.junit.Test;
import org.w3c.dom.*;

public class XmlResponseValidatorTest {

  @Test
  public void noErrorsForGetEeSummaryFound() {
    parse(Samples.create().getEeSummaryResponse());
  }

  private void parse(String sample) {
    String sampleBody = null;
    try {
      InputStream is = new ByteArrayInputStream(sample.getBytes());
      SOAPMessage response = MessageFactory.newInstance().createMessage(null, is);
      sampleBody = XmlDocuments.getSoapBodyAsString(response);
    } catch (SOAPException | IOException e) {
      e.printStackTrace();
    }
    Document document = XmlDocuments.create().parse(sampleBody);
    XmlResponseValidator xmlResponseValidator =
        XmlResponseValidator.builder()
            .soapMessageGenerator(soapMessageGenerator())
            .response(document)
            .build();
    xmlResponseValidator.validate();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  public void requestFailedForBadXml() {
    XmlResponseValidator xmlResponseValidator =
        XmlResponseValidator.builder()
            .soapMessageGenerator(soapMessageGenerator())
            .response(null)
            .build();
    xmlResponseValidator.validate();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  public void requestFailedForEeFault() {
    parse(Samples.create().eeFault());
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
  public void successForGoodResult() {
    parse(Samples.create().getEeSummaryResponse());
  }
}
