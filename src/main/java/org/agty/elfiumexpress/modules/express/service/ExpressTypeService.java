package org.agty.elfiumexpress.modules.express.service;

import org.agty.elfiumexpress.modules.express.common.ExpressType;
import org.agty.elfiumexpress.repository.ExpressTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpressTypeService {
    public final ExpressTypeRepository expressTypeRepository;

    public ExpressTypeService(ExpressTypeRepository expressTypeRepository) {
        this.expressTypeRepository = expressTypeRepository;
    }

    public List<ExpressType> findAll() {
        return expressTypeRepository.findAll();
    }
}
