package gov.va.api.health.queenelizabeth.ee.handlers;

import gov.va.api.health.queenelizabeth.ee.exceptions.RequestFailed;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Base soap handler to facilitate throwing RequestFailed exceptions for fault conditions. This is
 * used as a base class for custom handlers to eliminate the need for them to implement default
 * functionality for parsing faults.
 */
@Slf4j
@NoArgsConstructor
public class BaseFaultSoapHandler extends BaseQueenElizabethSoapHandler {

  public static final String FAULT_UNKNOWN_MESSAGE = "Unknown fault occurred.";
  public static final String FAULT_NULL_MESSAGE = "Fault received but is null.";
  public static final String FAULT_STRING_NULL_MESSAGE = "Fault received but string is null.";

  /**
   * Check the SOAP Message fault string and throw exception.
   *
   * @param faultString Fault string.
   * @throws RequestFailed Exception if fault string not empty.
   */
  protected void checkFaultString(final String faultString) throws RequestFailed {
    if (!faultString.isBlank()) {
      throw new RequestFailed(faultString);
    }
  }

  /**
   * Handle any fault that might be encountered.
   *
   * @param context SOAP Message context.
   * @return Boolean, however, this method will throw an exception.
   * @throws RequestFailed Exception for fault.
   */
  @Override
  public boolean handleFault(SOAPMessageContext context) throws RequestFailed {
    SOAPMessage message = context.getMessage();
    SOAPFault fault = parseFault(message);
    final String faultString = fault.getFaultString();
    if (faultString == null) {
      throw new RequestFailed(FAULT_STRING_NULL_MESSAGE);
    }
    checkFaultString(faultString);
    throw new RequestFailed(FAULT_UNKNOWN_MESSAGE);
  }

  /**
   * Parse the SOAP Message for a fault.
   *
   * @param message SOAP Message.
   * @return SOAP Fault.
   * @throws RequestFailed Exception if fault cannot be obtained or is null.
   */
  protected SOAPFault parseFault(SOAPMessage message) throws RequestFailed {
    SOAPFault fault = null;
    if (message != null) {
      try {
        SOAPBody body = message.getSOAPBody();
        if (body != null) {
          fault = body.getFault();
        }
      } catch (SOAPException e) {
        throw new RequestFailed(e.getMessage());
      }
    }
    if (fault == null) {
      throw new RequestFailed(FAULT_NULL_MESSAGE);
    }
    return fault;
  }
}
