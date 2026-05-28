package org.agty.elfiumexpress.web.controllers.mvc;

import jakarta.validation.Valid;
import org.agty.elfiumexpress.modules.express.entity.ExpressGroup;
import org.agty.elfiumexpress.modules.express.service.ExpressGroupService;
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

    @ModelAttribute(name = "expressGroup")
    public ExpressGroup addGroup() {
        return new ExpressGroup();
    }

    /**
     * Add form
     * @param title Title
     * @param expressGroup Express Group
     * @param rootGroup Express Root Group
     * @param model Model
     * @return view
     */
    private String addForm(String title, ExpressGroup expressGroup, ExpressGroup rootGroup, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("rootGroup", rootGroup);
        model.addAttribute("expressGroup", expressGroup);
        return "modules/express/groups/groups.add";
    }

    @GetMapping("{idRootGroup}/add/")
    public String add(@PathVariable Long idRootGroup, Model model) {
        return addForm("Add A Group", new ExpressGroup(), expressGroupService.getGroup(idRootGroup), model);
    }

    @PostMapping("{idRootGroup}/add/")
    public String addAction(@PathVariable Long idRootGroup, @Valid ExpressGroup expressGroup, Errors errors, Model model) {
        ExpressGroup rootGroup = expressGroupService.getGroup(idRootGroup);

        if (errors.hasErrors()) {
            return addForm("Add A Group", expressGroup, rootGroup, model);
        }

        ExpressGroup entityExpressGroup = new ExpressGroup();
        entityExpressGroup.setRGroup(idRootGroup);
        entityExpressGroup.setTitle(expressGroup.getTitle());
        entityExpressGroup.setComment(expressGroup.getComment());

        expressGroupService.save(entityExpressGroup);

        return "redirect:/express/" + idRootGroup;
    }

    @GetMapping("edit/{idGroup}")
    public String edit(@PathVariable Long idGroup, Model model) {
        ExpressGroup expressGroup = expressGroupService.getGroup(idGroup);
        ExpressGroup rootGroup = expressGroupService.getGroup(expressGroup.getRGroup());
        return addForm("Edit The Group", expressGroup, rootGroup, model);
    }

    @PostMapping("edit/{idGroup}")
    public String editAction(@PathVariable Long idGroup, @Valid ExpressGroup expressGroup, Errors errors, Model model) {
        ExpressGroup group = expressGroupService.getGroup(idGroup);

        if (errors.hasErrors()) {
            return addForm("Edit The Group", expressGroup, expressGroupService.getGroup(group.getRGroup()), model);
        }

        ExpressGroup entityExpressGroup = new ExpressGroup();
        entityExpressGroup.setIdGroup(idGroup);
        entityExpressGroup.setRGroup(group.getRGroup());
        entityExpressGroup.setTitle(expressGroup.getTitle());
        entityExpressGroup.setComment(expressGroup.getComment());

        expressGroupService.save(entityExpressGroup);

        return "redirect:/express/" + group.getRGroup();
    }

    @GetMapping("del/{idGroup}")
    public String del(@PathVariable Long idGroup, Model model) {
        ExpressGroup expressGroup = expressGroupService.getGroup(idGroup);
        model.addAttribute("title", "Delete The Group");
        model.addAttribute("group", expressGroup);
        model.addAttribute("rootGroup", expressGroupService.getGroup(expressGroup.getRGroup()));
        return "modules/express/groups/groups.del";
    }

    @PostMapping("del/{idGroup}")
    public String delAction(@PathVariable Long idGroup, SessionStatus status) {
        ExpressGroup expressGroup = expressGroupService.getGroup(idGroup);
        expressGroupService.del(idGroup);
        status.setComplete();
        return "redirect:/express/" + expressGroup.getRGroup();
    }
}
