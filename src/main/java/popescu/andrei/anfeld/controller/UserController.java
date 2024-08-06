package popescu.andrei.anfeld.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        // getAttribute("name") gets the name from GitHub, as opposed to getName() which gets a number
        return Collections.singletonMap("name", principal.getAttribute("name").toString() + " (" + principal.getName() + ")");
    }

    @GetMapping("/logout")
    public String showRegistrationForm() {
        return "redirect:/";
    }
}
