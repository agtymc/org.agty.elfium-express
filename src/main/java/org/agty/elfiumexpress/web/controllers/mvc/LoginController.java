package org.agty.elfiumexpress.web.controllers.mvc;

import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.agty.elfiumexpress.security.service.SetupStateService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final SetupStateService setupStateService;

    public LoginController(SetupStateService setupStateService) {
        this.setupStateService = setupStateService;
    }

    @GetMapping
    public String login(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        if (userDetails != null) {
            return "redirect:/";
        }
        if (setupStateService.isSetupRequired()) {
            return "redirect:/setup";
        }
        model.addAttribute("title", "Вход в Elfium Express");
        model.addAttribute("publicRegistrationAllowed", setupStateService.canAccessRegistration());
        return "security/login";
    }
}
