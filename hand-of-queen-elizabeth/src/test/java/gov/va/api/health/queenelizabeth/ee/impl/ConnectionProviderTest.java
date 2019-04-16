package gov.va.api.health.queenelizabeth.ee.impl;

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

  @Test(expected = Eligibilities.RequestFailed.class)
  @SneakyThrows
  public void badUrlProtocolGetsRequestFailed() {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(
            new URL("http://ee.va.gov:9334/getEESummary/"), "test-truststore.jks", "secret");
    connectionProvider.getConnection();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  @SneakyThrows
  public void localhostGetsRequestFailed() {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(
            new URL("https://localhost:9334/getEESummary/"), "test-truststore.jks", "secret");
    connectionProvider.getConnection();
  }

  @Test(expected = Eligibilities.RequestFailed.class)
  @SneakyThrows
  public void unknownHostGetsRequestFailed() {
    ConnectionProvider connectionProvider =
        new ConnectionProvider(
            new URL("https://ee.va.gov:9334/getEESummary/"), "test-truststore.jks", "secret");
    connectionProvider.getConnection();
  }
}
