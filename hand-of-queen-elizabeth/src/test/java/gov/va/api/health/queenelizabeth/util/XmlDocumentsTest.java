package gov.va.api.health.queenelizabeth.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.va.api.health.queenelizabeth.util.XmlDocuments.ParseFailed;
import gov.va.api.health.queenelizabeth.util.XmlDocuments.WriteFailed;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import lombok.SneakyThrows;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

public class XmlDocumentsTest {
  @Test(expected = ParseFailed.class)
  public void badXmlCausesParseFailure() {
    String xml = "<a><b>bee</b><c><no-closing-tag></c></a>";
    XmlDocuments.create().parse(xml);
  }

  @Test
  public void roundTrip() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a><b>bee</b><c><hmm>sees</hmm><hmm>seas</hmm></c></a>";
    Document document = XmlDocuments.create().parse(xml);
    String roundTrip = XmlDocuments.create().write(document);
    assertThat(roundTrip).isEqualToIgnoringWhitespace(xml);
  }

  @Test(expected = WriteFailed.class)
  public void writeFailedExceptionIsThrownWhenNoDomImplementationIsFound() {
    DOMImplementationRegistry mock = Mockito.mock(DOMImplementationRegistry.class);
    when(mock.getDOMImplementation(Mockito.any())).thenReturn(null);
    XmlDocuments.findLsDomImplementationOrDie(mock);
  }

  @Test(expected = WriteFailed.class)
  public void writeFailedExceptionIsThrownWhenNoLsDomImplementationIsFound() {
    DOMImplementationRegistry mock = Mockito.mock(DOMImplementationRegistry.class);
    DOMImplementation mockDom = Mockito.mock(DOMImplementation.class);
    when(mock.getDOMImplementation(Mockito.any())).thenReturn(mockDom);
    XmlDocuments.findLsDomImplementationOrDie(mock);
  }

  @Test(expected = WriteFailed.class)
  @SneakyThrows
  public void writeFailedWhenSoapBodyCantBeFound() {
    SOAPMessage soapMessage = Mockito.mock(SOAPMessage.class);
    Mockito.when(soapMessage.getSOAPBody()).thenThrow(SOAPException.class);
    XmlDocuments.getSoapBodyAsString(soapMessage);
  }
}
