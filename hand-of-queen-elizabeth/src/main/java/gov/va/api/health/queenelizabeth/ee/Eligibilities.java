package gov.va.api.health.queenelizabeth.ee;

import javax.xml.soap.SOAPMessage;

/** The primary interface for sending requests to the EE Service. */
public interface Eligibilities {
  String request(SoapMessageGenerator soapMessageGenerator);

  class EligibilitiesException extends RuntimeException {
    EligibilitiesException(String message, Throwable cause) {
      super(message, cause);
    }

    EligibilitiesException(String message) {
      super(message);
    }
  }

  class MissingIcnValue extends EligibilitiesException {
    public MissingIcnValue(SoapMessageGenerator soapMessageGenerator) {
      super(soapMessageGenerator.createGetEeSummarySoapRequest().toString());
    }
  }

  class PersonNotFound extends EligibilitiesException {
    public PersonNotFound(SoapMessageGenerator soapMessageGenerator, String message) {
      super(
          soapMessageGenerator.createGetEeSummarySoapRequest().toString() + " Reason: " + message);
    }
  }

  class RequestFailed extends EligibilitiesException {
    public RequestFailed(SoapMessageGenerator soapMessageGenerator, Exception cause) {
      super(soapMessageGenerator.createGetEeSummarySoapRequest().toString(), cause);
    }

    public RequestFailed(SoapMessageGenerator soapMessageGenerator, String message) {
      super(
          soapMessageGenerator.createGetEeSummarySoapRequest().toString() + " Reason: " + message);
    }

    public RequestFailed(SOAPMessage soapRequestMessage, String message) {
      super(soapRequestMessage.toString() + " Reason: " + message);
    }

    public RequestFailed(String message) {
      super(" Reason: " + message);
    }
  }
}
