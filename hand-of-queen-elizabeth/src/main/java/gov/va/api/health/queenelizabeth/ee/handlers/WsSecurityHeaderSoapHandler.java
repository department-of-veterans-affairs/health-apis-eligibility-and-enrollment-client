package gov.va.api.health.queenelizabeth.ee.handlers;

import gov.va.api.health.queenelizabeth.ee.exceptions.RequestFailed;
import gov.va.api.health.queenelizabeth.ee.impl.WsSecurityHeaderConfig;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.RequiredArgsConstructor;

/**
 * Custom soap handler used by Queen Elizabeth Service to optionally add WS Security Headers to soap
 * request messages.
 */
@RequiredArgsConstructor
public class WsSecurityHeaderSoapHandler extends BaseQueenElizabethSoapHandler {
  private final WsSecurityHeaderConfig config;

  /**
   * Add a WS Security Header as configured to the message.
   *
   * @param context Soap Message Context containing the soap envelope to manipulate.
   * @return Boolean indicating if processing should continue.
   */
  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    if (config.applyHeader()) {
      try {
        SOAPEnvelope env = context.getMessage().getSOAPPart().getEnvelope();

        env.addNamespaceDeclaration(config.getNamespace(), config.getSchema());

        SOAPElement usernameToken =
            env.getHeader()
                .addChildElement("Security", config.getNamespace())
                .addChildElement("UsernameToken", config.getNamespace());

        usernameToken
            .addChildElement("Username", config.getNamespace())
            .addTextNode(config.getUsername());

        usernameToken
            .addChildElement("Password", config.getNamespace())
            .addTextNode(config.getPassword());
      } catch (SOAPException e) {
        // If anything fails adding the security header we should fail the request.
        throw new RequestFailed(e.getMessage());
      }
    }

    return true;
  }
}
