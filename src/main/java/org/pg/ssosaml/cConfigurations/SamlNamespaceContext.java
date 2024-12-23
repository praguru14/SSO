package org.pg.ssosaml.cConfigurations;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class SamlNamespaceContext implements NamespaceContext {
    @Override
    public String getNamespaceURI(String prefix) {
        if ("saml2".equals(prefix)) {
            return "urn:oasis:names:tc:SAML:2.0:assertion"; // Update this to match the actual SAML namespace
        }
        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
