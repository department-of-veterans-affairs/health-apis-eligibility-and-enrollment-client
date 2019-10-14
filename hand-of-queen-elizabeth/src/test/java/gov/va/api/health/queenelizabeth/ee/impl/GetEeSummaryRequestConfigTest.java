package gov.va.api.health.queenelizabeth.ee.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test loading an GetEeSummaryRequestConfig soap service configuration from properties and
 * forcefully testing the case where the name is not configured resulting in an
 * IllegalArgumentException.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
  classes = GetEeSummaryRequestConfigTest.TestConfiguration.class,
  initializers = ConfigFileApplicationContextInitializer.class
)
public class GetEeSummaryRequestConfigTest {

  @Autowired private GetEeSummaryRequestConfig config;

  /**
   * By setting the name to null the result will be an IllegalArgumentException which prevents
   * application from starting.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIncompleteConfig() {
    config.setName(null);
    config.afterPropertiesSet();
  }

  /** Load test context with just the QueenElizabethConfig. */
  @EnableAutoConfiguration
  @EnableConfigurationProperties(value = {GetEeSummaryRequestConfig.class})
  public static class TestConfiguration {}
}
