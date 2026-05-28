package org.agty.elfiumexpress.repository;

import org.agty.agtysql.data.Arguments;
import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.dao.AgtySQLPool;
import org.agty.elfiumexpress.dao.ConnectionPool;
import org.agty.elfiumexpress.storage.entity.Thumb;
import org.agty.utils.AgtyUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ThumbsRepository {
    public void save(Thumb thumb) {
        if (!thumbIsExist(thumb.getThumb())) {
            Arguments arguments = Arguments.builder().setTable("{thumbs}").setData("file", thumb.getFile()).setData("thumb", thumb.getThumb());
            try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
                sql.sql().insert(arguments);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Thumb> getThumbListByFile(String file) {
        List<Thumb> thumbs = new ArrayList<>();

        Arguments arguments = Arguments.builder().setTable("{thumbs}").setWhere("file = '%s'", AgtyUtils.hencode(file));
        List<SqlRow> rows;
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            rows = sql.sql().findAll(arguments);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (SqlRow row : rows) {
            thumbs.add(Thumb.rowToThumb(row));
        }

        return thumbs;
    }

    public Thumb getThumb(String thumbName) {
        Arguments arguments = Arguments.builder().setTable("{thumbs}").setWhere("thumb = '%s'", AgtyUtils.hencode(thumbName));
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            return Thumb.rowToThumb(sql.sql().fetch(arguments));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean thumbIsExist(String thumbName) {
        Thumb thumb = getThumb(thumbName);
        return thumb.getId() != null;
    }

    public void deleteThumb(Long id) {
        try (AgtySQLPool.PooledAgtySQL sql = ConnectionPool.POOL.borrow()) {
            sql.sql().delete(Arguments.builder().setTable("{thumbs}").setWhere("id_thumbs = %d", id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteThumbByFile(String file, long idUser) {
        List<Thumb> thumbs = getThumbListByFile(file);

        if (!thumbs.isEmpty()) {
            for (Thumb thumb : thumbs) {

                deleteThumb(thumb.getId());

                try {
                    Files.deleteIfExists(Path.of("content/files/users/" + idUser + "/thumbs/" + thumb.getThumb()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
