package gov.va.api.health.queenelizabeth.ee.mock;

import gov.va.api.health.queenelizabeth.ee.impl.WsSecurityHeaderConfig;
import gov.va.api.health.queenelizabeth.ee.mock.faults.DetailFaultException;
import gov.va.api.health.queenelizabeth.ee.mock.faults.DetailSoapFaultDefinitionExceptionResolver;
import gov.va.api.health.queenelizabeth.ee.mock.faults.ServiceFaultClientException;
import gov.va.api.health.queenelizabeth.ee.mock.faults.ServiceFaultServerException;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * A WS Configurer Adapter to configure the Mock eeSummary SOAP service.
 *
 * <p>Note that other than this class supports simulation of custom fault handling via the
 * exceptionResolver but other than that it is basically the same as mockee WebServiceConfig.
 */
@EnableWs
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MockEeSummarySoapServiceWsConfigurerAdapter extends WsConfigurerAdapter {

  public static final String mockEeVersion = "/v0";

  private final WsSecurityHeaderConfig config;

  @Override
  public void addInterceptors(List<EndpointInterceptor> interceptors) {
    interceptors.add(securityInterceptor());
  }

  /** Default Wsdl. */
  @Bean(name = "summaries")
  public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema eeSchema) {
    DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("SummaryPort");
    wsdl11Definition.setLocationUri(mockEeVersion + "/ws");
    wsdl11Definition.setTargetNamespace("http://jaxws.webservices.esr.med.va.gov/schemas");
    wsdl11Definition.setSchema(eeSchema);
    return wsdl11Definition;
  }

  /** Get XSD Schema. */
  @Bean
  public XsdSchema eeSchema() {
    return new SimpleXsdSchema(new ClassPathResource("wsdl/eeSummary.xsd"));
  }

  /**
   * Custom exception resolver to facilitate simulation of the various fault condition samples.
   *
   * @return SoapFaultMappingExceptionResolver.
   */
  @Bean
  public SoapFaultMappingExceptionResolver exceptionResolver() {
    SoapFaultMappingExceptionResolver exceptionResolver =
        new DetailSoapFaultDefinitionExceptionResolver();
    // Supply a default soap fault definition for the resolver.
    SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
    faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
    exceptionResolver.setDefaultFault(faultDefinition);
    Properties errorMappings = new Properties();
    // Apply mappings of exception type to their appropriate SoapFaultDefinition codes which
    // ultimately get populated in the SOAPFault code element.
    errorMappings.setProperty(Exception.class.getName(), SoapFaultDefinition.SERVER.toString());
    errorMappings.setProperty(
        DetailFaultException.class.getName(), SoapFaultDefinition.SERVER.toString());
    errorMappings.setProperty(
        ServiceFaultServerException.class.getName(), SoapFaultDefinition.SERVER.toString());
    errorMappings.setProperty(
        ServiceFaultClientException.class.getName(), SoapFaultDefinition.CLIENT.toString());
    exceptionResolver.setExceptionMappings(errorMappings);
    exceptionResolver.setOrder(1);
    return exceptionResolver;
  }

  /** Set up Servlet. */
  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
      ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, mockEeVersion + "/ws/*");
  }

  /** Validation for user/password. */
  @Bean
  public SimplePasswordValidationCallbackHandler securityCallbackHandler() {
    SimplePasswordValidationCallbackHandler callbackHandler =
        new SimplePasswordValidationCallbackHandler();
    Properties users = new Properties();
    users.setProperty(config.getUsername(), config.getPassword());
    callbackHandler.setUsers(users);
    return callbackHandler;
  }

  /** Security Interceptor. */
  @Bean
  public Wss4jSecurityInterceptor securityInterceptor() {
    Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
    securityInterceptor.setValidationActions("UsernameToken");
    securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());
    return securityInterceptor;
  }
}
