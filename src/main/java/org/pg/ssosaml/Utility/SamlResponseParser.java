package org.pg.ssosaml.Utility;



import org.pg.ssosaml.Configurations.SamlNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.Base64;

public class SamlResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(SamlResponseParser.class);

    public static String extractNameID(String samlResponse) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(samlResponse);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(decodedBytes));

            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new SamlNamespaceContext());
            String expression = "//saml2:NameID";
            NodeList nameIdNodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            if (nameIdNodes.getLength() > 0) {
                return nameIdNodes.item(0).getTextContent();
            } else {
                logger.error("NameID element not found in SAML Response");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error parsing SAMLResponse: {}", e.getMessage());
            return null;
        }
    }
}

