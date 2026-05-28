package org.agty.elfiumexpress.modules.express.view;

import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.modules.express.view.files.*;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.storage.types.FileTypes;
import org.agty.elfiumexpress.storage.utils.ContentUtils;

import java.util.List;

public class FileView {
    public final ExpressPanelDto expressPanel;

    public FileView(ExpressPanelDto expressPanel) {
        this.expressPanel = expressPanel;
    }

    private List<UploadedFile> getFiles() {
        return expressPanel.getFiles();
    }

    public String getContent() {
        //TODO если одна - просмотр, если множество - списки
        List<UploadedFile> files = expressPanel.getFiles();
        if (files == null) return "";

        //Single file
        if (files.size() == 1) {
            return createContent(files.getFirst());
        }

        //A lot of files
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"express-files-gallery\">");
        for (UploadedFile file : files) {
            content.append( createContentForGallery(file) );
        }
        content.append("<div class=\"clear\"></div>");
        content.append("</div>");
        return content.toString();
    }

    private String createContent(UploadedFile file) {
        //TODO Добавить другие типы
        if (FileTypes.isImage(file.getContentType())) {
            return new Image().getContent(file);
        }

        if (FileTypes.isPdf(file.getContentType())) {
            return new Pdf().getContent(file);
        }

        if (FileTypes.isAudio(file.getContentType())) {
            return new Audio().getContent(file);
        }

        if (FileTypes.isVideo(file.getContentType())) {
            return new Video().getContent(file);
        }

        if (FileTypes.isText(file.getContentType())) {
            return ContentUtils.nl2br(new Text().getContentBlock(file));
        }

        if (FileTypes.isCode(file.getContentType())) {
            return new Text().getContentTextarea(file);
        }

        return new File().getContent(file);
    }

    private String createContentForGallery(UploadedFile file) {
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"express-files-gallery-item\">");
        content.append( "<a href=\"/content/files/").append(file.getFile()).append("\" data-id=\"file\" target=\"_blank\" \">");

        if (FileTypes.isImage(file.getContentType())) {
            content.append("<div class=\"express-files-gallery-item-image-single\">");
            content.append(     "<img src=\"/thumb/250x250/").append(file.getFile()).append("\" />");
            content.append("</div>");
        } else {
            content.append("<div class=\"express-files-gallery-item-image\">");
            content.append(     "<img src=\"/img/blank.png\" class=\"image-blank\" >");
            content.append("</div>");
            content.append("<div class=\"express-files-gallery-item-title\" title=\"").append(ContentUtils.escapeHtml(file.getName())).append("\">");
            content.append(     ContentUtils.escapeHtml(file.getName()));
            content.append("</div>");
        }

        content.append( "</a>");
        content.append("</div>");
        return content.toString();
    }
}
