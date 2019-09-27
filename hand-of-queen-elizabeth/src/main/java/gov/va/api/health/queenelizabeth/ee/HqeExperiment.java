package gov.va.api.health.queenelizabeth.ee;

import gov.va.med.esr.webservices.jaxws.schemas.EeSummaryPort;
import gov.va.med.esr.webservices.jaxws.schemas.EeSummaryPortService;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryRequest;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HqeExperiment {
  static final String trustStoreName = "eligibilityandenrollment-nonprod-truststore.jks";

  static final String trustStorePassword = "PASSWORD";

  static final String wsdlUrl =
      "https://vaww.esrpre-prod.aac.va.gov:8443/esr-ws/spring-ws/getEESummary/eeSummary.wsdl";

  static final String icn = "0000001008405009V205102000000";

  static final String headerUsername = "HealthAPISvcUsr";

  static final String headerPassword = "PASSWORD";

  @SneakyThrows
  static SSLContext configureSslContext() {
    try (InputStream tsInputStream =
        new FileInputStream(
            new File(
                "C:/workspace/health-apis-eligibility-and-enrollment-client/hand-of-queen-elizabeth"
                    + "/src/main/resources/"
                    + trustStoreName))) {
      KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(tsInputStream, trustStorePassword.toCharArray());
      TrustManagerFactory tmFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmFactory.init(ks);
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, tmFactory.getTrustManagers(), new SecureRandom());
      return sslContext;
    }
  }

  @SneakyThrows
  public static void main(String[] args) {
    HttpsURLConnection.setDefaultSSLSocketFactory(configureSslContext().getSocketFactory());

    EeSummaryPort port = new EeSummaryPortService(new URL(wsdlUrl)).getEeSummaryPortSoap11();
    Binding binding = ((BindingProvider) port).getBinding();
    @SuppressWarnings("rawtypes")
    List<Handler> handlers = binding.getHandlerChain();
    handlers.add(new SecurityHandler());
    binding.setHandlerChain(handlers);

    GetEESummaryResponse response =
        port.getEESummary(
            GetEESummaryRequest.builder().key(icn).requestName("CommunityCareInfo").build());
    log.info(response.toString());
  }

  private static final class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
    @Override
    public void close(MessageContext context) {}

    @Override
    public Set<QName> getHeaders() {
      return Collections.emptySet();
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
      return false;
    }

    @Override
    @SneakyThrows
    public boolean handleMessage(SOAPMessageContext context) {
      SOAPEnvelope env = context.getMessage().getSOAPPart().getEnvelope();
      env.addNamespaceDeclaration(
          "wsse",
          "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
      SOAPElement usernameToken =
          env.getHeader()
              .addChildElement("Security", "wsse")
              .addChildElement("UsernameToken", "wsse");
      usernameToken.addChildElement("Username", "wsse").addTextNode(headerUsername);
      usernameToken.addChildElement("Password", "wsse").addTextNode(headerPassword);
      return true;
    }
  }
}
