package org.pg.ssosaml.Controller;

import org.pg.ssosaml.Configurations.SamlNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;
@RestController
@RequestMapping("/saml2")
public class SAML2Controller1 {

    private static final Logger logger = LoggerFactory.getLogger(SAML2Controller1.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/ipAddress")
    public ResponseEntity<?> getIpAddress() {
        String ipAddress = "";
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
            logger.info("IP Address: {}", ipAddress);
        } catch (UnknownHostException e) {
            logger.error("Unable to retrieve IP address: {}", e.getMessage());
        }
        return ResponseEntity.ok(ipAddress);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> acs(HttpServletRequest request) {
        String samlResponse = request.getParameter("SAMLResponse");
        if (samlResponse == null || samlResponse.isEmpty()) {
            logger.error("SAMLResponse parameter not found in the request");
            return ResponseEntity.badRequest().body("SAMLResponse parameter missing");
        }

        logger.info("SAML Response received: {}", samlResponse);

        try {
            String decodedXML = decodeSAMLResponse(samlResponse);
            Document document = parseXML(decodedXML);

            String status = extractStatusCode(document);
            String nameIdValue = extractNameID(document);

            if (nameIdValue != null && !nameIdValue.isEmpty()) {
                String finalResponse = constructFinalResponse(status, nameIdValue);
                String responseFromCore = sendToEppsCore(finalResponse);

                logger.info("Received response from epps-core: {}", responseFromCore);
                return ResponseEntity.ok(finalResponse + ", Core Response: " + responseFromCore);
            } else {
                logger.error("NameID element not found in SAML Response");
                return ResponseEntity.status(404).body("NameID element not found");
            }

        } catch (Exception e) {
            logger.error("Error processing SAMLResponse: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error processing SAMLResponse");
        }
    }

    private String decodeSAMLResponse(String samlResponse) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(samlResponse);
        String decodedXML = new String(decodedBytes);
        logger.info("Decoded SAML XML: {}", decodedXML);
        return decodedXML;
    }

    private Document parseXML(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));
        logger.info("SAML Response successfully parsed into XML format.");
        return document;
    }

    private String extractStatusCode(Document document) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new SamlNamespaceContext());
        String statusCodeExpression = "//saml2p:Status/saml2p:StatusCode/@Value";
        String statusCode = (String) xpath.evaluate(statusCodeExpression, document, XPathConstants.STRING);

        if (statusCode != null && !statusCode.isEmpty()) {
            String status = statusCode.substring(statusCode.lastIndexOf(':') + 1);
            logger.info("Extracted Status: {}", status);
            return status;
        } else {
            logger.error("StatusCode not found in SAML Response");
            return null;
        }
    }

    private String extractNameID(Document document) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new SamlNamespaceContext());
        String nameIdExpression = "//saml2:NameID";
        NodeList nameIdNodes = (NodeList) xpath.evaluate(nameIdExpression, document, XPathConstants.NODESET);

        if (nameIdNodes.getLength() > 0) {
            String nameIdValue = nameIdNodes.item(0).getTextContent();
            logger.info("Extracted NameID: {}", nameIdValue);
            return nameIdValue;
        } else {
            return null;
        }
    }

    private String constructFinalResponse(String status, String nameIdValue) {
        return String.format("Status: %s, NameID: %s", status, nameIdValue);
    }

    private String sendToEppsCore(String finalResponse) {
        String ipAddress = "";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
            logger.info("IP Address: {}", ipAddress);
        } catch (UnknownHostException e) {
            logger.error("Unable to retrieve IP address: {}", e.getMessage());
        }
        String eppsCoreUrl = "http://"+ipAddress+":10082/epps-core/userManagement/sso";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(finalResponse, headers);
            return restTemplate.postForObject(eppsCoreUrl, requestEntity, String.class);
        } catch (Exception e) {
            logger.error("Error calling epps-core service: {}", e.getMessage(), e);
            return "Error calling epps-core service";
        }
    }
}
