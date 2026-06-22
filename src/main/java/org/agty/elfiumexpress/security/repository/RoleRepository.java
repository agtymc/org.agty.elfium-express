package org.agty.elfiumexpress.security.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.security.role.Role;
import org.agty.utils.AgtyUtils;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepository {
    public List<Role> findAll() {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            List<SqlRow> rows = sql.sql().findAll(Arguments.builder().setTable("spring_roles"));
            List<Role> roles = new ArrayList<>();
            for (SqlRow row : rows) {
                roles.add(rowToRole(row));
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Role> findByUserId(Long idUser) {
        if (idUser == null || idUser < 1) {
            return List.of();
        }

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            List<SqlRow> rows = sql.sql().findAll(
                    Arguments.builder()
                            .setTable("spring_users_roles ur LEFT JOIN spring_roles r ON r.id_role = ur.id_role")
                            .setFields("r.id_role, r.name, r.title")
                            .setWhere("ur.id_user = %d", idUser)
            );

            List<Role> roles = new ArrayList<>();
            for (SqlRow row : rows) {
                roles.add(rowToRole(row));
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Role findById(Long idRole) {
        if (idRole == null || idRole < 1) {
            return null;
        }

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("spring_roles")
                            .setWhere("id_role = %d", idRole)
            );
            return row.noEmpty() ? rowToRole(row) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Role findByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(
                    Arguments.builder()
                            .setTable("spring_roles")
                            .setWhere("name = '%s'", AgtyUtils.hencode(name.trim()))
            );
            return row.noEmpty() ? rowToRole(row) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void replaceUserRoles(Long idUser, List<Role> roles) {
        if (idUser == null || idUser < 1) {
            return;
        }

        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            sql.sql().executeUpdate("DELETE FROM spring_users_roles WHERE id_user = %d".formatted(idUser));

            for (Role role : roles) {
                sql.sql().executeUpdate(
                        "INSERT INTO spring_users_roles (id_user, id_role) VALUES (%d, %d) ON CONFLICT DO NOTHING"
                                .formatted(idUser, role.getId())
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Role rowToRole(SqlRow row) {
        Role role = new Role();
        role.setId(row.getLong("id_role"));
        role.setName(row.getString("name"));
        role.setTitle(row.getString("title"));
        return role;
    }
}
