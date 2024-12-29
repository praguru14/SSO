package com.epps.module.SSO;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SSOservice {
    private String status;
    private String nameID;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNameID() {
        return nameID;
    }

    public void setNameID(String nameID) {
        this.nameID = nameID;
    }
}

