package gov.va.api.health.queenelizabeth.ee;

import javax.xml.soap.SOAPMessage;

/** Takes Generated Soap Request and returns Eligibility Data from EE Service. */
public interface EligibilityInfo {
  /** Returns Eligibility Data in XML. */
  String executeSoapCall(SOAPMessage soapRequestMessage);
}
