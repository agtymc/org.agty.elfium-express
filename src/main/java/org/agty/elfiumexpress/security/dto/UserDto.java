package org.agty.elfiumexpress.security.dto;

import org.agty.elfiumexpress.security.role.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class UserDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6613116334803661543L;

    private Long id;
    private String firstName;
    private String lastName;
    private String thirdName;
    private String login;
    private String email;
    private String password;
    private boolean disabled;
    private Collection<Role> roles;

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
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();

        if (lastName != null && !lastName.isBlank()) {
            if (!fullName.isEmpty()) {
                fullName.append(' ');
            }
            fullName.append(lastName.trim());
        }
        if (firstName != null && !firstName.isBlank()) {
            if (!fullName.isEmpty()) {
                fullName.append(' ');
            }
            fullName.append(firstName.trim());
        }
        if (thirdName != null && !thirdName.isBlank()) {
            if (!fullName.isEmpty()) {
                fullName.append(' ');
            }
            fullName.append(thirdName.trim());
        }

        return fullName.toString();
    }

    public String getDisplayName() {
        String fullName = getFullName();
        if (!fullName.isBlank()) {
            return fullName;
        }
        if (login != null && !login.isBlank()) {
            return login;
        }
        return email;
    }

    public boolean hasRole(String roleName) {
        Collection<Role> roleList = roles == null ? List.of() : roles;
        return roleList.stream().anyMatch(role -> roleName.equals(role.getName()));
    }

    public String getPrimaryRoleTitle() {
        Collection<Role> roleList = roles == null ? List.of() : roles;
        return roleList.stream()
                .findFirst()
                .map(role -> role.getTitle() != null && !role.getTitle().isBlank() ? role.getTitle() : role.getName())
                .orElse("");
    }
}
