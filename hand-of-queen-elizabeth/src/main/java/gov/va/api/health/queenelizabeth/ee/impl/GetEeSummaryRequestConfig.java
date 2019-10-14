package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.handlers.GetEeSummaryRequestFaultSoapHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/** Configuration class for GetEESummaryRequest soap service. */
@Slf4j
@Configuration
@ConfigurationProperties("ee.request")
@Data
public class GetEeSummaryRequestConfig implements InitializingBean {

  /** Required. */
  private String name;

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    Assert.notNull(name, "GetEeSummaryRequestConfig name must not be null.");
  }

  /**
   * Construct a bean to do custom fault handling for the getEeSummary SOAP Service.
   *
   * @return GetEeSummaryRequestFaultSoapHandler bean.
   */
  @Bean
  public GetEeSummaryRequestFaultSoapHandler getEeSummaryRequestFaultSoapHandler() {
    return new GetEeSummaryRequestFaultSoapHandler();
  }
}
