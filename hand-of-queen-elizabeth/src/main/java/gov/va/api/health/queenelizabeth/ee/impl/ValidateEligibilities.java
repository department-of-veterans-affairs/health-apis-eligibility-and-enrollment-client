package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.EligibilityInfo;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import gov.va.api.health.queenelizabeth.util.XmlDocuments;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

/** Combines EE requesting and response validation. */
@Slf4j
@AllArgsConstructor
public class ValidateEligibilities implements Eligibilities {
  private final EligibilityInfo eligibilityInfo;

  private Document parse(SoapMessageGenerator soapMessageGenerator, String xml) {
    try {
      return XmlDocuments.create().parse(xml);
    } catch (XmlDocuments.ParseFailed e) {
      log.error("Couldn't parse EE response: {} ", e.getMessage());
      throw new RequestFailed(soapMessageGenerator, e);
    }
  }

  @Override
  @SneakyThrows
  public String request(final SoapMessageGenerator messageGenerator) {
    validate(messageGenerator);
    String xml = eligibilityInfo.executeSoapCall(messageGenerator.createGetEeSummarySoapRequest());
    Document document = parse(messageGenerator, xml);
    XmlResponseValidator.builder()
        .soapMessageGenerator(messageGenerator)
        .response(document)
        .build()
        .validate();
    return xml;
  }

  private void validate(SoapMessageGenerator soapMessageGenerator) {
    if (soapMessageGenerator.id().isEmpty()) {
      throw new MissingIcnValue(soapMessageGenerator);
    }
  }
}
