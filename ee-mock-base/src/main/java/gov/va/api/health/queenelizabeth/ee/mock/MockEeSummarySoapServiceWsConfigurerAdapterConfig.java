package gov.va.api.health.queenelizabeth.ee.mock;

import gov.va.api.health.queenelizabeth.ee.config.WsSecurityHeaderConfig;
import org.springframework.context.annotation.Import;

/**
 * Configuration class to instantiate a MockEeSummarySoapServiceWsConfigurerAdapter for use within a
 * Mock eeSummary SOAP service.
 */
@Import({
  WsSecurityHeaderConfig.class,
  MockEeSummarySecurityInterceptor.class,
  MockEeSummarySoapServiceWsConfigurerAdapter.class
})
public class MockEeSummarySoapServiceWsConfigurerAdapterConfig {}
