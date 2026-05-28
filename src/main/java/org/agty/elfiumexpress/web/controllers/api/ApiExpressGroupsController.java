package org.agty.elfiumexpress.web.controllers.api;

import org.agty.elfiumexpress.api.answers.AnswerOk;
import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.modules.express.service.ExpressGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//HttpEntity<String> httpEntity - все заголовок
@RestController
@RequestMapping("/api/v1/express/groups")
public class ApiExpressGroupsController {
    ExpressGroupService expressGroupService;

    public ApiExpressGroupsController(ExpressGroupService expressGroupService) {
        this.expressGroupService = expressGroupService;
    }

    @PostMapping("sort")
    public ResponseEntity<AnswerOk> sort(@RequestBody SortBody[] sortBodies) {
        expressGroupService.sort(sortBodies);
        return ResponseEntity.ok(new AnswerOk());
    }
}
