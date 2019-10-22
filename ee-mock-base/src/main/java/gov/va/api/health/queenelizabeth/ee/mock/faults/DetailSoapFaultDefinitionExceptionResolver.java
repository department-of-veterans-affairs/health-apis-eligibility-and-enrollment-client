package gov.va.api.health.queenelizabeth.ee.mock.faults;

import javax.xml.namespace.QName;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

/**
 * Custom SOAP Fault mapping exception resolver used to add custom fault detail element to SOAPFault
 * fault element. This is necessary to simulate the exact payloads of the sample faults via the mock
 * SOAP service.
 */
public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {

  private static final QName DETAIL = new QName("detail");

  @Override
  protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
    if (ex instanceof DetailFaultException) {
      SoapFaultDetail detail = fault.addFaultDetail();
      detail.addFaultDetailElement(DETAIL).addText(((DetailFaultException) ex).getDetail());
    }
  }
}
