package org.agty.elfiumexpress.security.converters;

import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.security.dto.UserDto;
import org.agty.elfiumexpress.security.entity.User;
import org.agty.elfiumexpress.security.role.Role;

import java.util.List;

public final class UserConverter {
    private UserConverter() {
    }

    public static User rowToEntity(SqlRow row) {
        User user = new User();
        user.setId(row.isSet("id_users") ? row.getLong("id_users") : row.getLong("id_user"));
        user.setFirstName(row.getString("first_name"));
        user.setLastName(row.getString("second_name"));
        user.setThirdName(row.getString("third_name"));
        user.setLogin(row.isSet("login") ? row.getString("login") : row.getString("email"));
        user.setEmail(row.getString("email"));
        user.setPassword(row.getString("password"));
        user.setDisabled(row.getBoolean("disabled"));
        user.setRoles(List.of(new Role("ROLE_USER")));
        return user;
    }

    public static UserDto rowToDto(SqlRow row) {
        UserDto dto = new UserDto();
        dto.setId(row.isSet("id_users") ? row.getLong("id_users") : row.getLong("id_user"));
        dto.setFirstName(row.getString("first_name"));
        dto.setLastName(row.getString("second_name"));
        dto.setThirdName(row.getString("third_name"));
        dto.setLogin(row.isSet("login") ? row.getString("login") : row.getString("email"));
        dto.setEmail(row.getString("email"));
        dto.setPassword(row.getString("password"));
        dto.setDisabled(Boolean.TRUE.equals(row.getBoolean("disabled")));
        dto.setRoles(List.of(new Role("ROLE_USER")));
        return dto;
    }

    public static User dtoToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setThirdName(dto.getThirdName());
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDisabled(dto.isDisabled());
        user.setRoles(dto.getRoles());
        return user;
    }
}
