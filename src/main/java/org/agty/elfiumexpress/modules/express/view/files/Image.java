package org.agty.elfiumexpress.modules.express.view.files;

import org.agty.elfiumexpress.modules.express.view.Files;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.storage.utils.ContentUtils;
import org.agty.utils.AgtyUtils;

public class Image implements Files {
    @Override
    public String getContent(UploadedFile file) {
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"express-image-single\">");
        content.append( "<a href=\"/content/files/").append(file.getFile()).append("\" data-id=\"image\" target=\"_blank\">");
        content.append(     "<img src=\"/content/files/").append(file.getFile()).append("\" >");
        content.append( "</a>");
        content.append( "<div class=\"express-image-title\">");
        content.append(     ContentUtils.escapeHtml(file.getName()));
        content.append(     " (");
        content.append(         AgtyUtils.filesizeToTitle(file.getSize(), "en"));
        content.append(     ")");
        content.append( "</div>");
        content.append("</div>");
        return content.toString();
    }
}
