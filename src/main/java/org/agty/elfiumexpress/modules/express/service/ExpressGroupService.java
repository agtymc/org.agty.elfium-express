package org.agty.elfiumexpress.modules.express.service;

import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.modules.express.entity.ExpressGroup;
import org.agty.elfiumexpress.repository.ExpressGroupRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ExpressGroupService {
    private final ExpressGroupRepository expressGroupRepository;

    public ExpressGroupService(ExpressGroupRepository expressGroupRepository) {
        this.expressGroupRepository = expressGroupRepository;
    }

    public void save(ExpressGroup expressGroup) {
        expressGroupRepository.save(expressGroup);
    }

    public ExpressGroup getGroup(Long id) {
        return expressGroupRepository.getById(id);
    }

    public List<ExpressGroup> getGroups(Long idRootGroup) {
        return expressGroupRepository.findAll(idRootGroup);
    }

    public void del(Long idGroup) {
        expressGroupRepository.del(idGroup);
    }

    public LinkedList<ExpressGroup> fullPath(Long idGroup) {
        return expressGroupRepository.fullPath(idGroup);
    }

    public void sort(SortBody[] bodies) {
        expressGroupRepository.sort(bodies);
    }
}
