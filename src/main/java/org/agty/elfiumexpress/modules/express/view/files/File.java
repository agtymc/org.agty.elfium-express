package org.agty.elfiumexpress.modules.express.view.files;

import org.agty.elfiumexpress.modules.express.view.Files;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.storage.utils.ContentUtils;
import org.agty.utils.AgtyUtils;

public class File implements Files {

    @Override
    public String getContent(UploadedFile file) {
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"express-file-single\">");

        content.append("<div class=\"express-file\">");
        content.append(     "<div class=\"express-file-blank\">");
        content.append(         "<div class=\"express-file-icon\">");
        content.append(             "<div class=\"express-file-frame\">");
        content.append(                 "<div class=\"express-file-frame-text\">");
        content.append(                     file.getExtension());
        content.append(                 "</div>");
        content.append(             "</div>");
        content.append(         "</div>");
        content.append(     "</div>");
        content.append("</div>");

        content.append(File.fileInfoBlock(file));

        content.append("</div>");
        return content.toString();
    }

    public static String fileInfoBlock(UploadedFile file, String uriTarget) {
        StringBuilder content = new StringBuilder();

        content.append("<div class=\"express-file-info\">");
        content.append(     "<div class=\"express-file-info-item express-file-info-item-title\"><span>Имя:</span> ").append(ContentUtils.escapeHtml(file.getName())).append("</div>");
        content.append(     "<div class=\"express-file-info-item\"><span>Размер:</span> ").append(ContentUtils.escapeHtml(AgtyUtils.filesizeToTitle(file.getSize(), "en"))).append("</div>");
        content.append(     "<div class=\"express-file-info-item\"><span>Content-Type:</span> ").append(ContentUtils.escapeHtml(file.getContentType())).append("</div>");
        content.append(     "<div class=\"express-file-info-item\"><span>Extension:</span> ").append(ContentUtils.escapeHtml(file.getExtension())).append("</div>");
        content.append("</div>");

        content.append("<div class=\"express-file-download\">");
        content.append("<a href=\"/content/files/").append(file.getFile()).append("\" class=\"button\" target=\"").append(uriTarget).append("\">СКАЧАТЬ</a>");
        content.append("</div>");

        return content.toString();
    }

    public static String fileInfoBlock(UploadedFile file) {
        return fileInfoBlock(file, "");
    }
}
