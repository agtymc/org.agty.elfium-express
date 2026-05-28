package org.agty.elfiumexpress.modules.security.controllers;

import org.agty.elfiumexpress.modules.security.service.UserDetailsCustom;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String login(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        if (userDetails != null) {
            return "redirect:/";
        }
        model.addAttribute("title", "Вход в Elfium Express");
        return "security/login";
    }
}
