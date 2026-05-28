package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.modules.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.modules.security.service.UserServiceInterface;
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

    public RegistrationController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String registration(Model model) {
        model.addAttribute("title", "Регистрация в Elfium Express");
        return "security/registration";
    }

    @PostMapping
    public String registration(@Valid @ModelAttribute("user") UserRegistrationDto userRegistrationDto,
                               BindingResult bindingResult,
                               Model model) {
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
