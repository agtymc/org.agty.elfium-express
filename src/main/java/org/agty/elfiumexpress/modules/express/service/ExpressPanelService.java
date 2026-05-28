package org.agty.elfiumexpress.modules.express.service;

import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.repository.FileUploadRepository;
import org.agty.elfiumexpress.repository.ThumbsRepository;
import org.agty.elfiumexpress.modules.express.repository.ExpressPanelRepository;
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

    public Long saveExpressPanel(ExpressPanelDto expressPanel, Long idUser) {
        return expressPanelRepository.save(expressPanel, idUser);
    }

    public ExpressPanelDto getExpressPanel(Long id, Long idUser) {
        return expressPanelRepository.getById(id, idUser);
    }

    public List<ExpressPanelDto> getExpressPanels(Long idGroup, Long idUser) {
        return expressPanelRepository.findAll(idGroup, idUser);
    }

    public List<UploadedFile> getFiles(Long idPanel) {
        return expressPanelRepository.findUploadedFiles(idPanel);
    }

    public void del(Long idPanel, Long idUser) {
        removeFiles(idPanel, idUser); //Сначала файлы
        expressPanelRepository.del(idPanel, idUser); //Потом панель
    }

    public void removeFiles(Long idPanel, Long idUser) {
        List<UploadedFile> uploadedFiles = getFiles(idPanel);
        for (UploadedFile uploadedFile : uploadedFiles) {
            thumbsRepository.deleteThumbByFile(uploadedFile.getFile(), uploadedFile.getIdUser());
            fileUploadRepository.deleteFile(uploadedFile);
        }
    }

    public void sort(SortBody[] bodies, Long idUser) {
        expressPanelRepository.sort(bodies, idUser);
    }
}
