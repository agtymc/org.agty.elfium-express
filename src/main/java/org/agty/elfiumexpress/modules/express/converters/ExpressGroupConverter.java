package org.agty.elfiumexpress.modules.express.converters;

import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.modules.express.dto.ExpressGroupDto;
import org.agty.elfiumexpress.modules.express.entity.ExpressGroup;

public final class ExpressGroupConverter {
    private ExpressGroupConverter() {
    }

    public static ExpressGroupDto rowToDto(SqlRow row) {
        ExpressGroupDto dto = new ExpressGroupDto();
        dto.setIdUser(row.getLong("id_user"));
        dto.setIdGroup(row.getLong("id_group"));
        dto.setRGroup(row.getLong("r_group"));
        dto.setTitle(row.getString("title"));
        dto.setComment(row.getString("comment"));
        return dto;
    }

    public static ExpressGroup dtoToEntity(ExpressGroupDto dto) {
        ExpressGroup entity = new ExpressGroup();
        entity.setIdUser(dto.getIdUser());
        entity.setIdGroup(dto.getIdGroup());
        entity.setRGroup(dto.getRGroup());
        entity.setTitle(dto.getTitle());
        entity.setComment(dto.getComment());
        return entity;
    }
}
