package gov.va.api.health.queenelizabeth.ee.mock;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test the WS Configurer Adapter is configured to not use a security interceptor if application
 * properties not specified.
 *
 * <p>Note this is achieved by not enabling auto configuration so the application.properties are not
 * used referenced in this test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {MockEeSummarySoapServiceWsConfigurerAdapterConfig.class})
@DirtiesContext
public class MockEeSummarySoapServiceWsConfigurerAdapterWithoutSecurityInterceptorTest {

  @Autowired MockEeSummarySoapServiceWsConfigurerAdapter adapter;

  /** Test the security interceptor bean does not exist as expected. */
  @Test
  public void testSecurityInterceptorDoesNotExist() {
    assertNull(adapter.getSecurityInterceptor());
  }
}
