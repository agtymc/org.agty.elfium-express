package org.agty.elfiumexpress.modules.express.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.api.entity.ActionItem;
import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.modules.express.converters.ExpressGroupConverter;
import org.agty.elfiumexpress.modules.express.dto.ExpressGroupDto;
import org.agty.elfiumexpress.modules.express.entity.ExpressGroup;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ExpressGroupRepository {
    private static final long ROOT_GROUP_ID = 1L;

    public ExpressGroupDto getById(Long id, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(Arguments.builder()
                    .setTable("{groups}")
                    .setWhere("id_group = %d AND (id_user = %d OR id_group = %d)", id, idUser, ROOT_GROUP_ID));
            return ExpressGroupConverter.rowToDto(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean save(ExpressGroupDto expressGroup, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            ExpressGroup entity = ExpressGroupConverter.dtoToEntity(expressGroup);
            Arguments arguments = Arguments.builder()
                    .setTable("{groups}")
                    .setData("id_user", idUser)
                    .setData("r_group", entity.getRGroup())
                    .setData("title", entity.getTitle())
                    .setData("comment", entity.getComment());

            if (entity.hasIdGroup()) {
                arguments.setWhere("[id_group] = %d AND id_user = %d", entity.getIdGroup(), idUser);
                sql.sql().update(arguments);
            } else {
                Long maxAlign = sql.sql().max(
                        Arguments.builder().setTable("{groups}")
                                .setWhere("r_group = %d AND id_user = %d", entity.getRGroup(), idUser)
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

    public List<ExpressGroupDto> findAll(Long idRootGroup, Long idUser) {
        List<ExpressGroupDto> expressGroups = new LinkedList<>();
        Arguments arguments = Arguments.builder()
                .setTable("{groups}")
                .setWhere("[r_group] = %d AND id_user = %d", idRootGroup, idUser)
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
            expressGroups.add(ExpressGroupConverter.rowToDto(row));
        }

        return expressGroups;
    }

    public void del(Long idGroup, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            sql.sql().delete(Arguments.builder().setTable("{groups}").setWhere("id_group = %d AND id_user = %d", idGroup, idUser));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkedList<ExpressGroupDto> fullPath(Long idGroup, Long idUser) {
        LinkedList<ExpressGroupDto> expressGroups = new LinkedList<>();
        getFullPath(idGroup, idUser, expressGroups);
        return expressGroups;
    }

    private void getFullPath(Long idGroup, Long idUser, LinkedList<ExpressGroupDto> expressGroups) {
        ExpressGroupDto current = getById(idGroup, idUser);
        if (current.getIdGroup() == null) {
            return;
        }
        expressGroups.addFirst(current);

        if (current.getRGroup() != null) {
            getFullPath(current.getRGroup(), idUser, expressGroups);
        }
    }

    public void sort(SortBody[] bodies, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            for (SortBody body : bodies) {
                sql.sql().update(
                        Arguments.builder()
                                .setTable("{groups}")
                                .setData("align", body.getAlign())
                                .setWhere("id_group = %d AND id_user = %d", body.getId(), idUser)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void move(ActionItem actionItem, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            Long maxAlign = sql.sql().max(
                    Arguments.builder().setTable("{groups}")
                            .setWhere("r_group = %d AND id_user = %d", actionItem.getDst(), idUser)
                            .setActionField("align")
            );

            sql.sql().update(
                    Arguments.builder()
                            .setTable("{groups}")
                            .setData("r_group", actionItem.getDst())
                            .setData("align", maxAlign != null ? maxAlign + 1 : 0)
                            .setWhere("id_group = %d AND id_user = %d", actionItem.getSrc(), idUser)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
