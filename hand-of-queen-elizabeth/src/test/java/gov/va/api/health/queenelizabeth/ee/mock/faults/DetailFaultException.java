package gov.va.api.health.queenelizabeth.ee.mock.faults;

import lombok.Data;

/**
 * Custom exception to store a detail string that will be inserted into a SOAPFault. This exception
 * must extend RuntimeException in order for the DetailSoapFaultDefinitionExceptionResolver to
 * recognize the exception and manipulate.
 */
@Data
public class DetailFaultException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private String detail;

  public DetailFaultException(String message, String detail) {
    super(message);
    this.detail = detail;
  }
}
