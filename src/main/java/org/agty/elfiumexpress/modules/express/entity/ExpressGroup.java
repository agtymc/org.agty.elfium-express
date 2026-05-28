package org.agty.elfiumexpress.modules.express.entity;

import jakarta.validation.constraints.NotBlank;
import org.agty.agtysql.interfaces.SqlRow;

public class ExpressGroup {
    private Long idUser;
    private Long idGroup;
    private Long rGroup;

    @NotBlank(message = "The \"Title\" field cannot be empty")
    private String title;
    private String comment;

    public static ExpressGroup rowToEntity(SqlRow row) {
        ExpressGroup expressGroup = new ExpressGroup();
        expressGroup.setIdUser(row.getLong("id_user"));
        expressGroup.setIdGroup(row.getLong("id_group"));
        expressGroup.setRGroup(row.getLong("r_group"));
        expressGroup.setTitle(row.getString("title"));
        expressGroup.setComment(row.getString("comment"));
        return expressGroup;
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

    public boolean hasIdGroup() {
        return idGroup != null;
    }

    public Long getRGroup() {
        return rGroup;
    }

    public void setRGroup(Long r_group) {
        this.rGroup = r_group;
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

    @Override
    public String toString() {
        return "Group{" +
                "id_user=" + idUser +
                ", " +
                "id_group=" + idGroup +
                ", r_group=" + rGroup +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
