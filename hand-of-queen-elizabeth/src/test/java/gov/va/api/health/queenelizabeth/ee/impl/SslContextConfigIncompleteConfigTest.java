package gov.va.api.health.queenelizabeth.ee.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test loading an SslContextConfig configuration from properties and forcefully testing the case
 * where the trust store configuration is invalid resulting in an IllegalArgumentException.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = SslContextConfigIncompleteConfigTest.TestConfiguration.class,
    initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class SslContextConfigIncompleteConfigTest {

  @Autowired private SslContextConfig config;

  /**
   * By setting the password to null the result will be an IllegalArgumentException which prevents
   * application from starting.
   */
  @Test
  public void testIncompleteTrustStoreConfig() {
    config.setPassword(null);

    assertThrows(IllegalArgumentException.class, () -> config.afterPropertiesSet());
  }

  /** Load test context with just the SslContextConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {SslContextConfig.class})
  public static class TestConfiguration {}
}
