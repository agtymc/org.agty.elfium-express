package org.agty.elfiumexpress.storage.entity;

public class UploadedFile {
    private Long idFile;
    private Long idUser;
    private String name;
    private String file;
    private String contentType;
    private Long size;
    private String extension;

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean idExists() {
        return idFile != null;
    }

    public Long getIdFile() {
        return idFile;
    }

    public void setIdFile(Long idFile) {
        this.idFile = idFile;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean hasFile() {
        return file != null && !file.isEmpty();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "UploadedFile{" +
                "idFile=" + idFile +
                ", idUser=" + idUser +
                ", name='" + name + '\'' +
                ", file='" + file + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", extension='" + extension + '\'' +
                '}';
    }
}
