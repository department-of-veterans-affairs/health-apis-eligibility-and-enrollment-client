package gov.va.api.health.queenelizabeth.ee.handlers;

import java.util.Collections;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Base custom soap handler that implements default values. This is used as a base class for custom
 * handlers to eliminate the need for them to implement default functionality.
 */
@Slf4j
@NoArgsConstructor
public class BaseQueenElizabethSoapHandler implements SOAPHandler<SOAPMessageContext> {

  @Override
  public void close(MessageContext context) {}

  @Override
  public Set<QName> getHeaders() {
    return Collections.emptySet();
  }

  @Override
  public boolean handleFault(SOAPMessageContext context) {
    return true;
  }

  @Override
  public boolean handleMessage(SOAPMessageContext context) throws RuntimeException {
    return true;
  }
}
