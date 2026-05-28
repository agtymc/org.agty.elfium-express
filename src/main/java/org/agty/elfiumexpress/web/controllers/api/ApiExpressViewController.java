package org.agty.elfiumexpress.web.controllers.api;

import org.agty.elfiumexpress.modules.express.common.ExpressView;
import org.agty.elfiumexpress.modules.express.entity.ExpressPanel;
import org.agty.elfiumexpress.modules.express.service.ExpressPanelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/express/view")
public class ApiExpressViewController {
    ExpressPanelService expressPanelService;

    public ApiExpressViewController(ExpressPanelService expressPanelService) {
        this.expressPanelService = expressPanelService;
    }

    @PostMapping
    public ResponseEntity<ExpressView> view(@RequestBody ExpressView expressView) {
        ExpressPanel panel = expressPanelService.getExpressPanel(expressView.getIdPanel());
        ExpressView view = ExpressView.convertFromPanel(panel);
        return ResponseEntity.ok(view);
    }
}
