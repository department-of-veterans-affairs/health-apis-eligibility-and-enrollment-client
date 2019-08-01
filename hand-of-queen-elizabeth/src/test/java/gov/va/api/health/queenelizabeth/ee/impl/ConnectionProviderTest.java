package gov.va.api.health.queenelizabeth.ee.impl;

import static org.assertj.core.api.Assertions.assertThat;

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

  @Test(expected = ConnectionProvider.ClassLoaderException.class)
  public void throwingClassLoaderException() {
    throw new ConnectionProvider.ClassLoaderException("This is a test");
  }
}
