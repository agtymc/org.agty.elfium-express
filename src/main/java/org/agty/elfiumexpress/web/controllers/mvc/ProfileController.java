package org.agty.elfiumexpress.web.controllers.mvc;

import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.agty.elfiumexpress.security.service.UserServiceInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserServiceInterface userServiceInterface;

    public ProfileController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        UserDto userDto = userServiceInterface.getById(userDetails.getUser().getId());
        model.addAttribute("title", "Профиль пользователя");
        model.addAttribute("userDto", userDto);
        return "security/profile";
    }
}
