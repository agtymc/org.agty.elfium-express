package org.agty.elfiumexpress.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.api.entity.ActionItem;
import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.modules.express.entity.ExpressGroup;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ExpressGroupRepository {
    public ExpressGroup getById(Long id) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(new Arguments().setTable("{groups}").setWhere("id_group = " + id));
            return ExpressGroup.rowToEntity(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean save(ExpressGroup expressGroup) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            Arguments arguments = new Arguments()
                    .setTable("{groups}")
                    .setData("r_group", expressGroup.getRGroup())
                    .setData("title", expressGroup.getTitle())
                    .setData("comment", expressGroup.getComment());

            if (expressGroup.hasIdGroup()) {
                arguments.setWhere("[id_group] = " + expressGroup.getIdGroup());
                sql.sql().update(arguments);
            } else {
                Long maxAlign = sql.sql().max(
                        new Arguments().setTable("{groups}")
                                .setWhere("r_group=" + expressGroup.getRGroup())
                                .setActionField("align")
                );
                arguments.setData("align", maxAlign != null ? maxAlign + 1 : 0);
                sql.sql().insert(arguments);
            }

            return !sql.sql().hasErrors();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExpressGroup> findAll(Long idRootGroup) {
        List<ExpressGroup> expressGroups = new LinkedList<>();
        Arguments arguments = new Arguments()
                .setTable("{groups}")
                .setWhere("[r_group] = " + idRootGroup)
                .setOrderBy("align ASC, title ASC");

        List<SqlRow> rows;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            rows = sql.sql().findAll(arguments);
            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (SqlRow row : rows) {
            expressGroups.add(ExpressGroup.rowToEntity(row));
        }

        return expressGroups;
    }

    public void del(Long idGroup) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            sql.sql().delete(new Arguments().setTable("{groups}").setWhere("id_group = " + idGroup));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<ExpressGroup> fullPath(Long idGroup) {
        LinkedList<ExpressGroup> expressGroups = new LinkedList<>();
        getFullPath(idGroup, expressGroups);
        return expressGroups;
    }

    private void getFullPath(Long idGroup, LinkedList<ExpressGroup> expressGroups) {
        ExpressGroup current = getById(idGroup);
        expressGroups.addFirst(current);

        if (current.getRGroup() != null) {
            getFullPath(current.getRGroup(), expressGroups);
        }
    }

    public void sort(SortBody[] bodies) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            for (SortBody body : bodies) {
                sql.sql().update(
                        new Arguments()
                                .setTable("{groups}")
                                .setData("align", body.getAlign())
                                .setWhere("id_group = " + body.getId())
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void move(ActionItem actionItem) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            Long maxAlign = sql.sql().max(
                    new Arguments().setTable("{groups}")
                            .setWhere("r_group=" + actionItem.getDst())
                            .setActionField("align")
            );

            sql.sql().update(
                    new Arguments()
                            .setTable("{groups}")
                            .setData("r_group", actionItem.getDst())
                            .setData("align", maxAlign != null ? maxAlign + 1 : 0)
                            .setWhere("id_group = " + actionItem.getSrc())
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
