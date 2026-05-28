package org.agty.elfiumexpress.modules.express.repository;

import org.agty.elfiumexpress.repository.FileUploadRepository;

import org.agty.agtysql.AgtySQL;
import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.api.entity.ActionItem;
import org.agty.elfiumexpress.api.entity.SortBody;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.modules.express.converters.ExpressPanelConverter;
import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.modules.express.entity.ExpressPanel;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.storage.utils.UploadedFileUtils;
import org.agty.elfiumexpress.utils.StringBuilderExtend;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ExpressPanelRepository {
    private final FileUploadRepository fileUploadRepository;

    public ExpressPanelRepository(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    public ExpressPanelDto getById(Long id, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            SqlRow row = sql.sql().fetch(Arguments.builder()
                    .setTable("{express}")
                    .setWhere("id_express = %d AND id_user = %d", id, idUser));
            ExpressPanelDto expressPanel = ExpressPanelConverter.rowToDto(row);
            if (expressPanel.getIdExpress() != null) {
                expressPanel.setFiles(findUploadedFiles(expressPanel.getIdExpress()));
            }
            return expressPanel;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExpressPanelDto> findAll(Long idGroup, Long idUser) {
        List<ExpressPanelDto> expressPanels = new LinkedList<>();
        String query = getListQuery(
                Arguments.builder()
                        .setWhere("express.id_group = %d AND express.id_user = %d", idGroup, idUser)
                        .setOrderBy("express.align ASC, express.title ASC")
        );

        List<SqlRow> list;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            list = sql.sql().listArray(Arguments.builder().setQuery(query));
            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (SqlRow row : list) {
            expressPanels.add(ExpressPanelConverter.rowToDto(row));
        }

        return expressPanels;
    }

    public List<UploadedFile> findUploadedFiles(Long idPanel) {
        Arguments arguments = Arguments.builder().setQuery(
                ("SELECT files.id_file, files.name, files.file, files.content_type, files.size, files.ext " +
                        "FROM {express_files} as expfiles " +
                        "LEFT JOIN {files} as files ON (files.id_file = expfiles.id_file) " +
                        "WHERE expfiles.id_express = %d ORDER BY files.id_file ASC").formatted(idPanel)
        );

        List<SqlRow> list;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            list = sql.sql().listArray(arguments);
            if (sql.sql().hasErrors()) {
                System.err.println(sql.sql().getErrors());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<UploadedFile> files = new LinkedList<>();
        for (SqlRow row : list) {
            files.add(UploadedFileUtils.rowToUploadedFile(row));
        }

        return files;
    }

    public Long save(ExpressPanelDto expressPanel, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            Long idPanel;
            ExpressPanel entity = ExpressPanelConverter.dtoToEntity(expressPanel);
            Arguments arguments = Arguments.builder()
                    .setTable("{express}")
                    .setData("id_user", idUser)
                    .setData("id_group", entity.getIdGroup())
                    .setData("title", entity.getTitle())
                    .setData("about", entity.getAbout())
                    .setData("uri", entity.getUri())
                    .setData("id_type", entity.getIdType())
                    .setData("body", entity.getBody());

            if (!entity.hasId()) {
                Long maxAlign = sql.sql().max(
                        Arguments.builder().setTable("{express}")
                                .setWhere("id_group = %d AND id_user = %d", entity.getIdGroup(), idUser)
                                .setActionField("align")
                );
                arguments.setData("align", maxAlign != null ? maxAlign + 1 : 0);
                sql.sql().insert(arguments);
                idPanel = sql.sql().lastInsertId(Arguments.builder().setTable("{express}"));
            } else {
                arguments.setWhere("[id_express] = %d AND id_user = %d", entity.getIdExpress(), idUser);
                sql.sql().update(arguments);
                idPanel = entity.getIdExpress();
            }

            if (idPanel != null) {
                saveFiles(entity.getFiles(), idPanel, sql.sql());
            }

            return idPanel;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFiles(List<UploadedFile> files, Long idPanel, AgtySQL sql) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (UploadedFile file : files) {
            Long idFile = file.idExists() ? file.getIdFile() : fileUploadRepository.save(file);
            if (idFile != null) {
                refToFile(idPanel, idFile, sql);
            }
        }
    }

    private void refToFile(Long idPanel, Long idFile, AgtySQL sql) {
        Arguments arguments = Arguments.builder().setTable("{express_files}")
                .setData("id_express", idPanel)
                .setData("id_file", idFile)
                .setWhere("id_express = %d AND id_file = %d", idPanel, idFile);

        if (!sql.rowIsExists(arguments)) {
            sql.insert(arguments);
        }
    }

    public void del(Long idPanel, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            sql.sql().delete(Arguments.builder().setTable("{express}").setWhere("[id_express] = %d AND id_user = %d", idPanel, idUser));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void sort(SortBody[] bodies, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            for (SortBody body : bodies) {
                sql.sql().update(
                        Arguments.builder()
                                .setTable("{express}")
                                .setData("align", body.getAlign())
                                .setWhere("id_express = %d AND id_user = %d", body.getId(), idUser)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void move(ActionItem actionItem, Long idUser) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            Long maxAlign = sql.sql().max(
                    Arguments.builder().setTable("{express}")
                            .setWhere("id_group = %d AND id_user = %d", actionItem.getDst(), idUser)
                            .setActionField("align")
            );

            sql.sql().update(
                    Arguments.builder()
                            .setTable("{express}")
                            .setData("id_group", actionItem.getDst())
                            .setData("align", maxAlign != null ? maxAlign + 1 : 0)
                            .setWhere("id_express = %d AND id_user = %d", actionItem.getSrc(), idUser)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getListQuery(Arguments arguments) {
        StringBuilderExtend query = new StringBuilderExtend();

        query.appendAndSpaceRight("SELECT");
        query.appendAndSpaceRight("express.id_express,");
        query.appendAndSpaceRight("express.date,");
        query.appendAndSpaceRight("express.title,");
        query.appendAndSpaceRight("express.about,");
        query.appendAndSpaceRight("express.uri,");
        query.appendAndSpaceRight("express.id_type,");
        query.appendAndSpaceRight("express.body,");
        query.appendAndSpaceRight("express.id_group,");
        query.appendAndSpaceRight("groups.title as group_title");
        query.appendAndSpaceRight("FROM {express} as express");
        query.appendAndSpaceRight("LEFT JOIN {groups} as groups ON (groups.id_group = express.id_group)");

        if (arguments.hasWhere()) {
            query.appendAndSpaceRight("WHERE");
            query.appendAndSpaceRight(arguments.getWhere());
        }

        if (arguments.hasOrderBy()) {
            query.appendAndSpaceRight("ORDER BY");
            query.appendAndSpaceRight(arguments.getOrderBy());
        }

        return query.toString();
    }
}
