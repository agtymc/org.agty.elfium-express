package org.agty.elfiumexpress.security.service;


import org.agty.elfiumexpress.security.entity.User;
import org.agty.elfiumexpress.security.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsCustom implements UserDetails {
    @Serial
    private static final long serialVersionUID = 2305380493185740958L;

    private final User user;
    private final String password;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsCustom(User user) {
        this.username = user.getEmail() != null ? user.getEmail() : user.getLogin();
        this.password = user.getPassword();
        this.authorities = mapRolesToAuthorities(user.getRoles());
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getUser().isDisabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return !getUser().isDisabled();
    }

    public User getUser() {
        return user;
    }

    public boolean hasRole(String roleName) {
        return authorities.stream().anyMatch(authority -> roleName.equals(authority.getAuthority()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<Role> roleList = roles == null ? List.of() : roles;
        return roleList.stream()
                .map(
                    role -> new SimpleGrantedAuthority(role.getName())
                )
                .collect(Collectors.toList());
    }
}
