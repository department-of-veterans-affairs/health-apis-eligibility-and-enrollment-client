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
 * Test loading an WsSecurityHeaderConfig configuration from properties and forcefully testing the
 * case where the configuration is invalid resulting in an IllegalArgumentException.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = WsSecurityHeaderConfigTest.TestConfiguration.class,
    initializers = ConfigFileApplicationContextInitializer.class)
@DirtiesContext
public class WsSecurityHeaderConfigTest {

  @Autowired private WsSecurityHeaderConfig config;

  /**
   * By setting the password to invalid value the result will be an IllegalArgumentException which
   * prevents application from starting.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIncompleteHeaderConfig() {
    config.setPassword(null);
    config.afterPropertiesSet();
  }

  /** Load test context with just the WsSecurityHeaderConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {WsSecurityHeaderConfig.class})
  public static class TestConfiguration {}
}
