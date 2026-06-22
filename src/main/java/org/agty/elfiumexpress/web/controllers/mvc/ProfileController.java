package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.security.dto.UserProfileDto;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.agty.elfiumexpress.security.service.UserServiceInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserServiceInterface userServiceInterface;

    public ProfileController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    @ModelAttribute("userProfileDto")
    public UserProfileDto userProfileDto() {
        return new UserProfileDto();
    }

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        UserProfileDto userProfileDto = userServiceInterface.getProfileById(userDetails.getUser().getId());
        fillModel(model, userProfileDto, userDetails);
        return "security/profile";
    }

    @PostMapping
    public String profileSave(@Valid @ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
                              BindingResult bindingResult,
                              Model model,
                              @AuthenticationPrincipal UserDetailsCustom userDetails) {
        userProfileDto.setId(userDetails.getUser().getId());

        if (bindingResult.hasErrors()) {
            fillModel(model, userProfileDto, userDetails);
            return "security/profile";
        }

        try {
            userServiceInterface.saveProfile(userProfileDto);
            return "redirect:/profile?saved";
        } catch (IllegalArgumentException e) {
            fillModel(model, userProfileDto, userDetails);
            model.addAttribute("profileError", e.getMessage());
            return "security/profile";
        }
    }

    private void fillModel(Model model, UserProfileDto userProfileDto, UserDetailsCustom userDetails) {
        model.addAttribute("title", "Профиль пользователя");
        model.addAttribute("isAdmin", userDetails.hasRole("ROLE_ADMIN"));
        model.addAttribute("userProfileDto", userProfileDto);
    }
}
