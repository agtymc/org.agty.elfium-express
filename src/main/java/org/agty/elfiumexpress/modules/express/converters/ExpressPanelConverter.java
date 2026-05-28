package org.agty.elfiumexpress.modules.express.converters;

import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.modules.express.common.ExpressType;
import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.modules.express.entity.ExpressPanel;

public final class ExpressPanelConverter {
    private ExpressPanelConverter() {
    }

    public static ExpressPanelDto rowToDto(SqlRow row) {
        ExpressPanelDto dto = new ExpressPanelDto();
        dto.setIdExpress(row.getLong("id_express"));
        dto.setIdUser(row.getLong("id_user"));
        dto.setDate(row.getLocalDateTime("date"));
        dto.setTitle(row.getString("title"));
        dto.setAbout(row.getString("about"));
        dto.setUri(row.getString("uri"));
        dto.setIdGroup(row.getLong("id_group"));
        dto.setGroupTitle(row.getString("group_title"));
        dto.setIdType(row.getLong("id_type"));
        dto.setBody(row.getString("body"));
        dto.setIcon(ExpressType.getIconByType(row.getLong("id_type")));
        return dto;
    }

    public static ExpressPanel dtoToEntity(ExpressPanelDto dto) {
        ExpressPanel entity = new ExpressPanel();
        entity.setIdExpress(dto.getIdExpress());
        entity.setIdUser(dto.getIdUser());
        entity.setIdGroup(dto.getIdGroup());
        entity.setDate(dto.getDate());
        entity.setAbout(dto.getAbout());
        entity.setBody(dto.getBody());
        entity.setGroupTitle(dto.getGroupTitle());
        entity.setIcon(dto.getIcon());
        entity.setTitle(dto.getTitle());
        entity.setUri(dto.getUri());
        entity.setAttachments(dto.getAttachments());
        entity.setFiles(dto.getFiles());
        entity.setIdType(dto.getIdType());
        return entity;
    }
}
