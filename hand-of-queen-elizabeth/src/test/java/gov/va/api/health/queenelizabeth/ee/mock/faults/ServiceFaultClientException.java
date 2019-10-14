package gov.va.api.health.queenelizabeth.ee.mock.faults;

/**
 * Custom detail fault exception that is mapped to SoapFaultDefinition.CLIENT to used by the
 * DetailSoapFaultDefinitionExceptionResolver mapping to apply to appropriate SOAPFault code.
 */
public class ServiceFaultClientException extends DetailFaultException {
  public ServiceFaultClientException(String message, String detail) {
    super(message, detail);
  }
}
