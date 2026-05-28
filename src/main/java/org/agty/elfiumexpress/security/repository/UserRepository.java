package org.agty.elfiumexpress.security.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.security.converters.UserConverter;
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
    public User findByEmail(String email) {
        if (AgtyUtils.stringIsNullOrEmpty(email)) return null;

        SqlRow row;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("{users}")
                            .setWhere("[email] = '%s'", email)
            );

            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (row.noEmpty()) {
            User user = UserConverter.rowToEntity(row);
            user.setRoles(List.of(new Role("ROLE_USER")));
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
            users.add(UserConverter.rowToDto(row));
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
            user.setRoles(List.of(new Role("ROLE_USER")));
            return user;
        }
        return null;
    }

    public User save(User user) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            return sql.sql().saveEntityWithCheck(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
