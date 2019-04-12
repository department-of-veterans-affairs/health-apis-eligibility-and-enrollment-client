package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.Builder;
import org.w3c.dom.Document;

public class XmlResponseValidator {
  private final SoapMessageGenerator soapMessageGenerator;
  private final Document response;

  @Builder
  private XmlResponseValidator(SoapMessageGenerator soapMessageGenerator, Document response) {
    this.soapMessageGenerator = soapMessageGenerator;
    this.response = response;
  }

  private String extractSummaryField() {
    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      return xpath.compile("/getEESummaryResponse").evaluate(response);
    } catch (XPathExpressionException e) {
      throw new Eligibilities.RequestFailed(soapMessageGenerator, "Don't Understand XML.");
    }
  }

  void validate() {
    String summary = extractSummaryField();
    if (summary.isEmpty()) {
      throw new Eligibilities.RequestFailed(
          soapMessageGenerator, "Don't Understand XML, getEESummaryResponse is Missing");
    }
  }
}
