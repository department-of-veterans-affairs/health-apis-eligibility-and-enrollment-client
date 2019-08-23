package gov.va.api.health.queenelizabeth.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.va.api.health.queenelizabeth.util.XmlDocuments.ParseFailed;
import gov.va.api.health.queenelizabeth.util.XmlDocuments.WriteFailed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import lombok.SneakyThrows;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

public class XmlDocumentsTest {

  public static Schema makeSchema(File wsdlFile)
      throws ParserConfigurationException, IOException, SAXException, InstantiationException,
          IllegalAccessException, ClassNotFoundException {
    // read wsdl document
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setNamespaceAware(true);
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document wsdlDoc = dBuilder.parse(wsdlFile);
    // read namespace declarations from wsdl document, in case they are referred from a schema
    NamedNodeMap attributes = wsdlDoc.getDocumentElement().getAttributes();
    Map<String, String> namespacesFromWsdlDocument = new HashMap<>();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node n = attributes.item(i);
      if (n.getNamespaceURI() != null
          && n.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) {
        namespacesFromWsdlDocument.put(n.getLocalName(), n.getNodeValue());
      }
    }
    // read the schema nodes from the wsdl
    NodeList schemas = wsdlDoc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
    Map<String, DOMSource> sources = new HashMap<>();
    for (int i = 0; i < schemas.getLength(); i++) {
      // create a document for each schema and copy the source schema
      Document schema = dBuilder.newDocument();
      Element schemaElement = (Element) schema.importNode(schemas.item(i), true);
      // add all non-existing namespace declarations from the wsdl node
      String targetNs = schemaElement.getAttribute("targetNamespace");
      for (Map.Entry<String, String> ns : namespacesFromWsdlDocument.entrySet()) {
        String name = ns.getKey();
        String value = ns.getValue();
        if (schemaElement.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", name) == null) {
          schemaElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + name, value);
        }
      }
      // map schemas by their target namespace
      schema.appendChild(schemaElement);
      DOMSource domSource = new DOMSource(schema);
      sources.put(targetNs, domSource);
    }
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    // Create a ResourceResolver that can find the correct schema from the map
    DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
    final DOMImplementationLS domImplementationLS =
        (DOMImplementationLS) registry.getDOMImplementation("LS");
    factory.setResourceResolver(
        new LSResourceResolver() {

          @Override
          public LSInput resolveResource(
              String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            Source xmlSource = sources.get(namespaceURI);
            if (xmlSource != null) {
              LSInput input = domImplementationLS.createLSInput();
              ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              Result outputTarget = new StreamResult(outputStream);
              try {
                TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(xmlSource, outputTarget);
              } catch (TransformerException e) {
                e.printStackTrace();
              }
              InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
              input.setByteStream(is);
              input.setSystemId(systemId);
              return input;
            } else {
              return null;
            }
          }
        });
    // create the schema object from the sources
    return factory.newSchema(sources.values().toArray(new DOMSource[] {}));
  }

  @Test(expected = ParseFailed.class)
  public void badXmlCausesParseFailure() {
    String xml = "<a><b>bee</b><c><no-closing-tag></c></a>";
    XmlDocuments.parse(xml);
  }

  @Test
  public void goodXmlPassesValidates() throws Exception {
    String responseXmlString =
        StreamUtils.copyToString(
            new ClassPathResource("testResponse.xml").getInputStream(), Charset.defaultCharset());
    File f =
        new File(this.getClass().getClassLoader().getResource("testDescription.wsdl").getFile());
    this.getClass().getClassLoader().getResource("testDescription.wsdl");
    System.out.println("f: " + f.getAbsolutePath());
    Schema schema = makeSchema(f);
    XmlDocuments.parse(responseXmlString, schema);
  }

  @Test
  public void roundTrip() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>bee</b><c><hmm>sees</hmm><hmm>seas</hmm></c></a>";
    Document document = XmlDocuments.parse(xml);
    String roundTrip = XmlDocuments.write(document);
    assertThat(roundTrip).isEqualToIgnoringWhitespace(xml);
  }

  @Test(expected = WriteFailed.class)
  public void writeFailedExceptionIsThrownWhenNoDomImplementationIsFound() {
    DOMImplementationRegistry mock = Mockito.mock(DOMImplementationRegistry.class);
    when(mock.getDOMImplementation(Mockito.any())).thenReturn(null);
    XmlDocuments.findLsDomImplementationOrDie(mock);
  }

  @Test(expected = WriteFailed.class)
  public void writeFailedExceptionIsThrownWhenNoLsDomImplementationIsFound() {
    DOMImplementationRegistry mock = Mockito.mock(DOMImplementationRegistry.class);
    DOMImplementation mockDom = Mockito.mock(DOMImplementation.class);
    when(mock.getDOMImplementation(Mockito.any())).thenReturn(mockDom);
    XmlDocuments.findLsDomImplementationOrDie(mock);
  }

  @Test(expected = WriteFailed.class)
  @SneakyThrows
  public void writeFailedWhenSoapBodyCantBeFound() {
    SOAPMessage soapMessage = Mockito.mock(SOAPMessage.class);
    Mockito.when(soapMessage.getSOAPBody()).thenThrow(SOAPException.class);
    XmlDocuments.getSoapBodyAsString(soapMessage);
  }
}
