package org.agty.elfiumexpress.storage.service;

import org.agty.elfiumexpress.repository.FileUploadRepository;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@Service
public class FileUploadService {
    private final FileUploadRepository fileUploadRepository;

    public FileUploadService(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    public List<UploadedFile> save(MultipartFile[] files) {
        List<UploadedFile> uploadedFiles = new LinkedList<UploadedFile>();

        if (files == null) return uploadedFiles;

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                UploadedFile uploadedFile = fileUploadRepository.store(file);
                if (uploadedFile != null) uploadedFiles.add(uploadedFile);
            }
        }

        return uploadedFiles;
    }

    public UploadedFile getFile(String filename) {
        return fileUploadRepository.findByName(filename);
    }
}
