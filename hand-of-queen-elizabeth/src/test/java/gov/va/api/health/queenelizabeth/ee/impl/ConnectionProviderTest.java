package gov.va.api.health.queenelizabeth.ee.impl;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import java.net.URL;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class ConnectionProviderTest {

  @Before
  @SneakyThrows
  public void _init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @SneakyThrows
  public void httpsSslContextSetToTls() {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(
            new URL("https://localhost:9334/getEESummary/"), "test-truststore.jks", "secret");
    assertThat(connectionProvider.getSslContext().getProtocol()).isEqualTo("TLS");
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  @SneakyThrows
  public void unknownHostGetsRequestFailedForHttp() {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(
            new URL("http://ee.va.gov:9334/getEESummary/"), null, null);
    connectionProvider.getConnection();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  @SneakyThrows
  public void unknownHostGetsRequestFailedForHttps() {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(
            new URL("https://ee.va.gov:9334/getEESummary/"), "test-truststore.jks", "secret");
    connectionProvider.getConnection();
  }
}
