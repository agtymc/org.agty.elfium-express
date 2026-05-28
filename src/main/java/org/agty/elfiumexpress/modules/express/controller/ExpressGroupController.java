package org.agty.elfiumexpress.modules.express.controller;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.modules.express.dto.ExpressGroupDto;
import org.agty.elfiumexpress.modules.express.service.ExpressGroupService;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/express/groups")
public class ExpressGroupController {
    private final ExpressGroupService expressGroupService;

    public ExpressGroupController(ExpressGroupService expressGroupService) {
        this.expressGroupService = expressGroupService;
    }

    @ModelAttribute(name = "expressGroupDto")
    public ExpressGroupDto expressGroupDto() {
        return new ExpressGroupDto();
    }

    /**
     * Add form
     * @param title Title
     * @param expressGroup Express Group
     * @param rootGroup Express Root Group
     * @param model Model
     * @return view
     */
    private String addForm(String title, ExpressGroupDto expressGroup, ExpressGroupDto rootGroup, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("rootGroup", rootGroup);
        model.addAttribute("expressGroupDto", expressGroup);
        return "modules/express/groups/groups.add";
    }

    @GetMapping("{idRootGroup}/add/")
    public String add(@PathVariable Long idRootGroup, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        return addForm("Add A Group", new ExpressGroupDto(), expressGroupService.getGroup(idRootGroup, currentUserId(userDetails)), model);
    }

    @PostMapping("{idRootGroup}/add/")
    public String addAction(@PathVariable Long idRootGroup,
                            @Valid ExpressGroupDto expressGroup,
                            Errors errors,
                            Model model,
                            @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressGroupDto rootGroup = expressGroupService.getGroup(idRootGroup, idUser);

        if (errors.hasErrors()) {
            return addForm("Add A Group", expressGroup, rootGroup, model);
        }

        ExpressGroupDto dto = new ExpressGroupDto();
        dto.setRGroup(idRootGroup);
        dto.setTitle(expressGroup.getTitle());
        dto.setComment(expressGroup.getComment());
        expressGroupService.save(dto, idUser);

        return "redirect:/express/" + idRootGroup;
    }

    @GetMapping("edit/{idGroup}")
    public String edit(@PathVariable Long idGroup, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressGroupDto expressGroup = expressGroupService.getGroup(idGroup, idUser);
        ExpressGroupDto rootGroup = expressGroupService.getGroup(expressGroup.getRGroup(), idUser);
        return addForm("Edit The Group", expressGroup, rootGroup, model);
    }

    @PostMapping("edit/{idGroup}")
    public String editAction(@PathVariable Long idGroup,
                             @Valid ExpressGroupDto expressGroup,
                             Errors errors,
                             Model model,
                             @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressGroupDto group = expressGroupService.getGroup(idGroup, idUser);

        if (errors.hasErrors()) {
            return addForm("Edit The Group", expressGroup, expressGroupService.getGroup(group.getRGroup(), idUser), model);
        }

        ExpressGroupDto dto = new ExpressGroupDto();
        dto.setIdGroup(idGroup);
        dto.setRGroup(group.getRGroup());
        dto.setTitle(expressGroup.getTitle());
        dto.setComment(expressGroup.getComment());
        expressGroupService.save(dto, idUser);

        return "redirect:/express/" + group.getRGroup();
    }

    @GetMapping("del/{idGroup}")
    public String del(@PathVariable Long idGroup, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressGroupDto expressGroup = expressGroupService.getGroup(idGroup, idUser);
        model.addAttribute("title", "Delete The Group");
        model.addAttribute("group", expressGroup);
        model.addAttribute("rootGroup", expressGroupService.getGroup(expressGroup.getRGroup(), idUser));
        return "modules/express/groups/groups.del";
    }

    @PostMapping("del/{idGroup}")
    public String delAction(@PathVariable Long idGroup,
                            SessionStatus status,
                            @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressGroupDto expressGroup = expressGroupService.getGroup(idGroup, idUser);
        expressGroupService.del(idGroup, idUser);
        status.setComplete();
        return "redirect:/express/" + expressGroup.getRGroup();
    }

    private Long currentUserId(UserDetailsCustom userDetails) {
        return userDetails.getUser().getId();
    }
}
