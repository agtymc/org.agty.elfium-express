package org.agty.elfiumexpress.modules.security.service;


import org.agty.elfiumexpress.modules.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.modules.security.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * service/UserRegistrationDto | service/UserServiceImpl
 */
public interface UserServiceInterface extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    List<User> getAll();
    UserDetails loadUserByUsername(String username);
}
