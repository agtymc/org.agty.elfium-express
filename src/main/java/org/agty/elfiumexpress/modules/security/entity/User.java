package org.agty.elfiumexpress.modules.security.entity;

import org.agty.agtysql.interfaces.SqlRow;
import org.agty.agtysql.model.annotations.Column;
import org.agty.agtysql.model.annotations.Entity;
import org.agty.agtysql.model.annotations.Id;
import org.agty.agtysql.model.annotations.Table;
import org.agty.elfiumexpress.modules.security.role.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "spring_users", schema = "elfworktest")
public class User implements Serializable {
    @Serial
    @Column(skip = true)
    private static final long serialVersionUID = 6899854012925561164L;

    @Id
    @Column(name = "id_users")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String lastName;

    @Column(name = "third_name")
    private String thirdName;

    @Column
    private String login;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Boolean disabled;

    @Column(skip = true)
    private Collection<Role> roles;

    public User() {

    }

    public User(Long id, String firstName, String lastName, String thirdName, String login, String email, String password, Boolean disabled, Collection<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.thirdName = thirdName;
        this.email = email;
        this.login = login == null ? email : login;
        this.password = password;
        this.disabled = disabled;
        this.roles = roles;
    }

    public static User rowToUser(SqlRow row) {
        User user = new User();
        user.setId(row.isSet("id_users") ? row.getLong("id_users") : row.getLong("id_user"));
        user.setFirstName(row.getString("first_name"));
        user.setLastName(row.getString("second_name"));
        user.setThirdName(row.getString("third_name"));
        user.setLogin(row.isSet("login") ? row.getString("login") : row.getString("email"));
        user.setEmail(row.getString("email"));
        user.setPassword(row.getString("password"));
        user.setDisabled(row.getBoolean("disabled"));

        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDisabled() {
        return Boolean.TRUE.equals(disabled);
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", thirdName='" + thirdName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", disabled=" + disabled +
                ", roles=" + roles +
                '}';
    }
}
