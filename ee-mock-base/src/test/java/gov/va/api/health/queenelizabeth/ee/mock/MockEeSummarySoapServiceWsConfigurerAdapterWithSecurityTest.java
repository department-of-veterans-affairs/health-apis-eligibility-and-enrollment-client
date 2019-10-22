package gov.va.api.health.queenelizabeth.ee.mock;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test the WS Configurer Adapter is configured to use a security interceptor via application
 * properties.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@ContextConfiguration(
  classes = {MockEeSummarySoapServiceWsConfigurerAdapterConfig.class},
  initializers = ConfigFileApplicationContextInitializer.class
)
@DirtiesContext
public class MockEeSummarySoapServiceWsConfigurerAdapterWithSecurityTest {

  @Autowired MockEeSummarySoapServiceWsConfigurerAdapter adapter;

  /** Test the security interceptor bean was created as expected. */
  @Test
  public void testSecurityInterceptorConfigured() {
    assertNotNull(adapter.getSecurityInterceptor());
  }
}
