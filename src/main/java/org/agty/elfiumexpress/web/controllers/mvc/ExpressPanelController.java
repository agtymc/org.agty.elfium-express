package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.modules.express.entity.ExpressPanel;
import org.agty.elfiumexpress.modules.express.entity.ExpressGroup;
import org.agty.elfiumexpress.modules.express.service.ExpressGroupService;
import org.agty.elfiumexpress.modules.express.service.ExpressPanelService;
import org.agty.elfiumexpress.modules.express.service.ExpressTypeService;
import org.agty.elfiumexpress.storage.service.FileUploadService;
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

    @ModelAttribute(name = "expressPanel")
    public ExpressPanel addExpress() {
        return new ExpressPanel();
    }

    @GetMapping
    public String express(Model model) {
        return express(1L, model);
    }

    @GetMapping("{idGroup}")
    public String express(@PathVariable Long idGroup, Model model) {
        ExpressGroup expressGroup = expressGroupService.getGroup(idGroup);
        model.addAttribute("title", expressGroup.getTitle());
        model.addAttribute("nowGroup", expressGroup);
        model.addAttribute("isIndex", idGroup == 1);
        model.addAttribute("noExpress", idGroup == 1);
        model.addAttribute("panels", expressPanelService.getExpressPanels(idGroup));
        model.addAttribute("groups", expressGroupService.getGroups(idGroup));
        if (idGroup != 1) {
            model.addAttribute("fullPath", expressGroupService.fullPath(idGroup));
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
    private String addForm(String title, ExpressPanel panel, ExpressGroup group, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("group", group);
        model.addAttribute("expressTypes", expressTypeService.findAll());
        model.addAttribute("expressPanel", panel);
        return "modules/express/panels/panel.add";
    }

    @GetMapping("{idGroup}/add/")
    public String add(@PathVariable Long idGroup, Model model) {
        return addForm("Add An Express Panel Item", new ExpressPanel(), expressGroupService.getGroup(idGroup), model);
    }

    @PostMapping("{idGroup}/add/")
    public String addAction(@PathVariable Long idGroup, @Valid ExpressPanel expressPanel, BindingResult errors, Model model) {

        checkFields(expressPanel, errors);

        if (errors.hasErrors()) {
            return addForm("Add An Express Panel Item", expressPanel, expressGroupService.getGroup(idGroup), model);
        }

        expressPanelService.saveExpressPanel( createEntity(null, idGroup, expressPanel) );

        return "redirect:/express/" + idGroup;
    }

    @GetMapping("edit/{idPanel}")
    public String edit(@PathVariable Long idPanel, Model model) {
        ExpressPanel panel = expressPanelService.getExpressPanel(idPanel);
        return addForm("Edit The Express Panel Item", panel, expressGroupService.getGroup(panel.getIdGroup()), model);
    }

    @PostMapping("edit/{idPanel}")
    public String editAction(@PathVariable Long idPanel, @Valid ExpressPanel expressPanel, BindingResult errors, Model model) {
        ExpressPanel panel = expressPanelService.getExpressPanel(idPanel);

        checkFields(expressPanel, errors);

        if (errors.hasErrors()) {
            return addForm("Edit The Express Panel Item", expressPanel, expressGroupService.getGroup(panel.getIdGroup()), model);
        }

        expressPanelService.saveExpressPanel( createEntity(idPanel, panel.getIdGroup(), expressPanel) );

        return "redirect:/express/" + panel.getIdGroup();
    }

    /**
     * Проверка полей
     * @param expressPanel ExpressPanel
     * @param errors BindingResult
     */
    private void checkFields(ExpressPanel expressPanel, BindingResult errors) {

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
    private ExpressPanel createEntity(Long idPanel, Long idGroup, ExpressPanel expressPanel) {
        ExpressPanel entityExpressPanel = new ExpressPanel();
        entityExpressPanel.setIdExpress(idPanel);
        entityExpressPanel.setIdGroup(idGroup);
        entityExpressPanel.setTitle(expressPanel.getTitle());
        entityExpressPanel.setAbout(expressPanel.getAbout());
        entityExpressPanel.setUri(expressPanel.getUri());
        entityExpressPanel.setIdType(expressPanel.getIdType());
        entityExpressPanel.setBody(expressPanel.getBody());
        entityExpressPanel.setAttachments(expressPanel.getAttachments());
        entityExpressPanel.setFiles(fileUploadService.save(expressPanel.getAttachments()));
        return entityExpressPanel;
    }

    @GetMapping("del/{idPanel}")
    public String del(@PathVariable Long idPanel, Model model) {
        ExpressPanel panel = expressPanelService.getExpressPanel(idPanel);
        model.addAttribute("title", "Delete The Express Panel Item");
        model.addAttribute("panel", panel);
        return "modules/express/panels/panel.del";
    }

    @PostMapping("del/{idPanel}")
    public String delAction(@PathVariable Long idPanel, SessionStatus status) {
        ExpressPanel panel = expressPanelService.getExpressPanel(idPanel);
        expressPanelService.del(idPanel);
        status.setComplete();
        return "redirect:/express/" + panel.getIdGroup();
    }
}
