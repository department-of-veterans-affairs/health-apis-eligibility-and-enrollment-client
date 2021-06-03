package gov.va.api.health.queenelizabeth.ee.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test loading an SslContextConfig configuration from properties and forcefully testing the case
 * where the trust store configuration has invalid password resulting in an
 * IllegalArgumentException.
 */
@ContextConfiguration(
    classes = SslContextConfigInvalidPasswordTest.TestConfiguration.class,
    initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class SslContextConfigInvalidPasswordTest {

  @Autowired private SslContextConfig config;

  /**
   * By setting the password to invalid value the result will be an IllegalArgumentException which
   * prevents application from starting.
   */
  @Test
  public void testInvalidTrustStoreConfig() {
    config.setPassword("invalid");

    assertThrows(IllegalArgumentException.class, () -> config.afterPropertiesSet());
  }

  /** Load test context with just the SslContextConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {SslContextConfig.class})
  public static class TestConfiguration {}
}
