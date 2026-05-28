package org.agty.elfiumexpress.modules.express.api.controller;

import org.agty.elfiumexpress.modules.express.common.ExpressView;
import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.modules.express.service.ExpressPanelService;
import org.agty.elfiumexpress.security.service.UserDetailsCustom;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/express/view")
public class ApiExpressViewController {
    ExpressPanelService expressPanelService;

    public ApiExpressViewController(ExpressPanelService expressPanelService) {
        this.expressPanelService = expressPanelService;
    }

    @GetMapping("/{idPanel}")
    public ResponseEntity<ExpressView> view(@PathVariable long idPanel,
                                            @AuthenticationPrincipal UserDetailsCustom userDetails) {
        ExpressPanelDto panel = expressPanelService.getExpressPanel(idPanel, userDetails.getUser().getId());
        ExpressView view = ExpressView.convertFromPanel(panel);
        return ResponseEntity.ok(view);
    }
}
