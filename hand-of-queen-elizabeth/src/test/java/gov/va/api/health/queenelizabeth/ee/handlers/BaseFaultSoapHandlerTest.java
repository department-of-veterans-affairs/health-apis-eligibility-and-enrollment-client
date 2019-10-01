package gov.va.api.health.queenelizabeth.ee.handlers;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Test the BaseFaultSoapHandler. Most of these cases are for unlikely off-nominal conditions. */
public class BaseFaultSoapHandlerTest {

  @Rule public ExpectedException expectedEx = ExpectedException.none();

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
    expectedEx.expect(RequestFailed.class);
    expectedEx.expectMessage(MockExceptionBodySoapMessage.MOCK_EXCEPTION_MESSAGE);
    handler.parseFault(new MockExceptionBodySoapMessage());
  }

  /** Test exceptional case where fault detected but message body was null. */
  @Test
  public void testBodyNull() {
    expectedEx.expect(RequestFailed.class);
    expectedEx.expectMessage(BaseFaultSoapHandler.FAULT_NULL_MESSAGE);
    handler.parseFault(new MockNullBodySoapMessage());
  }

  /** Test exceptional case where fault detected but fault was null. */
  @Test
  public void testFaultNull() {
    expectedEx.expect(RequestFailed.class);
    expectedEx.expectMessage(BaseFaultSoapHandler.FAULT_NULL_MESSAGE);
    final SOAPMessage soapMessage =
        parse(Paths.get("src", "test", "resources", "samples", "eeFaultNoFault.xml"));
    soapMessage.getSOAPPart().setNodeValue(null);
    handler.parseFault(soapMessage);
  }

  /**
   * Test exceptional case where fault detected but fault string was blank.
   *
   * <p>NOTE: the test file must have extension 'jks' as a silly workaround to prevent the xml
   * formatter from clobbering the blank fault string.
   */
  @Test
  public void testFaultStringBlank() {
    expectedEx.expect(RequestFailed.class);
    expectedEx.expectMessage(BaseFaultSoapHandler.FAULT_UNKNOWN_MESSAGE);
    handler.handleFault(
        new MockSoapMessageContext(
            parse(Paths.get("src", "test", "resources", "samples", "eeFaultBlankFault.jks"))));
  }

  /** Test exceptional case where fault detected but fault string was null. */
  @Test
  public void testFaultStringNull() {
    expectedEx.expect(RequestFailed.class);
    expectedEx.expectMessage(BaseFaultSoapHandler.FAULT_STRING_NULL_MESSAGE);
    handler.handleFault(
        new MockSoapMessageContext(
            parse(Paths.get("src", "test", "resources", "samples", "eeFaultNullFault.xml"))));
  }
}
