package org.agty.elfiumexpress.modules.express.dto;

import jakarta.validation.constraints.NotBlank;

public class ExpressGroupDto {
    private Long idUser;
    private Long idGroup;
    private Long rGroup;

    @NotBlank(message = "The \"Title\" field cannot be empty")
    private String title;

    private String comment;

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

    public boolean hasIdGroup() {
        return idGroup != null;
    }

    public Long getRGroup() {
        return rGroup;
    }

    public void setRGroup(Long rGroup) {
        this.rGroup = rGroup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
