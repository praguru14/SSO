package org.pg.ssosaml.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String redirectToIdp() {
        return "redirect:/saml2/authenticate/idptwo";
    }
}

