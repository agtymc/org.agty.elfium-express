package org.agty.elfiumexpress.security.role;

import java.io.Serial;
import java.io.Serializable;

public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 8372522676269363875L;

    private Long id;
    private String name;
    private String title;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Role(Long id, String name, String title) {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
