package gov.va.api.health.queenelizabeth.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** Utilities for working with XML documents. */
@Slf4j
public final class XmlDocuments {

  private static DOMImplementationRegistry createRegistryOrDie() {
    DOMImplementationRegistry registry;
    try {
      registry = DOMImplementationRegistry.newInstance();
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      throw new WriteFailed(e);
    }
    return registry;
  }

  /**
   * Finds a DOM implementation is capable of 'Load/Save' operations, which gives us the ability to
   * write Documents.
   */
  public static DOMImplementationLS findLsDomImplementationOrDie(DOMImplementationRegistry registry)
      throws WriteFailed {
    DOMImplementation domImplementation = registry.getDOMImplementation("LS");
    if (domImplementation == null) {
      throw new WriteFailed("No Load/Save LS DOM implementation available.");
    }
    if (domImplementation instanceof DOMImplementationLS) {
      return (DOMImplementationLS) domImplementation;
    }
    throw new WriteFailed(
        "Unexpected LS DOM implementation. Required: org.w3c.dom.ls.DOMImplementationLS, Got: "
            + domImplementation.getClass());
  }

  /**
   * Get a document builder factory.
   *
   * @return DocumentBuilderFactory.
   * @throws ParseFailed Exception if failed to obtain factory.
   */
  public static DocumentBuilderFactory getDocumentBuilderFactory() throws ParseFailed {
    try {
      // TODO: setting this does not appear necessary after correcting validation but I would
      // like to see an actual request before deleting.
      // System.setProperty(
      // "javax.xml.soap.SAAJMetaFactory",
      // "com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      return factory;
    } catch (ParserConfigurationException e) {
      throw new ParseFailed(e);
    }
  }

  /**
   * Get the primary schema from Wsdl. NOTE: this is a simplistic implementation that only obtains
   * the first schema within a wsdl.
   *
   * @param file The wsdl file located on the classpath.
   * @return The first schema found within the wsdl.
   * @throws ParseFailed Exception if an error during parsing.
   */
  public static Schema getSchemaFromWsdl(final File file) throws ParseFailed {
    try {
      final SchemaFactory schemaFactory =
          SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      DOMSource domSource = new DOMSource(getSchemaNodeFromWsdl(file));
      return schemaFactory.newSchema(domSource);
    } catch (SAXException e) {
      throw new ParseFailed(e);
    }
  }

  /**
   * Get the primary schema from Wsdl. NOTE: this is a simplistic implementation that only obtains
   * the first schema within a wsdl.
   *
   * @param file The wsdl file located on the classpath.
   * @return The first schema node found within the wsdl.
   * @throws ParseFailed Exception if an error during parsing.
   */
  public static org.w3c.dom.Node getSchemaNodeFromWsdl(final File file) throws ParseFailed {
    try {
      DocumentBuilderFactory factory = getDocumentBuilderFactory();
      factory.setNamespaceAware(true);
      final String xml = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      final Document wsdlDoc = parse(factory, xml);
      final NodeList schemas =
          wsdlDoc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "schema");
      // NOTE: this simplisitic implementation assumes only one schema in the wsdl.
      if (schemas.getLength() != 1) {
        throw new ParseFailed("Expected a single schema within the given wsdl.");
      }
      return schemas.item(0);
    } catch (IOException e) {
      throw new ParseFailed(e);
    }
  }

  /** Get the body of a SOAP Message as a string value. */
  public static String getSoapBodyAsString(SOAPMessage soapMessage) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
      transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      SOAPBody soapBody = soapMessage.getSOAPBody();
      Node element = soapBody.getChildElements().next();
      StringWriter stringResult = new StringWriter();
      transformerFactory
          .newTransformer()
          .transform(new DOMSource(element), new StreamResult(stringResult));
      return stringResult.toString();
    } catch (SOAPException | TransformerException e) {
      throw new WriteFailed(e);
    }
  }

  /**
   * Get the primary schema target namespace from Wsdl. NOTE: this is a simplistic implementation
   * that only obtains the first schema within a wsdl.
   *
   * @param file The wsdl file located on the classpath.
   * @return The target namespace of the first schema found in the wsdl.
   * @throws ParseFailed Exception if an error during parsing.
   */
  public static String getTargetNamespaceSchemaFromWsdl(final File file) throws ParseFailed {
    return ((Element) getSchemaNodeFromWsdl(file)).getAttribute("targetNamespace");
  }

  /**
   * Parse the given XML into a Document model. A ParseFailed exception can be thrown if the
   * document cannot be read for any reason. This is deprecated because it does not do validation.
   */
  @Deprecated
  public static Document parse(String xml) throws ParseFailed {
    return parse(getDocumentBuilderFactory(), xml);
  }

  /**
   * Parse the given XML into a Document Model. Validate the document against a given schema. A
   * ParseFailed exception can be thrown if the document cannot be read for any reason.
   */
  public static Document parse(String xml, Schema schema) throws ParseFailed {
    DocumentBuilderFactory factory = getDocumentBuilderFactory();
    if (schema != null) {
      factory.setNamespaceAware(true);
      factory.setSchema(schema);
    } else {
      log.warn("Schema is null so not validating.");
    }
    return parse(factory, xml);
  }

  private static Document parse(DocumentBuilderFactory factory, String xml) throws ParseFailed {
    try {
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputSource is = new InputSource(new StringReader(xml));
      return builder.parse(is);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new ParseFailed(e);
    }
  }

  /**
   * Write the given Document as an indented XML string. A WriteFailed exception will be thrown if
   * the document cannot be written for some reason.
   */
  public static String write(Document document) {
    DOMImplementationRegistry registry = createRegistryOrDie();
    DOMImplementationLS domImplementation = findLsDomImplementationOrDie(registry);
    Writer stringWriter = new StringWriter();
    LSOutput formattedOutput = domImplementation.createLSOutput();
    formattedOutput.setCharacterStream(stringWriter);
    LSSerializer domSerializer = domImplementation.createLSSerializer();
    domSerializer.getDomConfig().setParameter("format-pretty-print", true);
    domSerializer.getDomConfig().setParameter("xml-declaration", true);
    domSerializer.write(document, formattedOutput);
    return stringWriter.toString();
  }

  public static class ParseFailed extends RuntimeException {

    private static final long serialVersionUID = 1L;

    ParseFailed(String message) {
      super(message);
    }

    ParseFailed(Exception cause) {
      super(cause);
    }
  }

  public static class WriteFailed extends RuntimeException {

    private static final long serialVersionUID = 1L;

    WriteFailed(String message) {
      super(message);
    }

    WriteFailed(Exception cause) {
      super(cause);
    }
  }
}
