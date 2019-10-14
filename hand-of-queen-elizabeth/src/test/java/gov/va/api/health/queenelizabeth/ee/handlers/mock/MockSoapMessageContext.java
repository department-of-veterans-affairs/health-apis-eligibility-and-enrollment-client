package gov.va.api.health.queenelizabeth.ee.handlers.mock;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import lombok.RequiredArgsConstructor;

/** Mock class to forcefully construct a SOAPMessageContext. */
@RequiredArgsConstructor
public class MockSoapMessageContext implements SOAPMessageContext {

  private final SOAPMessage soapMessage;

  @Override
  public void clear() {}

  @Override
  public boolean containsKey(Object key) {
    return false;
  }

  @Override
  public boolean containsValue(Object value) {
    return false;
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    return false;
  }

  @Override
  public Object get(Object key) {
    return null;
  }

  @Override
  public Object[] getHeaders(QName header, JAXBContext context, boolean allRoles) {
    return new Object[0];
  }

  @Override
  public SOAPMessage getMessage() {
    return soapMessage;
  }

  @Override
  public void setMessage(SOAPMessage message) {}

  @Override
  public Set<String> getRoles() {
    return null;
  }

  @Override
  public Scope getScope(String name) {
    return null;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Set<String> keySet() {
    return null;
  }

  @Override
  public Object put(String key, Object value) {
    return null;
  }

  @Override
  public void putAll(Map<? extends String, ?> m) {}

  @Override
  public Object remove(Object key) {
    return null;
  }

  @Override
  public void setScope(String name, Scope scope) {}

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Collection<Object> values() {
    return null;
  }
}
