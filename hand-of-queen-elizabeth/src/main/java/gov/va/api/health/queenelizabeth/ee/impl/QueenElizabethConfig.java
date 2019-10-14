package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.QueenElizabethService;
import org.springframework.context.annotation.Import;

/**
 * Queen Elizabeth Service Configuration class to instantiate a Queen Elizabeth Service adapter that
 * wraps the eeSummary SOAP Service. Import of this class will configure the Queen Elizabeth service
 * as appropriate via application properties.
 */
@Import({
  EeSummaryEndpointConfig.class,
  SslContextConfig.class,
  WsSecurityHeaderConfig.class,
  GetEeSummaryRequestConfig.class,
  QueenElizabethService.class
})
public class QueenElizabethConfig {}
