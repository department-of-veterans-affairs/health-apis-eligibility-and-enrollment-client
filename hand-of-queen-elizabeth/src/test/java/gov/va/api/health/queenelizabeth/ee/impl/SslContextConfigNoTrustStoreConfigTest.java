package gov.va.api.health.queenelizabeth.ee.impl;

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
 * where the application is configured to not use a trust store.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = SslContextConfigNoTrustStoreConfigTest.TestConfiguration.class,
    initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class SslContextConfigNoTrustStoreConfigTest {

  @Autowired private SslContextConfig config;

  /** If both the password and path are not set then the application will not use a trust store. */
  @Test
  public void testNoTrustStoreConfig() {
    config.setPassword(null);
    config.setPath(null);
    config.afterPropertiesSet();
  }

  /** Load test context with just the SslContextConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {SslContextConfig.class})
  public static class TestConfiguration {}
}
