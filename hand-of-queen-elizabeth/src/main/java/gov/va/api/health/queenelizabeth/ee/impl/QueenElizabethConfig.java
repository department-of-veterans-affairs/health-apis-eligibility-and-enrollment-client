package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.EligibilityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueenElizabethConfig {

  private final String endpointUrl;

  private final String eeTrustStorePath;

  private final String eeTrustStorePassword;

  /** All-args constructor. */
  public QueenElizabethConfig(
      @Value("${ee.endpoint.url}") String endpointUrl,
      @Value("${ee.truststore.path}") String eeTrustStorePath,
      @Value("${ee.truststore.password}") String eeTrustStorePassword) {
    this.endpointUrl = endpointUrl;
    this.eeTrustStorePath = eeTrustStorePath;
    this.eeTrustStorePassword = eeTrustStorePassword;
  }

  @Bean
  public Eligibilities eligibilities(@Autowired EligibilityInfo eligibilityInfo) {
    return new ValidateEligibilities(eligibilityInfo);
  }

  @Bean
  public EligibilityInfo eligibilityInfo() {
    return new SoapRequester(endpointUrl, eeTrustStorePath, eeTrustStorePassword);
  }
}
