package gov.va.api.health.queenelizabeth.ee.config;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test loading an WsSecurityHeaderConfig configuration with no properties and forcefully testing
 * the case where the configuration will optionally not apply a header.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WsSecurityHeaderConfig.class)
@DirtiesContext
public class WsSecurityHeaderConfigNoHeaderTest {

  @Autowired private WsSecurityHeaderConfig config;

  /** Bean configured without properties will not apply header. */
  @Test
  public void testDoNotApplyHeaderConfig() {
    assertFalse(config.applyHeader());
  }
}
