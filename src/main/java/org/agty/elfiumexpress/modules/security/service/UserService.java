package org.agty.elfiumexpress.modules.security.service;

import org.agty.elfiumexpress.modules.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.modules.security.entity.User;
import org.agty.elfiumexpress.modules.security.role.Role;
import org.agty.elfiumexpress.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        String email = registrationDto.getEmail() == null ? null : registrationDto.getEmail().trim().toLowerCase();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail обязателен");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже существует");
        }
        User user = new User(
                null,
                registrationDto.getFirstName(),
                registrationDto.getLastName(),
                registrationDto.getThirdName(),
                registrationDto.getLogin() == null || registrationDto.getLogin().isBlank() ? email : registrationDto.getLogin().trim(),
                email,
                passwordEncoder.encode(registrationDto.getPassword()),
                false,
                List.of(new Role("ROLE_USER"))
        );

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username == null ? null : username.trim().toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new UserDetailsCustom(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
