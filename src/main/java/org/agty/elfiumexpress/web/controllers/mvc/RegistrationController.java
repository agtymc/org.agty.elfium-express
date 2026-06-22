package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.security.service.SetupStateService;
import org.agty.elfiumexpress.security.service.UserServiceInterface;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final UserServiceInterface userServiceInterface;
    private final SetupStateService setupStateService;

    public RegistrationController(UserServiceInterface userServiceInterface, SetupStateService setupStateService) {
        this.userServiceInterface = userServiceInterface;
        this.setupStateService = setupStateService;
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String registration(Model model) {
        if (setupStateService.isSetupRequired()) {
            return "redirect:/setup";
        }
        if (!setupStateService.canAccessRegistration()) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Регистрация в Elfium Express");
        return "security/registration";
    }

    @PostMapping
    public String registration(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto userRegistrationDto,
                               BindingResult bindingResult,
                               Model model) {
        if (setupStateService.isSetupRequired()) {
            return "redirect:/setup";
        }
        if (!setupStateService.canAccessRegistration()) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Регистрация в Elfium Express");
        if (bindingResult.hasErrors()) {
            return "security/registration";
        }

        try {
            userServiceInterface.save(userRegistrationDto);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "security/registration";
        }
    }
}
