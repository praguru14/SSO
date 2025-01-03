package com.epps.module.SSO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/epps-core")
public class SSOController {

    private static final Logger logger = LoggerFactory.getLogger(SSOController.class);

    private final Map<String, String> storedData = new HashMap<>();

    @PostMapping("/sso")
    public ResponseEntity<String> handleRequest(@RequestBody String receivedData) {
        logger.info("Received data from SAML2Controller1: {}", receivedData);
        if (receivedData == null || receivedData.trim().isEmpty()) {
            logger.error("Received empty or null data");
            return ResponseEntity.badRequest().body("Invalid data format");
        }
        storedData.put("latestData", receivedData);

        try {
            String[] sections = receivedData.split(", ");
            if (sections.length < 2) {
                logger.error("Invalid data structure: {}", receivedData);
                return ResponseEntity.status(400).body("Invalid data structure");
            }

            String status = parseSection(sections[0], "Status");
            String nameID = parseSection(sections[1], "NameID");

            if (status == null || nameID == null) {
                logger.error("Missing required data fields in: {}", receivedData);
                return ResponseEntity.status(400).body("Missing required fields");
            }

            logger.info("Parsed Status: {}, NameID: {}", status, nameID);

            // Respond with success message
            String responseMessage = "epps-core service response: Data received successfully";
            logger.info("Returning response: {}", responseMessage);
            return ResponseEntity.ok(responseMessage + " | Status: " + status + ", NameID: " + nameID);

        } catch (Exception e) {
            logger.error("Error processing data: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error while processing data");
        }
    }

    private String parseSection(String section, String key) {
        try {
            String[] keyValue = section.split(": ");
            if (keyValue.length == 2 && keyValue[0].equalsIgnoreCase(key)) {
                return keyValue[1];
            } else {
                logger.warn("Invalid section for key {}: {}", key, section);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error parsing section: {}", section, e);
            return null;
        }
    }
}
