package org.agty.elfiumexpress.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.modules.express.common.ExpressType;
import org.agty.elfiumexpress.modules.express.entity.ExpressPanel;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ExpressTypeRepository {
    /**
     * Get the panel type by ID
     * @param id ID panel
     * @return ExpressPanel
     */
    public ExpressPanel getById(Long id) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(new Arguments().setTable("{express_type}").setWhere("id_type = " + id));
            return ExpressPanel.rowToEntity(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a collection of panel's types into a group
     * @return collection of panel's types
     */
    public List<ExpressType> findAll() {
        List<ExpressType> expressType = new LinkedList<>();

        List<SqlRow> list;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            list = sql.sql().listArray(new Arguments().setTable("{express_type}").setOrderBy("id_type ASC"));

            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (SqlRow row : list) {
            expressType.add(ExpressType.rowToEntity(row));
        }

        return expressType;
    }
}
