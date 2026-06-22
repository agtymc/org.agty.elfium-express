package org.agty.elfiumexpress.security.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.security.converters.UserConverter;
import org.agty.elfiumexpress.security.dto.AdminUserDto;
import org.agty.elfiumexpress.security.dto.UserProfileDto;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.entity.User;
import org.agty.elfiumexpress.security.role.Role;
import org.agty.utils.AgtyUtils;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    private final RoleRepository roleRepository;

    public UserRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public User findByEmail(String email) {
        if (AgtyUtils.stringIsNullOrEmpty(email)) return null;

        SqlRow row;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("{users}")
                            .setWhere("[email] = '%s'", AgtyUtils.hencode(email))
            );

            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (row.noEmpty()) {
            User user = UserConverter.rowToEntity(row);
            user.setRoles(roleRepository.findByUserId(user.getId()));
            return user;
        }
        return null;
    }

    public List<UserDto> findAll() {

        List<UserDto> users = new ArrayList<>();

        List<SqlRow> list;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            list = sql.sql().findAll(
                    Arguments.builder()
                            .setTable("{users}")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (SqlRow row : list) {
            UserDto userDto = UserConverter.rowToDto(row);
            userDto.setRoles(roleRepository.findByUserId(userDto.getId()));
            users.add(userDto);
        }

        return users;
    }

    public User findById(Long id) {
        if (id == null || id < 1) return null;

        SqlRow row;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("{users}")
                            .setWhere("[id_user] = %d", id)
            );

            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (row.noEmpty()) {
            User user = UserConverter.rowToEntity(row);
            user.setRoles(roleRepository.findByUserId(user.getId()));
            return user;
        }
        return null;
    }

    public UserDto findDtoById(Long id) {
        if (id == null || id < 1) return null;

        SqlRow row;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("{users}")
                            .setWhere("[id_user] = %d", id)
            );

            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (row.noEmpty()) {
            UserDto userDto = UserConverter.rowToDto(row);
            userDto.setRoles(roleRepository.findByUserId(userDto.getId()));
            return userDto;
        }
        return null;
    }

    public User save(User user) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            User saved = sql.sql().saveEntityWithCheck(user);
            if (sql.sql().hasErrors()) {
                throw new IllegalStateException(sql.sql().getErrors());
            }
            if (saved == null || saved.getId() == null) {
                throw new IllegalStateException("Пользователь не был сохранен");
            }
            List<Role> roles = user.getRoles() == null || user.getRoles().isEmpty()
                    ? List.of(defaultRole())
                    : List.copyOf(user.getRoles());
            roleRepository.replaceUserRoles(saved.getId(), roles);
            saved.setRoles(roles);
            return saved;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User saveProfile(UserProfileDto userProfileDto, Role role, String encodedPassword) {
        if (userProfileDto.getId() == null || userProfileDto.getId() < 1) {
            throw new IllegalArgumentException("Некорректный пользователь");
        }

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            String updateQuery = encodedPassword == null
                    ? "UPDATE spring_users SET second_name = %s, login = %s, email = %s WHERE id_user = %d".formatted(
                    toSqlString(userProfileDto.getLastName()),
                    toSqlString(userProfileDto.getEmail()),
                    toSqlString(userProfileDto.getEmail()),
                    userProfileDto.getId()
            )
                    : "UPDATE spring_users SET second_name = %s, login = %s, email = %s, password = '%s' WHERE id_user = %d".formatted(
                    toSqlString(userProfileDto.getLastName()),
                    toSqlString(userProfileDto.getEmail()),
                    toSqlString(userProfileDto.getEmail()),
                    AgtyUtils.hencode(encodedPassword),
                    userProfileDto.getId()
            );
            sql.sql().executeUpdate(updateQuery);
            roleRepository.replaceUserRoles(userProfileDto.getId(), List.of(role));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return findById(userProfileDto.getId());
    }

    public User saveAdminUser(AdminUserDto adminUserDto, Role role, String encodedPassword) {
        if (adminUserDto.getId() == null || adminUserDto.getId() < 1) {
            throw new IllegalArgumentException("Некорректный пользователь");
        }

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            String updateQuery = encodedPassword == null
                    ? "UPDATE spring_users SET second_name = %s, login = %s, email = %s WHERE id_user = %d".formatted(
                    toSqlString(adminUserDto.getLastName()),
                    toSqlString(adminUserDto.getEmail()),
                    toSqlString(adminUserDto.getEmail()),
                    adminUserDto.getId()
            )
                    : "UPDATE spring_users SET second_name = %s, login = %s, email = %s, password = '%s' WHERE id_user = %d".formatted(
                    toSqlString(adminUserDto.getLastName()),
                    toSqlString(adminUserDto.getEmail()),
                    toSqlString(adminUserDto.getEmail()),
                    AgtyUtils.hencode(encodedPassword),
                    adminUserDto.getId()
            );
            sql.sql().executeUpdate(updateQuery);
            roleRepository.replaceUserRoles(adminUserDto.getId(), List.of(role));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return findById(adminUserDto.getId());
    }

    public boolean emailExists(String email, Long excludeUserId) {
        if (AgtyUtils.stringIsNullOrEmpty(email)) {
            return false;
        }

        String where = excludeUserId == null
                ? "[email] = '%s'".formatted(AgtyUtils.hencode(email))
                : "[email] = '%s' AND [id_user] <> %d".formatted(AgtyUtils.hencode(email), excludeUserId);

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("{users}")
                            .setWhere(where)
            );
            return row.noEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long countRegisteredUsers() {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("{users}")
                            .setFields("COUNT(*) AS cnt")
            );
            return row.noEmpty() ? row.getLong("cnt") : 0L;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Role defaultRole() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .filter(role -> "ROLE_USER".equals(role.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role ROLE_USER not found"));
    }

    private String toSqlString(String value) {
        return value == null || value.isBlank() ? "NULL" : "'%s'".formatted(AgtyUtils.hencode(value.trim()));
    }
}
