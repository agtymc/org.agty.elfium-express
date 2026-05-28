package org.agty.elfiumexpress.modules.express.controller;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.modules.express.dto.ExpressGroupDto;
import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.modules.express.service.ExpressGroupService;
import org.agty.elfiumexpress.modules.express.service.ExpressPanelService;
import org.agty.elfiumexpress.modules.express.service.ExpressTypeService;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.agty.elfiumexpress.storage.service.FileUploadService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.agty.elfiumexpress.utils.ParsePage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import static org.agty.elfiumexpress.modules.express.enums.Types.*;

@Controller
@RequestMapping("/express")
public class ExpressPanelController {
    private final ExpressPanelService expressPanelService;
    private final ExpressGroupService expressGroupService;
    private final ExpressTypeService expressTypeService;
    private final FileUploadService fileUploadService;

    public ExpressPanelController(ExpressPanelService expressPanelService,
                                  ExpressGroupService expressGroupService,
                                  ExpressTypeService expressTypeService,
                                  FileUploadService fileUploadService) {
        this.expressPanelService = expressPanelService;
        this.expressGroupService = expressGroupService;
        this.expressTypeService = expressTypeService;
        this.fileUploadService = fileUploadService;
    }

    @ModelAttribute(name = "expressPanelDto")
    public ExpressPanelDto expressPanelDto() {
        return new ExpressPanelDto();
    }

    @GetMapping
    public String express(Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        return express(1L, model, userDetails);
    }

    @GetMapping("{idGroup}")
    public String express(@PathVariable Long idGroup,
                          Model model,
                          @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressGroupDto expressGroup = expressGroupService.getGroup(idGroup, idUser);
        if (expressGroup.getIdGroup() == null) {
            return "redirect:/express";
        }
        model.addAttribute("title", expressGroup.getTitle());
        model.addAttribute("nowGroup", expressGroup);
        model.addAttribute("isIndex", idGroup == 1);
        model.addAttribute("noExpress", idGroup == 1);
        model.addAttribute("panels", expressPanelService.getExpressPanels(idGroup, idUser));
        model.addAttribute("groups", expressGroupService.getGroups(idGroup, idUser));
        if (idGroup != 1) {
            model.addAttribute("fullPath", expressGroupService.fullPath(idGroup, idUser));
        }
        return "modules/express/express.index";
    }

    /**
     * Add form
     * @param panel Express Panel
     * @param group Express Group
     * @param model Model
     * @return view
     */
    private String addForm(String title, ExpressPanelDto panel, ExpressGroupDto group, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("group", group);
        model.addAttribute("expressTypes", expressTypeService.findAll());
        model.addAttribute("expressPanelDto", panel);
        return "modules/express/panels/panel.add";
    }

    @GetMapping("{idGroup}/add/")
    public String add(@PathVariable Long idGroup, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        return addForm("Add An Express Panel Item", new ExpressPanelDto(), expressGroupService.getGroup(idGroup, currentUserId(userDetails)), model);
    }

    @PostMapping("{idGroup}/add/")
    public String addAction(@PathVariable Long idGroup,
                            @Valid ExpressPanelDto expressPanel,
                            BindingResult errors,
                            Model model,
                            @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);

        checkFields(expressPanel, errors);

        if (errors.hasErrors()) {
            return addForm("Add An Express Panel Item", expressPanel, expressGroupService.getGroup(idGroup, idUser), model);
        }

        expressPanelService.saveExpressPanel(createDto(null, idGroup, expressPanel), idUser);

        return "redirect:/express/" + idGroup;
    }

    @GetMapping("edit/{idPanel}")
    public String edit(@PathVariable Long idPanel, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressPanelDto panel = expressPanelService.getExpressPanel(idPanel, idUser);
        return addForm("Edit The Express Panel Item", panel, expressGroupService.getGroup(panel.getIdGroup(), idUser), model);
    }

    @PostMapping("edit/{idPanel}")
    public String editAction(@PathVariable Long idPanel,
                             @Valid ExpressPanelDto expressPanel,
                             BindingResult errors,
                             Model model,
                             @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressPanelDto panel = expressPanelService.getExpressPanel(idPanel, idUser);

        checkFields(expressPanel, errors);

        if (errors.hasErrors()) {
            return addForm("Edit The Express Panel Item", expressPanel, expressGroupService.getGroup(panel.getIdGroup(), idUser), model);
        }

        expressPanelService.saveExpressPanel(createDto(idPanel, panel.getIdGroup(), expressPanel), idUser);

        return "redirect:/express/" + panel.getIdGroup();
    }

    /**
     * Проверка полей
     * @param expressPanel ExpressPanel
     * @param errors BindingResult
     */
    private void checkFields(ExpressPanelDto expressPanel, BindingResult errors) {

        if (expressPanel.hasUri() && !expressPanel.hasTitle()) {
            expressPanel.setTitle(new ParsePage(expressPanel.getUri()).getTitle());
        }

        if (!expressPanel.hasTitle()) {
            errors.rejectValue("title", "error.expressPanel", "The \"Title\" field cannot be empty");
        }

        if (expressPanel.emptyFieldIfExists(URI)) {
            errors.rejectValue("uri", "error.expressPanel", "The \"URI\" field cannot be empty");
        }

        if (expressPanel.emptyFieldIfExists(BOOKMARK)) {
            errors.rejectValue("body", "error.expressPanel", "The \"Body\" field cannot be empty");
        }
    }

    /**
     * Создать сущность из уже имеющейся
     * @param idPanel ID Express panel
     * @param idGroup ID Group
     * @param expressPanel Source Express Panel
     * @return ExpressPanel
     */
    private ExpressPanelDto createDto(Long idPanel, Long idGroup, ExpressPanelDto expressPanel) {
        ExpressPanelDto dto = new ExpressPanelDto();
        dto.setIdExpress(idPanel);
        dto.setIdGroup(idGroup);
        dto.setTitle(expressPanel.getTitle());
        dto.setAbout(expressPanel.getAbout());
        dto.setUri(expressPanel.getUri());
        dto.setIdType(expressPanel.getIdType());
        dto.setBody(expressPanel.getBody());
        dto.setAttachments(expressPanel.getAttachments());
        dto.setFiles(fileUploadService.save(expressPanel.getAttachments()));
        return dto;
    }

    @GetMapping("del/{idPanel}")
    public String del(@PathVariable Long idPanel, Model model, @AuthenticationPrincipal UserDetailsCustom userDetails) {
        ExpressPanelDto panel = expressPanelService.getExpressPanel(idPanel, currentUserId(userDetails));
        model.addAttribute("title", "Delete The Express Panel Item");
        model.addAttribute("panel", panel);
        return "modules/express/panels/panel.del";
    }

    @PostMapping("del/{idPanel}")
    public String delAction(@PathVariable Long idPanel,
                            SessionStatus status,
                            @AuthenticationPrincipal UserDetailsCustom userDetails) {
        Long idUser = currentUserId(userDetails);
        ExpressPanelDto panel = expressPanelService.getExpressPanel(idPanel, idUser);
        expressPanelService.del(idPanel, idUser);
        status.setComplete();
        return "redirect:/express/" + panel.getIdGroup();
    }

    private Long currentUserId(UserDetailsCustom userDetails) {
        return userDetails.getUser().getId();
    }
}
