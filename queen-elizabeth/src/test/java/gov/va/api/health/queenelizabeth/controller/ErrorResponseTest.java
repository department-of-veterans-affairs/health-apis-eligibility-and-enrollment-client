package gov.va.api.health.queenelizabeth.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import lombok.SneakyThrows;
import org.junit.Test;

public class ErrorResponseTest {

  @Test
  public void errorResponse() {
    roundTrip(new ErrorResponse());
    roundTrip(ErrorResponse.builder().message("halp").timestamp(80085).type("halpful").build());
  }

  @Test
  public void fromException() {
    ErrorResponse halp = ErrorResponse.of(new RuntimeException("halp"));
    assertThat(halp.message()).isEqualTo("halp");
    assertThat(halp.type()).isEqualTo("RuntimeException");
  }

  @SneakyThrows
  private <T> void roundTrip(T object) {
    ObjectMapper mapper = new JacksonConfig().objectMapper();
    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    Object evilTwin = mapper.readValue(json, object.getClass());
    assertThat(evilTwin).isEqualTo(object);
    assertThat(evilTwin.hashCode()).isEqualTo(object.hashCode());
  }
}
