package gov.va.api.health.queenelizabeth.ee.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test loading an EeSummaryEndpointConfig configuration from properties and forcefully testing the
 * case where the url is not configured resulting in the default wsdl url usage.
 */
@ContextConfiguration(
    classes = EeSummaryEndpointConfigTest.TestConfiguration.class,
    initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class EeSummaryEndpointConfigTest {

  @Autowired private EeSummaryEndpointConfig config;

  /**
   * By setting the url to null the result will be to use the default wsdl location in the
   * QueenElizabethService.
   */
  @Test
  public void testDefaultConfig() {
    config.setUrl(null);
    config.afterPropertiesSet();
  }

  /** Load test context with just the EeSummaryEndpointConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {EeSummaryEndpointConfig.class})
  public static class TestConfiguration {}
}
