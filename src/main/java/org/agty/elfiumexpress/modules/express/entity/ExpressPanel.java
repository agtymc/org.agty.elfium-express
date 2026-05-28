package org.agty.elfiumexpress.modules.express.entity;

import jakarta.validation.constraints.NotNull;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.modules.express.common.ExpressType;
import org.agty.elfiumexpress.modules.express.enums.Types;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ExpressPanel {
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

    public ExpressPanel() {}

    public static ExpressPanel rowToEntity(SqlRow row) {
        ExpressPanel expressPanel = new ExpressPanel();

        expressPanel.setIdExpress(row.getLong("id_express"));
        expressPanel.setIdUser(row.getLong("id_user"));
        expressPanel.setDate(row.getLocalDateTime("date"));
        expressPanel.setTitle(row.getString("title"));
        expressPanel.setAbout(row.getString("about"));
        expressPanel.setUri(row.getString("uri"));
        expressPanel.setIdGroup(row.getLong("id_group"));
        expressPanel.setGroupTitle(row.getString("group_title"));
        expressPanel.setIdType(row.getLong("id_type"));
        expressPanel.setBody(row.getString("body"));
        expressPanel.setIcon(ExpressType.getIconByType(row.getLong("id_type")));

        return expressPanel;
    }

    public boolean hasId() {
        return idExpress != null;
    }

    public Long getIdExpress() {
        return idExpress;
    }

    public void setIdExpress(Long id_express) {
        this.idExpress = id_express;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    @Override
    public String toString() {
        return "ExpressPanel{" +
                "idExpress=" + idExpress +
                ", idUser=" + idUser +
                ", idGroup=" + idGroup +
                ", idType=" + idType +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", about='" + about + '\'' +
                ", body='" + body + '\'' +
                ", uri='" + uri + '\'' +
                ", image='" + icon + '\'' +
                ", groupTitle='" + groupTitle + '\'' +
                '}';
    }
}
