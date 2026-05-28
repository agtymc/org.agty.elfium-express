package org.agty.elfiumexpress.modules.express.service;

import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.modules.express.dto.ExpressGroupDto;
import org.agty.elfiumexpress.modules.express.repository.ExpressGroupRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ExpressGroupService {
    private final ExpressGroupRepository expressGroupRepository;

    public ExpressGroupService(ExpressGroupRepository expressGroupRepository) {
        this.expressGroupRepository = expressGroupRepository;
    }

    public void save(ExpressGroupDto expressGroup, Long idUser) {
        expressGroupRepository.save(expressGroup, idUser);
    }

    public ExpressGroupDto getGroup(Long id, Long idUser) {
        return expressGroupRepository.getById(id, idUser);
    }

    public List<ExpressGroupDto> getGroups(Long idRootGroup, Long idUser) {
        return expressGroupRepository.findAll(idRootGroup, idUser);
    }

    public void del(Long idGroup, Long idUser) {
        expressGroupRepository.del(idGroup, idUser);
    }

    public LinkedList<ExpressGroupDto> fullPath(Long idGroup, Long idUser) {
        return expressGroupRepository.fullPath(idGroup, idUser);
    }

    public void sort(SortBody[] bodies, Long idUser) {
        expressGroupRepository.sort(bodies, idUser);
    }
}
