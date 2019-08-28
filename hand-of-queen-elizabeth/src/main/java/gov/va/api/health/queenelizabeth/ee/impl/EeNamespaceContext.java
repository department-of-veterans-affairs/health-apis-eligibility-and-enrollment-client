package gov.va.api.health.queenelizabeth.ee.impl;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import lombok.RequiredArgsConstructor;

/**
 * Namespace context for EE to provide a default target namespace obtained from the wsdl schema so
 * that xpath identifies elements.
 */
@RequiredArgsConstructor
public class EeNamespaceContext implements NamespaceContext {

  private final String namespace;

  @Override
  public String getNamespaceURI(String prefix) {
    if ("ns".equals(prefix)) {
      return namespace;
    }
    return null;
  }

  @Override
  public String getPrefix(String namespaceURI) {
    return null;
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    return null;
  }
}
