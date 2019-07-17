package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
    try {
      InetAddress.getByName(endpointUrl.getHost());
    } catch (UnknownHostException e) {
      throw new Eligibilities.RequestFailed("Unknown Host");
    }
    if (endpointUrl.getProtocol().equals("http")) {
      urlConnection = (HttpURLConnection) endpointUrl.openConnection();
      urlConnection.connect();

    } else {
      HttpsURLConnection.setDefaultSSLSocketFactory(getSslContext().getSocketFactory());
      urlConnection = (HttpsURLConnection) endpointUrl.openConnection();
      urlConnection.connect();
    }
    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    soapConnection = soapConnectionFactory.createConnection();
    return soapConnection;
  }

  @SneakyThrows
  SSLContext getSslContext() {
    /* Load the truststore that contains the ee certs. */
    try (InputStream truststoreInputStream =
        ConnectionProvider.class.getResourceAsStream(FilenameUtils.getName(truststorePath))) {
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
}
