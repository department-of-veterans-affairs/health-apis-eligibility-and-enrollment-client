package gov.va.api.health.queenelizabeth.ee.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Mock Spring Boot application to simulate an eeSummary SOAP Service. */
@SpringBootApplication
public class MockEeSummarySoapServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(MockEeSummarySoapServiceApplication.class, args);
  }
}
