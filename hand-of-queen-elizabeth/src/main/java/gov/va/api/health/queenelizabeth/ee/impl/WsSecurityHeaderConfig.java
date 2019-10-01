package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.handlers.WsSecurityHeaderSoapHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/** Configure the WS Security Header for SOAP Messages with the values from property file. */
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

  /** Required. */
  private String username;

  /** Required. */
  private String password;

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    Assert.notNull(username, "WsSecurityHeaderConfig username must not be null.");
    Assert.notNull(password, "WsSecurityHeaderConfig password must not be null.");
  }

  /**
   * Construct a bean to add the security header to SOAP Messages.
   *
   * @return WsSecurityHeaderSoapHandler bean.
   */
  @Bean
  public WsSecurityHeaderSoapHandler wsSecurityHeaderSoapHandler() {
    return new WsSecurityHeaderSoapHandler(this);
  }
}
