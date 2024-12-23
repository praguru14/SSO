package org.pg.ssosaml.Controller;

import org.pg.ssosaml.Utility.CaptureParams;
import org.pg.ssosaml.cConfigurations.SamlNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.Base64;


@RestController
@RequestMapping("/saml2")
public class SAML2Controller1 {

    private static final Logger logger = LoggerFactory.getLogger(SAML2Controller1.class);
    @Autowired
   private CaptureParams captureParams;

    @PostMapping("/authenticate")
    public ResponseEntity<String> acs(HttpServletRequest request) {
        String samlResponse = request.getParameter("SAMLResponse");
        if (samlResponse == null || samlResponse.isEmpty()) {
            logger.error("SAMLResponse parameter not found in the request");
            return ResponseEntity.badRequest().body("SAMLResponse parameter missing");
        }

        logger.info("SAML Response received: {}", samlResponse);
        try {
            // Decode the SAML response
            byte[] decodedBytes = Base64.getDecoder().decode(samlResponse);
            String decodedXML = new String(decodedBytes);
            logger.info("Decoded SAML XML: {}", decodedXML);

            // Parse the XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(decodedBytes));

            logger.info("SAML Response successfully parsed into XML format.");

            // Initialize XPath
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new SamlNamespaceContext());

            // Extract StatusCode
            String statusCodeExpression = "//saml2p:Status/saml2p:StatusCode/@Value";
            String statusCode = (String) xpath.evaluate(statusCodeExpression, document, XPathConstants.STRING);
            String status="";
            if (statusCode != null && !statusCode.isEmpty()) {
                logger.info("Extracted StatusCode: {}", statusCode);
                 status = statusCode.substring(statusCode.lastIndexOf(':') + 1);
                logger.error("Status of the saml response is: {}", status);
            } else {
                logger.error("StatusCode not found in SAML Response");
            }

            // Extract NameID
            String nameIdExpression = "//saml2:NameID";
            NodeList nameIdNodes = (NodeList) xpath.evaluate(nameIdExpression, document, XPathConstants.NODESET);

            if (nameIdNodes.getLength() > 0) {
                String nameIdValue = nameIdNodes.item(0).getTextContent();
                logger.info("Extracted NameID value: {}", nameIdValue);
                String format = nameIdNodes.item(0).getAttributes().getNamedItem("Format").getNodeValue();
                logger.info("Extracted Format attribute: {}", format);
                captureParams.params(status,nameIdValue);
                return ResponseEntity.ok(status +" "+nameIdValue);
            } else {
                logger.error("NameID element not found in SAML Response");
                return ResponseEntity.status(404).body("NameID element not found");
            }

        } catch (Exception e) {
            logger.error("Error decoding or parsing SAMLResponse: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error decoding SAMLResponse");
        }

    }


}
