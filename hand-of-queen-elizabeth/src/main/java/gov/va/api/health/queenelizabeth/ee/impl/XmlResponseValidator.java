package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

public class XmlResponseValidator {
  private final SoapMessageGenerator soapMessageGenerator;
  private final Document response;
  private final NamespaceContext namespaceContext;

  @Builder
  private XmlResponseValidator(
      SoapMessageGenerator soapMessageGenerator,
      Document response,
      NamespaceContext namespaceContext) {
    this.soapMessageGenerator = soapMessageGenerator;
    this.response = response;
    this.namespaceContext = namespaceContext;
  }

  private String extractFaultString() {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      return xpath.compile("/Fault/faultstring").evaluate(response);
    } catch (XPathExpressionException e) {
      throw new Eligibilities.RequestFailed(soapMessageGenerator, "Don't Understand XML.");
    }
  }

  private String extractSummaryField() {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      // Not clear why this approach is used instead of simply using jaxb to unmarshall the
      // response?  It would save a lot
      // of headache with parsing the xml into a dom and validating it against the schema
      // pulled from the wsdl as the response class should already be trusted.
      // Anyways, using this approach, once we added namespace aware to the document to
      // validate it introduces issues when running xpath as the elements are in the default
      // namespace.
      // One option is to hack it using local-name like so:
      // xpath.compile("/*[local-name()='getEESummaryResponse']").evaluate(response);
      // The option implemented is to specify a namespace context but this requires knowing
      // the namespace.
      // So as not to hardcod it in the context, it is obtained as a bean at
      // application startup from the wsdl schema targetNamespace.
      xpath.setNamespaceContext(namespaceContext);
      return xpath.compile("/ns:getEESummaryResponse").evaluate(response);
    } catch (XPathExpressionException e) {
      throw new Eligibilities.RequestFailed(soapMessageGenerator, "Don't Understand XML.");
    }
  }

  void validate() {
    String summary = extractSummaryField();
    if (!summary.isEmpty()) {
      return;
    }

    String faultString = extractFaultString();
    if (StringUtils.isNotBlank(faultString)) {
      if (faultString.equals("PERSON_NOT_FOUND")) {
        throw new Eligibilities.PersonNotFound(soapMessageGenerator, faultString);
      }
      throw new Eligibilities.RequestFailed(soapMessageGenerator, faultString);
    }

    throw new Eligibilities.RequestFailed(
        soapMessageGenerator, "Don't Understand XML, getEESummaryResponse is Missing");
  }
}
