package gov.va.api.health.queenelizabeth.ee.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test loading an SslContextConfig configuration from properties and forcefully testing the case
 * where the trust store can not be found resulting in an IllegalArgumentException.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = SslContextConfigInvalidPathTest.TestConfiguration.class,
  initializers = ConfigFileApplicationContextInitializer.class
)
@DirtiesContext
public class SslContextConfigInvalidPathTest {

  @Autowired private SslContextConfig config;

  /**
   * By setting the trust store to an invalid path the result will be an IllegalArgumentException
   * which prevents application from starting.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTrustStoreConfig() {
    config.setPath("invalid.jks");
    config.afterPropertiesSet();
  }

  /** Load test context with just the SslContextConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {SslContextConfig.class})
  public static class TestConfiguration {}
}
