package org.pg.ssosaml.cConfigurations;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SamlNamespaceContext implements NamespaceContext {

    private static final Map<String, String> NAMESPACES = new HashMap<>();

    static {
        NAMESPACES.put("saml2", "urn:oasis:names:tc:SAML:2.0:assertion");
        NAMESPACES.put("saml2p", "urn:oasis:names:tc:SAML:2.0:protocol");
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return NAMESPACES.getOrDefault(prefix, null);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> entry : NAMESPACES.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return NAMESPACES.keySet().iterator();
    }
}
