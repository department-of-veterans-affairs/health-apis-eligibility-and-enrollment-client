package gov.va.api.health.queenelizabeth.ee.impl;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import java.io.InputStream;
import java.net.InetAddress;
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

  private final URL enpointUrl;

  private final String truststorePath;

  private final String truststorePassword;

  SOAPConnection soapConnection;

  HttpsURLConnection httpsUrlConnection;

  /** Constructor. */
  public ConnectionProvider(URL endpointUrl, String truststorePath, String truststorePassword) {
    this.enpointUrl = endpointUrl;
    this.truststorePath = truststorePath;
    this.truststorePassword = truststorePassword;
  }

  @SneakyThrows
  public void disconnect() {
    soapConnection.close();
    httpsUrlConnection.disconnect();
  }

  /** Get HTTPS Connection to EE. */
  @SneakyThrows
  public SOAPConnection getConnection() {
    /* In E&E we trust. */
    httpsUrlConnection = openHttpsConnection();
    SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
    soapConnection = soapConnectionFactory.createConnection();
    return soapConnection;
  }

  @SneakyThrows
  private SSLContext getSslContext() {
    /* Load the truststore that contains the ee certs. */
    InputStream truststoreInputStream =
        getClass().getClassLoader().getResourceAsStream(FilenameUtils.getName(truststorePath));
    KeyStore ts = KeyStore.getInstance("JKS");
    ts.load(truststoreInputStream, truststorePassword.toCharArray());
    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(ts);
    /* Initialize the ssl context using the truststore. */
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
    truststoreInputStream.close();
    return sslContext;
  }

  @SneakyThrows
  private HttpsURLConnection openHttpsConnection() {
    /* HTTPS connection with the EE service. */
    HttpsURLConnection.setDefaultSSLSocketFactory(getSslContext().getSocketFactory());
    String urlProtocol = enpointUrl.getProtocol();
    if (!urlProtocol.equals("https")) {
      throw new Eligibilities.RequestFailed("E&E Url received is not https.");
    }
    String urlHost = enpointUrl.getHost();
    InetAddress inetAddress = InetAddress.getByName(urlHost);
    if (inetAddress.isAnyLocalAddress()
        || inetAddress.isLoopbackAddress()
        || inetAddress.isLinkLocalAddress()) {
      throw new Eligibilities.RequestFailed("E&E Url received is a local address.");
    }
    HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) enpointUrl.openConnection();
    httpsUrlConnection.connect();
    return httpsUrlConnection;
  }
}
