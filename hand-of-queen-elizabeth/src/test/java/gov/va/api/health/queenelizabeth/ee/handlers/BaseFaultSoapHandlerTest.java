package gov.va.api.health.queenelizabeth.ee.handlers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.health.queenelizabeth.ee.exceptions.RequestFailed;
import gov.va.api.health.queenelizabeth.ee.handlers.mock.MockExceptionBodySoapMessage;
import gov.va.api.health.queenelizabeth.ee.handlers.mock.MockNullBodySoapMessage;
import gov.va.api.health.queenelizabeth.ee.handlers.mock.MockSoapMessageContext;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.soap.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/** Test the BaseFaultSoapHandler. Most of these cases are for unlikely off-nominal conditions. */
public class BaseFaultSoapHandlerTest {

  private BaseFaultSoapHandler handler = new BaseFaultSoapHandler();

  /**
   * Parse a resource path file contents into a soap message.
   *
   * @param path Path of resource file to parse into a soap message.
   * @return SOAPMessage.
   */
  @SneakyThrows
  private SOAPMessage parse(final Path path) {
    return MessageFactory.newInstance()
        .createMessage(
            null, new ByteArrayInputStream(new String(Files.readAllBytes(path)).getBytes()));
  }

  /** Test exceptional case when a message body can not be obtained. */
  @Test
  public void testBodyException() {
    assertThrows(RequestFailed.class, () -> handler.parseFault(new MockExceptionBodySoapMessage()));
  }

  /** Test exceptional case where fault detected but message body was null. */
  @Test
  public void testBodyNull() {
    assertThrows(RequestFailed.class, () -> handler.parseFault(new MockNullBodySoapMessage()));
  }

  /** Test exceptional case where fault detected but fault was null. */
  @Test
  public void testFaultNull() {
    final SOAPMessage soapMessage =
        parse(Paths.get("src", "test", "resources", "samples", "eeFaultNoFault.xml"));
    soapMessage.getSOAPPart().setNodeValue(null);

    assertThrows(RequestFailed.class, () -> handler.parseFault(soapMessage));
  }

  /**
   * Test exceptional case where fault detected but fault string was blank.
   *
   * <p>NOTE: the test file must have extension 'jks' as a silly workaround to prevent the xml
   * formatter from clobbering the blank fault string.
   */
  @Test
  public void testFaultStringBlank() {
    assertThrows(
        RequestFailed.class,
        () ->
            handler.handleFault(
                new MockSoapMessageContext(
                    parse(
                        Paths.get(
                            "src", "test", "resources", "samples", "eeFaultBlankFault.jks")))));
  }

  /** Test exceptional case where fault detected but fault string was null. */
  @Test
  public void testFaultStringNull() {
    assertThrows(
        RequestFailed.class,
        () ->
            handler.handleFault(
                new MockSoapMessageContext(
                    parse(
                        Paths.get(
                            "src", "test", "resources", "samples", "eeFaultNullFault.xml")))));
  }
}
