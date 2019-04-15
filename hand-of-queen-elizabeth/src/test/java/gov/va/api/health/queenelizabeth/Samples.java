package gov.va.api.health.queenelizabeth;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StreamUtils;

@NoArgsConstructor(staticName = "create")
public class Samples {

  public String eeFault() {
    return read("eeFault.xml");
  }

  public String getEeSummaryResponse() {
    return read("getEESummaryResponse.xml");
  }

  public String getEeSummaryResponseBody() {
    return read("getEeSummaryResponseBody.xml");
  }

  @SneakyThrows
  private String read(String fileName) {
    InputStream stream = getClass().getResourceAsStream("/samples/" + fileName);
    assertThat(stream).isNotNull();
    return StreamUtils.copyToString(stream, StandardCharsets.UTF_8).replaceAll("(>\\W+<)", "><");
  }
}
