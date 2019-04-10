package gov.va.api.health.queenelizabeth.ee;

import javax.xml.soap.SOAPMessage;
import org.springframework.stereotype.Component;

/** Takes Generated Soap Request and returns Eligibility Data from EE Service. */
@Component
public interface EligibilityInfo {

  /** Returns Eligibility Data in XML. */
  String executeSoapCall(SOAPMessage soapRequestMessage);
}
