package org.agty.elfiumexpress.security.service;

import org.agty.elfiumexpress.security.dto.AdminUserDto;
import org.agty.elfiumexpress.security.dto.UserProfileDto;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.dto.UserRegistrationDto;
import org.agty.elfiumexpress.security.entity.User;
import org.agty.elfiumexpress.security.role.Role;
import org.agty.elfiumexpress.security.repository.RoleRepository;
import org.agty.elfiumexpress.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        return createUser(registrationDto, List.of(requiredRole("ROLE_USER")));
    }

    @Override
    public User createInitialAdmin(UserRegistrationDto registrationDto) {
        return createUser(registrationDto, List.of(
                requiredRole("ROLE_ADMIN"),
                requiredRole("ROLE_USER")
        ));
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
    public List<UserDto> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getById(Long id) {
        return userRepository.findDtoById(id);
    }

    @Override
    public AdminUserDto getAdminUserById(Long id) {
        UserDto userDto = userRepository.findDtoById(id);
        if (userDto == null) {
            return null;
        }

        AdminUserDto adminUserDto = new AdminUserDto();
        adminUserDto.setId(userDto.getId());
        adminUserDto.setLastName(userDto.getLastName());
        adminUserDto.setEmail(userDto.getEmail());
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            adminUserDto.setRoleId(userDto.getRoles().iterator().next().getId());
        }
        return adminUserDto;
    }

    @Override
    public UserProfileDto getProfileById(Long id) {
        UserDto userDto = userRepository.findDtoById(id);
        if (userDto == null) {
            return null;
        }

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(userDto.getId());
        userProfileDto.setLastName(userDto.getLastName());
        userProfileDto.setEmail(userDto.getEmail());
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            userProfileDto.setRoleId(userDto.getRoles().iterator().next().getId());
        }
        return userProfileDto;
    }

    @Override
    public User saveProfile(UserProfileDto userProfileDto) {
        User currentUser = userRepository.findById(userProfileDto.getId());
        if (currentUser == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        String email = userProfileDto.getEmail() == null ? null : userProfileDto.getEmail().trim().toLowerCase();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail обязателен");
        }
        if (userRepository.emailExists(email, userProfileDto.getId())) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже существует");
        }

        Role role = currentUser.getRoles() == null || currentUser.getRoles().isEmpty()
                ? roleRepository.findAll().stream()
                    .filter(item -> "ROLE_USER".equals(item.getName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Role ROLE_USER not found"))
                : currentUser.getRoles().iterator().next();

        String encodedPassword = null;
        if (userProfileDto.hasPasswordChange()) {
            if (userProfileDto.getNewPassword() == null || userProfileDto.getNewPassword().isBlank()) {
                throw new IllegalArgumentException("Введите новый пароль");
            }
            if (userProfileDto.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("Новый пароль должен быть не короче 6 символов");
            }
            if (!userProfileDto.getNewPassword().equals(userProfileDto.getConfirmPassword())) {
                throw new IllegalArgumentException("Подтверждение пароля не совпадает");
            }
            encodedPassword = passwordEncoder.encode(userProfileDto.getNewPassword());
        }

        userProfileDto.setEmail(email);
        if (userProfileDto.getLastName() != null) {
            userProfileDto.setLastName(userProfileDto.getLastName().trim());
        }
        return userRepository.saveProfile(userProfileDto, role, encodedPassword);
    }

    @Override
    public User createAdminUser(AdminUserDto adminUserDto) {
        String email = adminUserDto.getEmail() == null ? null : adminUserDto.getEmail().trim().toLowerCase();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail обязателен");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже существует");
        }

        Role role = roleRepository.findById(adminUserDto.getRoleId());
        if (role == null) {
            throw new IllegalArgumentException("Выберите роль");
        }

        if (adminUserDto.getNewPassword() == null || adminUserDto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("Введите пароль");
        }
        if (adminUserDto.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть не короче 6 символов");
        }
        if (!adminUserDto.getNewPassword().equals(adminUserDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Подтверждение пароля не совпадает");
        }

        User user = new User(
                null,
                null,
                adminUserDto.getLastName() == null ? null : adminUserDto.getLastName().trim(),
                null,
                email,
                email,
                passwordEncoder.encode(adminUserDto.getNewPassword()),
                false,
                List.of(role)
        );
        return userRepository.save(user);
    }

    @Override
    public User saveAdminUser(AdminUserDto adminUserDto) {
        String email = adminUserDto.getEmail() == null ? null : adminUserDto.getEmail().trim().toLowerCase();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail обязателен");
        }
        if (userRepository.emailExists(email, adminUserDto.getId())) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже существует");
        }

        Role role = roleRepository.findById(adminUserDto.getRoleId());
        if (role == null) {
            throw new IllegalArgumentException("Выберите роль");
        }

        String encodedPassword = null;
        if (adminUserDto.hasPasswordChange()) {
            if (adminUserDto.getNewPassword() == null || adminUserDto.getNewPassword().isBlank()) {
                throw new IllegalArgumentException("Введите новый пароль");
            }
            if (adminUserDto.getNewPassword().length() < 6) {
                throw new IllegalArgumentException("Новый пароль должен быть не короче 6 символов");
            }
            if (!adminUserDto.getNewPassword().equals(adminUserDto.getConfirmPassword())) {
                throw new IllegalArgumentException("Подтверждение пароля не совпадает");
            }
            encodedPassword = passwordEncoder.encode(adminUserDto.getNewPassword());
        }

        adminUserDto.setEmail(email);
        if (adminUserDto.getLastName() != null) {
            adminUserDto.setLastName(adminUserDto.getLastName().trim());
        }
        return userRepository.saveAdminUser(adminUserDto, role, encodedPassword);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    private User createUser(UserRegistrationDto registrationDto, List<Role> roles) {
        String email = registrationDto.getEmail() == null ? null : registrationDto.getEmail().trim().toLowerCase();
        String lastName = registrationDto.getLastName() == null ? null : registrationDto.getLastName().trim();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail обязателен");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Пользователь с таким e-mail уже существует");
        }
        registrationDto.setLastName(lastName);

        User user = new User(
                null,
                null,
                registrationDto.getLastName(),
                registrationDto.getThirdName(),
                registrationDto.getLogin() == null || registrationDto.getLogin().isBlank() ? email : registrationDto.getLogin().trim(),
                email,
                passwordEncoder.encode(registrationDto.getPassword()),
                false,
                roles
        );

        return userRepository.save(user);
    }

    private Role requiredRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new IllegalStateException("Role %s not found".formatted(roleName));
        }
        return role;
    }
}
