package gov.va.api.health.queenelizabeth.ee.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configure the SSL Context using the configured trust store. */
@Slf4j
@Configuration
@ConfigurationProperties("ee.truststore")
@Data
public class SslContextConfig implements InitializingBean {

  // TODO: It is not clear if TLS is the correct value
  // ie, (would TLSv1.2 be more appropriate)?
  // See
  // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SSLContext
  private static final String ALGORITHM_PROTOCOL = "TLS";

  private static final String KEYSTORE_TYPE = "JKS";

  private String path;

  private String password;

  @Override
  public void afterPropertiesSet() throws IllegalArgumentException {
    // If configured to use trust store then both of these properties must be specified.
    if (path == null ^ password == null) {
      throw new IllegalArgumentException("SslContextConfig has invalid trust store configuration.");
    } else if (path == null && password == null) {
      log.warn("SslContextConfig is configured to not use a trust store.");
    } else {
      // Set the default SSL Socket Factory SSLContext with the configured trust store.
      try {
        HttpsURLConnection.setDefaultSSLSocketFactory(initSslContext().getSocketFactory());
      } catch (Exception e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
  }

  /**
   * Initialize the SSLContext.
   *
   * @return SSLContext.
   * @throws RuntimeException Exception for null classloader.
   * @throws IOException Exception for problem finding configured trust store.
   * @throws NoSuchAlgorithmException Exception for unsupported algorithm.
   */
  private SSLContext initSslContext()
      throws IOException, RuntimeException, NoSuchAlgorithmException {
    // Satisfying Fortify null check.
    ClassLoader cl = getClass().getClassLoader();
    if (cl == null) {
      throw new RuntimeException("Could not obtain class loader.");
    }
    // Check the truststore can be found.
    if (cl.getResource(FilenameUtils.getName(path)) == null) {
      throw new IOException("Could not load trust store.");
    }
    // Load the truststore that contains the ee certs.
    SSLContext sslContext = SSLContext.getInstance(ALGORITHM_PROTOCOL);
    try (InputStream truststoreInputStream = cl.getResourceAsStream(FilenameUtils.getName(path))) {
      KeyStore ts = KeyStore.getInstance(KEYSTORE_TYPE);
      ts.load(truststoreInputStream, password.toCharArray());
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(ts);
      // Initialize the ssl context using the truststore.
      sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
    } catch (IOException | CertificateException | KeyStoreException | KeyManagementException e) {
      throw new RuntimeException(e.getMessage());
    }
    return sslContext;
  }
}
