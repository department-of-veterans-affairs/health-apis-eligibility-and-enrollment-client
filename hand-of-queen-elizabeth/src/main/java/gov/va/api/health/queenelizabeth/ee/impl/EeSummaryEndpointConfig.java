package gov.va.api.health.queenelizabeth.ee.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to point the Queen Elizabeth Service to the appropriate endpoint base url.
 */
@Slf4j
@Configuration
@ConfigurationProperties("ee.endpoint")
@Data
public class EeSummaryEndpointConfig implements InitializingBean {

  /** Optional. */
  private String url;

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    if (url == null) {
      log.info("QueenElizabethConfig will use default wsdl endpoint url.");
    }
  }
}
