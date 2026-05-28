package org.agty.elfiumexpress.web.controllers.mvc;

import org.agty.elfiumexpress.storage.entity.Thumb;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.repository.ThumbsRepository;
import org.agty.elfiumexpress.storage.service.FileUploadService;
import org.agty.elfiumexpress.storage.service.StorageService;
import org.agty.elfiumexpress.storage.thumbs.Thumbs;
import org.agty.elfiumexpress.storage.thumbs.ThumbsObserver;
import org.agty.elfiumexpress.storage.types.ContentDisposition;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class StorageController {
    private final StorageService storageService;
    private final FileUploadService fileUploadService;
    private final ThumbsRepository thumbsRepository;

    public StorageController(StorageService storageService, FileUploadService fileUploadService, ThumbsRepository thumbsRepository) {
        this.storageService = storageService;
        this.fileUploadService = fileUploadService;
        this.thumbsRepository = thumbsRepository;
    }

    /**
     * Подгрузка файлов. По умолчанию будет из users/0/
     * @param filename Filename
     * @return ResponseEntity<Resource>
     */
    @GetMapping("/content/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {

        //Get a file from repository
        UploadedFile file = fileUploadService.getFile(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        /*
            TODO: здесь можно ввести ограничение, что видит только свои файлы
         */
        Resource resource = storageService.loadAsResource("users/0/" + filename);

        if (resource == null)
            return ResponseEntity.notFound().build();

        String contentDisposition = ContentDisposition.getContentDispositionByContentType(file.getContentType());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8) + "\"");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(resource);
    }

    /**
     * Подгрузка файлов. По умолчанию будет из users/0/
     * @param filename Filename
     * @return ResponseEntity<Resource>
     */
    @GetMapping("/thumb/{width}x{height}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getThumb(@PathVariable int width, @PathVariable int height, @PathVariable final String filename) {
        if (height > 250) height = 250;
        if (width > 250) width = 250;
        if (height < 100) height = 100;
        if (width < 100) width = 100;

        //Get a file from repository
        UploadedFile file = fileUploadService.getFile(filename);

        if (file == null)
               return ResponseEntity.notFound().build();

        String source = "content/files/users/0/" + filename;
        String thumbName = file.getFile() + "-" + width + "x" + height + ".jpg";
        String destination = "content/files/users/0/thumbs/" + thumbName;

        //TODO: переделать в потоки и дожидаться завершения
        while(ThumbsObserver.thumbInProgress(thumbName)) {}

        if (!Files.exists(Path.of(destination))) {
            try {
                ThumbsObserver.registerCreator(thumbName);

                Thumbs thumbCreator = new Thumbs();
                thumbCreator.setSource(source);
                thumbCreator.setDestination(destination);
                thumbCreator.setWidth(width);
                thumbCreator.setHeight(height);
                thumbCreator.setQuality(.75f);
                thumbCreator.setMimeType(file.getContentType());
                thumbCreator.save();

                if (Files.exists(Path.of(destination))) {
                    Thumb thumb = new Thumb();
                    thumb.setThumb(thumbName);
                    thumb.setFile(filename);

                    thumbsRepository.save(thumb);
                }

                ThumbsObserver.unregisterCreator(thumbName);

            } catch (IOException e) {
                //throw new RuntimeException(e);
                return ResponseEntity.notFound().build();
            }
        }

        /*
            TODO: здесь можно ввести ограничение, что видит только свои файлы
         */
        Resource resource = storageService.loadAsResource("users/0/thumbs/" + thumbName);

        if (resource == null)
            return ResponseEntity.notFound().build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "image/jpeg");
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(resource);
    }
}
