package org.pg.ssosaml.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CaptureParams {

    private static final Logger logger = LoggerFactory.getLogger(CaptureParams.class);

    public void params(String status, String nameId) {
        logger.info("Captured Parameters - Status: {}, NameID: {}", status, nameId);
        System.out.println(status +" "+nameId);
        // Additional logic for handling the status and nameId can be implemented here.
        // For example, storing in a database, forwarding to another service, etc.
    }
}
