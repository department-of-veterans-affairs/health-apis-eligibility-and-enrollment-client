package gov.va.api.health.queenelizabeth.ee.mock.faults;

/**
 * Custom detail fault exception that is mapped to SoapFaultDefinition.SERVER to used by the
 * DetailSoapFaultDefinitionExceptionResolver mapping to apply to appropriate SOAPFault code.
 */
public class ServiceFaultServerException extends DetailFaultException {
  public ServiceFaultServerException(String message, String detail) {
    super(message, detail);
  }
}
