package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.security.dto.AdminUserDto;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.agty.elfiumexpress.security.service.UserServiceInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceInterface userServiceInterface;

    public AdminController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    @ModelAttribute("adminUserDto")
    public AdminUserDto adminUserDto() {
        return new AdminUserDto();
    }

    @GetMapping
    public String dashboard(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        List<UserDto> users = userServiceInterface.getAll();
        fillBaseModel(model, "dashboard", "Администрирование", userDetails);
        model.addAttribute("usersCount", users.size());
        model.addAttribute("adminsCount", users.stream().filter(user -> user.hasRole("ROLE_ADMIN")).count());
        model.addAttribute("regularUsersCount", users.stream().filter(user -> user.hasRole("ROLE_USER")).count());
        model.addAttribute("rolesCount", userServiceInterface.getRoles().size());
        model.addAttribute("recentUsers", users.stream().limit(8).toList());
        return "admin/index";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Long roleId,
                        Model model,
                        @AuthenticationPrincipal UserDetailsCustom userDetails) {
        List<UserDto> users = userServiceInterface.getAll().stream()
                .filter(user -> matchesQuery(user, q))
                .filter(user -> matchesRole(user, roleId))
                .toList();
        fillBaseModel(model, "users", "Пользователи", userDetails);
        model.addAttribute("users", users);
        model.addAttribute("roleOptions", userServiceInterface.getRoles());
        model.addAttribute("query", q);
        model.addAttribute("selectedRoleId", roleId);
        return "admin/users";
    }

    @GetMapping("/users/add")
    public String addUser(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        fillEditModel(model, new AdminUserDto(), userDetails, "Создание пользователя", "/admin/users/add", true);
        return "admin/user.edit";
    }

    @PostMapping("/users/add")
    public String addUserSave(@Valid @ModelAttribute("adminUserDto") AdminUserDto adminUserDto,
                              BindingResult bindingResult,
                              Model model,
                              @AuthenticationPrincipal UserDetailsCustom userDetails) {
        if (bindingResult.hasErrors()) {
            fillEditModel(model, adminUserDto, userDetails, "Создание пользователя", "/admin/users/add", true);
            return "admin/user.edit";
        }

        try {
            UserDto createdUser = userServiceInterface.getById(userServiceInterface.createAdminUser(adminUserDto).getId());
            return "redirect:/admin/users/" + createdUser.getId() + "?created";
        } catch (IllegalArgumentException e) {
            fillEditModel(model, adminUserDto, userDetails, "Создание пользователя", "/admin/users/add", true);
            model.addAttribute("adminUserError", e.getMessage());
            return "admin/user.edit";
        }
    }

    @GetMapping("/users/{idUser}")
    public String editUser(@PathVariable Long idUser, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        AdminUserDto adminUserDto = userServiceInterface.getAdminUserById(idUser);
        if (adminUserDto == null) {
            return "redirect:/admin/users";
        }
        fillEditModel(model, adminUserDto, userDetails, "Редактирование пользователя", "/admin/users/" + idUser, false);
        return "admin/user.edit";
    }

    @PostMapping("/users/{idUser}")
    public String editUserSave(@PathVariable Long idUser,
                               @Valid @ModelAttribute("adminUserDto") AdminUserDto adminUserDto,
                               BindingResult bindingResult,
                               Model model,
                               @AuthenticationPrincipal UserDetailsCustom userDetails) {
        adminUserDto.setId(idUser);

        if (bindingResult.hasErrors()) {
            fillEditModel(model, adminUserDto, userDetails, "Редактирование пользователя", "/admin/users/" + idUser, false);
            return "admin/user.edit";
        }

        try {
            userServiceInterface.saveAdminUser(adminUserDto);
            return "redirect:/admin/users/" + idUser + "?saved";
        } catch (IllegalArgumentException e) {
            fillEditModel(model, adminUserDto, userDetails, "Редактирование пользователя", "/admin/users/" + idUser, false);
            model.addAttribute("adminUserError", e.getMessage());
            return "admin/user.edit";
        }
    }

    private void fillBaseModel(Model model, String section, String title, UserDetailsCustom userDetails) {
        model.addAttribute("title", title);
        model.addAttribute("adminSection", section);
        model.addAttribute("currentUser", userServiceInterface.getById(userDetails.getUser().getId()));
    }

    private void fillEditModel(Model model,
                               AdminUserDto adminUserDto,
                               UserDetailsCustom userDetails,
                               String title,
                               String actionUrl,
                               boolean createMode) {
        fillBaseModel(model, "users", title, userDetails);
        model.addAttribute("roleOptions", userServiceInterface.getRoles());
        model.addAttribute("adminUserDto", adminUserDto);
        model.addAttribute("actionUrl", actionUrl);
        model.addAttribute("createMode", createMode);
    }

    private boolean matchesQuery(UserDto user, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String normalized = query.trim().toLowerCase();
        return (user.getDisplayName() != null && user.getDisplayName().toLowerCase().contains(normalized))
                || (user.getEmail() != null && user.getEmail().toLowerCase().contains(normalized));
    }

    private boolean matchesRole(UserDto user, Long roleId) {
        if (roleId == null) {
            return true;
        }
        return user.getRoles() != null && user.getRoles().stream().anyMatch(role -> roleId.equals(role.getId()));
    }
}
