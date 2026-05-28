package org.agty.elfiumexpress.modules.security.role;

import java.io.Serial;
import java.io.Serializable;

public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 8372522676269363875L;

    private Long id;
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
