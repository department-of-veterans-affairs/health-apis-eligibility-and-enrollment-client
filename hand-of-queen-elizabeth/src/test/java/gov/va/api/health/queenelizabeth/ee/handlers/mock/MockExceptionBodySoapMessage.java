package gov.va.api.health.queenelizabeth.ee.handlers.mock;

import javax.xml.soap.*;

/** Mock class to forcefully throw SOAPException when calling getSOAPBody(). */
public class MockExceptionBodySoapMessage extends MockNullBodySoapMessage {

  public static final String MOCK_EXCEPTION_MESSAGE =
      "Exception occurred getting soap message body.";

  @Override
  public SOAPBody getSOAPBody() throws SOAPException {
    throw new SOAPException(MOCK_EXCEPTION_MESSAGE);
  }
}
