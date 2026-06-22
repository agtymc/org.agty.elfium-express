package org.agty.elfiumexpress.security.service;

import org.agty.elfiumexpress.security.dto.AdminUserDto;
import org.agty.elfiumexpress.security.dto.UserProfileDto;
import org.agty.elfiumexpress.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.entity.User;
import org.agty.elfiumexpress.security.role.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * service/UserRegistrationDto | service/UserServiceImpl
 */
public interface UserServiceInterface extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    User createInitialAdmin(UserRegistrationDto registrationDto);
    List<UserDto> getAll();
    UserDto getById(Long id);
    AdminUserDto getAdminUserById(Long id);
    UserProfileDto getProfileById(Long id);
    User saveProfile(UserProfileDto userProfileDto);
    User createAdminUser(AdminUserDto adminUserDto);
    User saveAdminUser(AdminUserDto adminUserDto);
    List<Role> getRoles();
    UserDetails loadUserByUsername(String username);
}
