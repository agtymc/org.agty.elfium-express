package org.agty.elfiumexpress.modules.express.api.controller;

import org.agty.elfiumexpress.api.answers.AnswerOk;
import org.agty.elfiumexpress.api.entity.ActionItem;
import org.agty.elfiumexpress.modules.express.service.ExpressMoveService;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<AnswerOk> move(@RequestBody ActionItem[] actionItem,
                                         @AuthenticationPrincipal UserDetailsCustom userDetails) {
        expressMoveService.move(actionItem, userDetails.getUser().getId());
        return ResponseEntity.ok(new AnswerOk());
    }
}
