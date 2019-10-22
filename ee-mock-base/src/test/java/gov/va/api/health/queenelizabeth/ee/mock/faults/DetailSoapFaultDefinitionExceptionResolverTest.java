package gov.va.api.health.queenelizabeth.ee.mock.faults;

import static org.junit.Assert.assertEquals;

import gov.va.api.health.queenelizabeth.ee.mock.MockEeSummarySoapServiceWsConfigurerAdapterConfig;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.soap.soap11.Soap11Fault;
import org.w3c.dom.Node;

/**
 * Test the custom SOAP Fault mapping exception resolver used to add custom fault detail element to
 * SOAPFault fault element.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {MockEeSummarySoapServiceWsConfigurerAdapterConfig.class})
@DirtiesContext
public class DetailSoapFaultDefinitionExceptionResolverTest {

  /** Client coded fault. */
  private static final ServiceFaultClientException CLIENT_EXCEPTION =
      new ServiceFaultClientException("client exception message", "client fault detail");

  /** Server coded fault. */
  private static final ServiceFaultServerException SERVER_EXCEPTION =
      new ServiceFaultServerException("server exception message", "server fault detail");

  /** Resolver. */
  @Autowired private SoapFaultMappingExceptionResolver resolver;

  /**
   * Create a message context for test.
   *
   * @return MessageContext.
   */
  @SneakyThrows
  private static MessageContext messageContext() {
    MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
    SOAPMessage message = messageFactory.createMessage();
    SoapMessageFactory factory = new SaajSoapMessageFactory(messageFactory);
    return new DefaultMessageContext(new SaajSoapMessage(message), factory);
  }

  /**
   * Get the string representation of a node.
   *
   * @param node A node.
   * @return A string.
   */
  @SneakyThrows
  private static String parseStringFromNode(Node node) {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    transformer.transform(new DOMSource(node), result);
    return writer.toString();
  }

  /** Test a thrown client exception is mapped to a CLIENT coded SOAP fault. */
  @Test
  @SneakyThrows
  public void testClientException() {
    MessageContext context = messageContext();

    resolver.resolveException(context, null, CLIENT_EXCEPTION);

    Soap11Fault fault =
        (Soap11Fault) ((SoapMessage) context.getResponse()).getSoapBody().getFault();
    assertEquals(
        SoapFaultDefinition.CLIENT.getLocalPart().toString().toLowerCase(),
        fault.getFaultCode().getLocalPart().toString().toLowerCase());

    Source detailSource = fault.getFaultDetail().getDetailEntries().next().getSource();
    assertEquals(
        CLIENT_EXCEPTION.getDetail(),
        parseStringFromNode(((DOMSource) detailSource).getNode().getLastChild()));
  }

  /** Test a thrown server exception is mapped to a SERVER coded SOAP fault. */
  @Test
  @SneakyThrows
  public void testServerException() {
    MessageContext context = messageContext();

    resolver.resolveException(context, null, SERVER_EXCEPTION);

    Soap11Fault fault =
        (Soap11Fault) ((SoapMessage) context.getResponse()).getSoapBody().getFault();
    assertEquals(
        SoapFaultDefinition.SERVER.getLocalPart().toString().toLowerCase(),
        fault.getFaultCode().getLocalPart().toString().toLowerCase());

    Source detailSource = fault.getFaultDetail().getDetailEntries().next().getSource();
    assertEquals(
        SERVER_EXCEPTION.getDetail(),
        parseStringFromNode(((DOMSource) detailSource).getNode().getLastChild()));
  }
}
