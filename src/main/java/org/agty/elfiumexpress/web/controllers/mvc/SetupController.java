package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.modules.express.dto.ExpressGroupDto;
import org.agty.elfiumexpress.modules.express.service.ExpressGroupService;
import org.agty.elfiumexpress.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.security.service.SetupStateService;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.agty.elfiumexpress.security.service.UserServiceInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/setup")
public class SetupController {
    private static final long ROOT_GROUP_ID = 1L;

    private final UserServiceInterface userServiceInterface;
    private final SetupStateService setupStateService;
    private final ExpressGroupService expressGroupService;

    public SetupController(UserServiceInterface userServiceInterface,
                           SetupStateService setupStateService,
                           ExpressGroupService expressGroupService) {
        this.userServiceInterface = userServiceInterface;
        this.setupStateService = setupStateService;
        this.expressGroupService = expressGroupService;
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String setup(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        if (!setupStateService.isSetupRequired()) {
            return userDetails != null ? "redirect:/" : "redirect:/login";
        }

        model.addAttribute("title", "Первичная настройка");
        return "security/setup";
    }

    @PostMapping
    public String setupSave(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto userRegistrationDto,
                            BindingResult bindingResult,
                            Model model,
                            @AuthenticationPrincipal UserDetailsCustom userDetails) {
        if (!setupStateService.isSetupRequired()) {
            return userDetails != null ? "redirect:/" : "redirect:/login";
        }

        model.addAttribute("title", "Первичная настройка");
        if (bindingResult.hasErrors()) {
            return "security/setup";
        }

        try {
            Long userId = userServiceInterface.createInitialAdmin(userRegistrationDto).getId();
            expressGroupService.ensureRootGroup(userId);
            ExpressGroupDto expressGroupDto = new ExpressGroupDto();
            expressGroupDto.setRGroup(ROOT_GROUP_ID);
            expressGroupDto.setTitle("Экспресс панель");
            expressGroupService.save(expressGroupDto, userId);
            return "redirect:/login?setupComplete";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "security/setup";
        }
    }
}
