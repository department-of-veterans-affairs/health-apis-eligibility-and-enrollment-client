package gov.va.api.health.queenelizabeth.ee.mock;

import gov.va.api.health.queenelizabeth.ee.config.WsSecurityHeaderConfig;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;

/**
 * WS Security Interceptor to perform simple UsernameToken validation.
 *
 * <p>Note this bean is conditional on WsSecurityHeaderConfig bean or if said bean exists,
 * configured to apply header.
 */
@Configuration
@ConditionalOnBean(WsSecurityHeaderConfig.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MockEeSummarySecurityInterceptor {

  private final WsSecurityHeaderConfig config;

  /** Validation for configured username/password. */
  public SimplePasswordValidationCallbackHandler securityCallbackHandler() {
    SimplePasswordValidationCallbackHandler callbackHandler =
        new SimplePasswordValidationCallbackHandler();
    Properties users = new Properties();
    users.setProperty(config.getUsername(), config.getPassword());
    callbackHandler.setUsers(users);
    return callbackHandler;
  }

  /** Security Interceptor. */
  @Bean
  public Wss4jSecurityInterceptor securityInterceptor() {
    if (config.applyHeader()) {
      Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
      securityInterceptor.setValidationActions(ConfigurationConstants.USERNAME_TOKEN);
      securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());
      return securityInterceptor;
    }
    return null;
  }
}
