package gov.va.api.health.queenelizabeth.ee.impl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;

public class ConnectionProvider {

  private final URL endpointUrl;

  private final String truststorePath;

  private final String truststorePassword;

  SOAPConnection soapConnection;

  HttpURLConnection urlConnection;

  /** Constructor. */
  public ConnectionProvider(URL endpointUrl, String truststorePath, String truststorePassword) {
    this.endpointUrl = endpointUrl;
    this.truststorePath = truststorePath;
    this.truststorePassword = truststorePassword;
  }

  /** Disconnect. */
  @SneakyThrows
  public void disconnect() {
    soapConnection.close();
    urlConnection.disconnect();
  }

  /** Get HTTPS Connection to EE. */
  @SneakyThrows
  public SOAPConnection getConnection() {
    if (endpointUrl.getProtocol().equals("http")) {
      urlConnection = (HttpURLConnection) endpointUrl.openConnection();
    } else {
      HttpsURLConnection.setDefaultSSLSocketFactory(getSslContext().getSocketFactory());
      urlConnection = (HttpsURLConnection) endpointUrl.openConnection();
    }
    urlConnection.connect();
    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    soapConnection = soapConnectionFactory.createConnection();
    return soapConnection;
  }

  @SneakyThrows
  SSLContext getSslContext() {
    /* Satisfying Fortify null check */
    ClassLoader cl = getClass().getClassLoader();
    if (cl == null) {
      throw new ClassLoaderException("Something went wrong getting the class loader");
    }
    /* Load the truststore that contains the ee certs. */
    try (InputStream truststoreInputStream =
        cl.getResourceAsStream(FilenameUtils.getName(truststorePath))) {
      KeyStore ts = KeyStore.getInstance("JKS");
      ts.load(truststoreInputStream, truststorePassword.toCharArray());
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(ts);
      /* Initialize the ssl context using the truststore. */
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
      return sslContext;
    }
  }

  public static class ClassLoaderException extends RuntimeException {
    ClassLoaderException(String message) {
      super(message);
    }
  }
}
