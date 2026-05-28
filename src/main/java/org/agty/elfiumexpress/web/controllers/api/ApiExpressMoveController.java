package org.agty.elfiumexpress.web.controllers.api;

import org.agty.elfiumexpress.api.answers.AnswerOk;
import org.agty.elfiumexpress.api.entity.ActionItem;
import org.agty.elfiumexpress.modules.express.service.ExpressMoveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//HttpEntity<String> httpEntity - все заголовок
@RestController
@RequestMapping("/api/v1/express/move")
public class ApiExpressMoveController {
    ExpressMoveService expressMoveService;

    public ApiExpressMoveController(ExpressMoveService expressMoveService) {
        this.expressMoveService = expressMoveService;
    }

    @PostMapping
    public ResponseEntity<AnswerOk> move(@RequestBody ActionItem[] actionItem) {
        expressMoveService.move(actionItem);
        return ResponseEntity.ok(new AnswerOk());
    }
}
