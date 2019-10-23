package gov.va.api.health.queenelizabeth.ee.mock;

import gov.va.api.health.queenelizabeth.ee.mock.endpoints.MockEeSummaryResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/** Mock Spring Boot application to simulate an eeSummary SOAP Service. */
@SpringBootApplication
@Import({MockEeSummarySoapServiceConfig.class, MockEeSummaryResponse.class})
public class MockEeSummarySoapServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(MockEeSummarySoapServiceApplication.class, args);
  }
}
