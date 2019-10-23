package gov.va.api.health.queenelizabeth.ee.mock;

import gov.va.api.health.queenelizabeth.ee.config.WsSecurityHeaderConfig;
import gov.va.api.health.queenelizabeth.ee.mock.endpoints.MockEeSummaryEndpoint;
import org.springframework.context.annotation.Import;

/** Configuration class to facilitate running a Mock eeSummary SOAP service. */
@Import({
  WsSecurityHeaderConfig.class,
  MockEeSummarySecurityInterceptor.class,
  MockEeSummarySoapServiceWsConfigurerAdapter.class,
  MockEeSummaryEndpoint.class
})
public class MockEeSummarySoapServiceConfig {}
