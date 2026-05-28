package org.agty.elfiumexpress.modules.express.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.modules.express.converters.ExpressTypeConverter;
import org.agty.elfiumexpress.modules.express.dto.ExpressTypeDto;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ExpressTypeRepository {
    public ExpressTypeDto getById(Long id) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(Arguments.builder().setTable("{express_type}").setWhere("id_type = %d", id));
            return ExpressTypeConverter.rowToDto(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExpressTypeDto> findAll() {
        List<ExpressTypeDto> expressType = new LinkedList<>();

        List<SqlRow> list;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            list = sql.sql().listArray(Arguments.builder().setTable("{express_type}").setOrderBy("id_type ASC"));

            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (SqlRow row : list) {
            expressType.add(ExpressTypeConverter.rowToDto(row));
        }

        return expressType;
    }
}
