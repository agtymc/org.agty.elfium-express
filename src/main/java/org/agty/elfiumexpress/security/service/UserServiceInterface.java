package org.agty.elfiumexpress.security.service;

import org.agty.elfiumexpress.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * service/UserRegistrationDto | service/UserServiceImpl
 */
public interface UserServiceInterface extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    List<UserDto> getAll();
    UserDto getById(Long id);
    UserDetails loadUserByUsername(String username);
}
