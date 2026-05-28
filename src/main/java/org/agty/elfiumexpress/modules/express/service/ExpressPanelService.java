package org.agty.elfiumexpress.modules.express.service;

import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.modules.express.entity.ExpressPanel;
import org.agty.elfiumexpress.repository.ExpressPanelRepository;
import org.agty.elfiumexpress.repository.FileUploadRepository;
import org.agty.elfiumexpress.repository.ThumbsRepository;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpressPanelService {
    public final ExpressPanelRepository expressPanelRepository;
    public final ThumbsRepository thumbsRepository;
    public final FileUploadRepository fileUploadRepository;

    public ExpressPanelService(ExpressPanelRepository expressPanelRepository,
                               ThumbsRepository thumbsRepository,
                               FileUploadRepository fileUploadRepository) {
        this.expressPanelRepository = expressPanelRepository;
        this.thumbsRepository = thumbsRepository;
        this.fileUploadRepository = fileUploadRepository;
    }

    public Long saveExpressPanel(ExpressPanel expressPanel) {
        return expressPanelRepository.save(expressPanel);
    }

    public ExpressPanel getExpressPanel(Long id) {
        return expressPanelRepository.getById(id);
    }

    public List<ExpressPanel> getExpressPanels(Long idGroup) {
        return expressPanelRepository.findAll(idGroup);
    }

    public List<UploadedFile> getFiles(Long idPanel) {
        return expressPanelRepository.findUploadedFiles(idPanel);
    }

    public void del(Long idPanel) {
        removeFiles(idPanel); //Сначала файлы
        expressPanelRepository.del(idPanel); //Потом панель
    }

    public void removeFiles(Long idPanel) {
        List<UploadedFile> uploadedFiles = getFiles(idPanel);
        for (UploadedFile uploadedFile : uploadedFiles) {
            thumbsRepository.deleteThumbByFile(uploadedFile.getFile());
            fileUploadRepository.deleteFile(uploadedFile, 0L);
        }
    }

    public void sort(SortBody[] bodies) {
        expressPanelRepository.sort(bodies);
    }
}
