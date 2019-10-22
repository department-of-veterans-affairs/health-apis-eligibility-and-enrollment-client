package gov.va.api.health.queenelizabeth.ee.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration bean to hold configured WS Security Header settings for SOAP Messages with the
 * values from property file.
 */
@Slf4j
@Configuration
@ConfigurationProperties("ee.header")
@Data
public class WsSecurityHeaderConfig implements InitializingBean {

  /** Optional. */
  private String namespace = "wsse";

  /** Optional. */
  private String schema =
      "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

  /** Optional. */
  private String username;

  /** Optional. */
  private String password;

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    // If configured to apply WS Security Header then both of these properties must be specified.
    if (username == null ^ password == null) {
      throw new IllegalArgumentException(
          "WsSecurityHeaderConfig has invalid header configuration.");
    } else if (!applyHeader()) {
      log.warn("WsSecurityHeaderConfig is configured to not add security header.");
    }
  }

  /**
   * Check if the configuration is set to apply header.
   *
   * @return Boolean indicating if security header should be applied.
   */
  public boolean applyHeader() {
    return (username != null && password != null);
  }
}
