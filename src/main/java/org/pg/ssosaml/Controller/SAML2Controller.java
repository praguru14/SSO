package org.pg.ssosaml.Controller;


import org.pg.ssosaml.Utility.SamlResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/saml2")
public class SAML2Controller {

    private static final Logger logger = LoggerFactory.getLogger(SAML2Controller.class);

    @PostMapping("/authenticate")
    public ResponseEntity<String> acs(HttpServletRequest request) {
        String samlResponse = request.getParameter("SAMLResponse");
        if (samlResponse == null || samlResponse.isEmpty()) {
            logger.error("SAMLResponse parameter not found in the request");
            return ResponseEntity.badRequest().body("SAMLResponse parameter missing");
        }

        logger.info("SAML Response received: {}", samlResponse);
        String nameIdValue = SamlResponseParser.extractNameID(samlResponse);

        if (nameIdValue != null) {
            logger.info("Extracted NameID value: {}", nameIdValue);
            return ResponseEntity.ok("NameID: " + nameIdValue);
        } else {
            return ResponseEntity.status(404).body("NameID element not found");
        }
    }
}
