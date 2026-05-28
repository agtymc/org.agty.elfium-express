package org.agty.elfiumexpress.modules.express.view.files;

import org.agty.elfiumexpress.modules.express.view.Files;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.storage.utils.ContentUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Text implements Files {
    @Override
    public String getContent(UploadedFile file) {
        //TODO: нихуя не безопасно!
        return readFile("content/files/users/" + file.getIdUser() + "/" + file.getFile());
    }

    public String getContentBlock(UploadedFile file) {
        return getBlock(file, getContent(file));
    }

    public String getContentTextarea(UploadedFile file) {
        StringBuilder block = new StringBuilder();
        block.append("<textarea class='express-file-text-area'>");
        block.append(ContentUtils.escapeHtml(getContent(file)));
        block.append("</textarea>");
        return getBlock(file, block.toString());
    }

    public String getBlock(UploadedFile file, String content) {
        StringBuilder block = new StringBuilder();
        block.append("<div class=\"express-file-text\">");
        block.append("<div class=\"express-file-text-file\">");
        block.append(content);
        block.append("</div>");
        block.append(File.fileInfoBlock(file));
        block.append("</div>");
        return block.toString();
    }

    private String readFile(String fileName) {

        try {
            java.io.File file = new java.io.File(fileName);
            BufferedReader br  = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            content.setLength(content.length() - 1);
            return content.toString();
        } catch (IOException e) {
            return "Read file error";
        }
    }
}
