package org.agty.elfiumexpress.modules.express.dto;

import jakarta.validation.constraints.NotNull;
import org.agty.elfiumexpress.modules.express.enums.Types;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ExpressPanelDto {
    private Long idExpress;
    private Long idUser;
    private Long idGroup;
    private LocalDateTime date;
    private String about;
    private String body;
    private String groupTitle;
    private String icon;
    private String title;
    private String uri;
    private MultipartFile[] attachments;
    private List<UploadedFile> files;

    @NotNull(message = "The \"Type\" field cannot be empty")
    private Long idType;

    public boolean hasId() {
        return idExpress != null;
    }

    public Long getIdExpress() {
        return idExpress;
    }

    public void setIdExpress(Long idExpress) {
        this.idExpress = idExpress;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public MultipartFile[] getAttachments() {
        return attachments;
    }

    public void setAttachments(MultipartFile[] attachments) {
        this.attachments = attachments;
    }

    public List<UploadedFile> getFiles() {
        return files;
    }

    public void setFiles(List<UploadedFile> files) {
        this.files = files;
    }

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }

    public boolean emptyFieldIfExists(Types type) {
        if (getIdType() == type.getType()) {
            if (type == Types.URI) {
                return getUri() == null || getUri().isEmpty();
            }
            if (type == Types.BOOKMARK) {
                return getBody() == null || getBody().isEmpty();
            }
        }
        return false;
    }

    public boolean hasUri() {
        return getUri() != null && !getUri().isEmpty();
    }

    public boolean hasTitle() {
        return getTitle() != null && !getTitle().isEmpty();
    }

    public boolean isUriType() {
        return getIdType() != null && getIdType() == 1L;
    }

    public boolean isBookmarkType() {
        return getIdType() != null && getIdType() == 2L;
    }

    public boolean isFileType() {
        return getIdType() != null && getIdType() == 3L;
    }
}
