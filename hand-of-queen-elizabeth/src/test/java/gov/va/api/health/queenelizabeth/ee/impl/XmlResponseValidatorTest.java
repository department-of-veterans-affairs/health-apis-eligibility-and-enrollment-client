package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.Samples;
import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import gov.va.api.health.queenelizabeth.util.XmlDocuments;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.namespace.NamespaceContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.validation.Schema;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class XmlResponseValidatorTest {

  private NamespaceContext namespaceContext;

  private Schema schema;

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
    Document document = XmlDocuments.parse(sampleBody, schema);
    XmlResponseValidator xmlResponseValidator =
        XmlResponseValidator.builder()
            .soapMessageGenerator(soapMessageGenerator())
            .response(document)
            .namespaceContext(namespaceContext)
            .build();
    xmlResponseValidator.validate();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  public void requestFailedForBadXml() {
    XmlResponseValidator xmlResponseValidator =
        XmlResponseValidator.builder()
            .soapMessageGenerator(soapMessageGenerator())
            .response(null)
            .namespaceContext(namespaceContext)
            .build();
    xmlResponseValidator.validate();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  public void requestFailedForEeFault() {
    parse(Samples.create().eeFault());
  }

  @Test(expected = Eligibilities.PersonNotFound.class)
  public void requestPersonNotFound() {
    parse(Samples.create().personNotFound());
  }

  @Before
  public void setUp() {
    final Path resource = Paths.get("..", "ee-artifacts", "src", "main", "wsdl", "eeSummary.wsdl");
    schema = XmlDocuments.getSchemaFromWsdl(resource.toFile().getAbsoluteFile());
    namespaceContext =
        new EeNamespaceContext(
            XmlDocuments.getTargetNamespaceSchemaFromWsdl(resource.toFile().getAbsoluteFile()));
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
