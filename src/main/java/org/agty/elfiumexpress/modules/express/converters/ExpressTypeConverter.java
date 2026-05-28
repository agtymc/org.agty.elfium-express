package org.agty.elfiumexpress.modules.express.converters;

import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.modules.express.dto.ExpressTypeDto;

public final class ExpressTypeConverter {
    private ExpressTypeConverter() {
    }

    public static ExpressTypeDto rowToDto(SqlRow row) {
        ExpressTypeDto dto = new ExpressTypeDto();
        dto.setTitle(row.getString("title"));
        dto.setIdType(row.getLong("id_type"));
        return dto;
    }
}
