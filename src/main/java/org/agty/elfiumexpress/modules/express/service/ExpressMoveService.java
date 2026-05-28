package org.agty.elfiumexpress.modules.express.service;

import org.agty.elfiumexpress.api.entity.ActionItem;
import org.agty.elfiumexpress.modules.express.repository.ExpressGroupRepository;
import org.agty.elfiumexpress.modules.express.repository.ExpressPanelRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpressMoveService {
    private final ExpressPanelRepository expressPanelRepository;
    private final ExpressGroupRepository expressGroupRepository;

    public ExpressMoveService(ExpressPanelRepository expressPanelRepository,
                              ExpressGroupRepository expressGroupRepository) {
        this.expressPanelRepository = expressPanelRepository;
        this.expressGroupRepository = expressGroupRepository;
    }

    public void move(ActionItem[] actionItems, Long idUser) {
        for (ActionItem actionItem : actionItems) {
            if (actionItem == null) continue;

            if ("panel".equals(actionItem.getObject())) {
                expressPanelRepository.move(actionItem, idUser);
            }

            if ("group".equals(actionItem.getObject())) {
                expressGroupRepository.move(actionItem, idUser);
            }
        }
    }
}
