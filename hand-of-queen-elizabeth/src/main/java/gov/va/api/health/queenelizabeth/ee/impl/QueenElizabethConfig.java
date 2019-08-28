package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.EligibilityInfo;
import gov.va.api.health.queenelizabeth.util.XmlDocuments;
import gov.va.api.health.queenelizabeth.util.XmlDocuments.ParseFailed;
import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class QueenElizabethConfig {
  private final String endpointUrl;

  private final String eeTrustStorePath;

  private final String eeTrustStorePassword;

  private final Schema schema;

  private final NamespaceContext namespaceContext;

  /**
   * All-args constructor.
   *
   * @param endpointUrl Endpoint URL.
   * @param eeTrustStorePath Trust Store Path.
   * @param eeTrustStorePassword Trust Store Password.
   * @throws IOException IOException
   * @throws ParseFailed If unable to parse schema from wsdl.
   */
  public QueenElizabethConfig(
      @Value("${ee.endpoint.url}") String endpointUrl,
      @Value("${ee.truststore.path}") String eeTrustStorePath,
      @Value("${ee.truststore.password}") String eeTrustStorePassword)
      throws IOException, ParseFailed {
    this.endpointUrl = endpointUrl;
    this.eeTrustStorePath = eeTrustStorePath;
    this.eeTrustStorePassword = eeTrustStorePassword;

    // Load schema from the application wsdl interface.
    Resource resource = new ClassPathResource("eeSummary.wsdl");
    this.schema = XmlDocuments.getSchemaFromWsdl(resource.getFile());
    // Create a namespace context for xpath.
    this.namespaceContext =
        new EeNamespaceContext(XmlDocuments.getTargetNamespaceSchemaFromWsdl(resource.getFile()));
  }

  @Bean
  public Eligibilities eligibilities(@Autowired EligibilityInfo eligibilityInfo) {
    return new ValidateEligibilities(eligibilityInfo, schema, namespaceContext);
  }

  @Bean
  public EligibilityInfo eligibilityInfo() {
    return new SoapRequester(endpointUrl, eeTrustStorePath, eeTrustStorePassword);
  }
}
