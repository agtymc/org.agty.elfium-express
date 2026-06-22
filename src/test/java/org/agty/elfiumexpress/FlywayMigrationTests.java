package org.agty.elfiumexpress;

import org.agty.elfiumexpress.config.LocalConfig;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlywayMigrationTests {
    private static final String DB_PREFIX = "db.default.";

    @Test
    void migrateCleanSchema() throws Exception {
        String schema = "flyway_test_" + UUID.randomUUID().toString().replace("-", "");

        try (Connection connection = openConnection()) {
            createSchema(connection, schema);

            Flyway flyway = Flyway.configure()
                    .dataSource(buildJdbcUrl(schema), dbUser(), dbPassword())
                    .schemas(schema)
                    .defaultSchema(schema)
                    .locations(LocalConfig.getString("db.flyway.locations", "classpath:db/migration"))
                    .baselineOnMigrate(false)
                    .createSchemas(true)
                    .load();

            flyway.migrate();
            flyway.validate();

            assertTrue(tableExists(connection, schema, "spring_users"));
            assertTrue(tableExists(connection, schema, "spring_groups"));
            assertTrue(tableExists(connection, schema, "spring_express"));
            assertTrue(tableExists(connection, schema, "spring_express_type"));
            assertTrue(tableExists(connection, schema, "spring_files"));
            assertTrue(tableExists(connection, schema, "spring_express_files"));
            assertTrue(tableExists(connection, schema, "spring_thumbs"));
            assertTrue(tableExists(connection, schema, "spring_users_session"));
            assertTrue(tableExists(connection, schema, "spring_users_session_attributes"));
            assertTrue(tableExists(connection, schema, "spring_roles"));
            assertTrue(tableExists(connection, schema, "spring_users_roles"));
            assertTrue(columnExists(connection, schema, "spring_files", "id_user"));

            assertEquals(3, countRows(connection, schema, "spring_express_type"));
            assertEquals(0, countRows(connection, schema, "spring_users"));
            assertEquals(0, countRows(connection, schema, "spring_groups"));
            assertEquals(2, countRows(connection, schema, "spring_roles"));
            assertEquals(0, countRows(connection, schema, "spring_users_roles"));
        } finally {
            try (Connection connection = openConnection()) {
                dropSchema(connection, schema);
            }
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(buildJdbcUrl(null), dbUser(), dbPassword());
    }

    private String buildJdbcUrl(String schema) {
        String base = "jdbc:postgresql://%s:%s/%s".formatted(
                requireString(DB_PREFIX + "server"),
                requireString(DB_PREFIX + "port"),
                requireString(DB_PREFIX + "database")
        );

        if (schema == null || schema.isBlank()) {
            return base;
        }

        return base + "?currentSchema=" + schema;
    }

    private String dbUser() {
        return requireString(DB_PREFIX + "user");
    }

    private String dbPassword() {
        return requireString(DB_PREFIX + "password");
    }

    private String requireString(String key) {
        String value = LocalConfig.getString(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }

    private void createSchema(Connection connection, String schema) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
        }
    }

    private void dropSchema(Connection connection, String schema) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
        }
    }

    private boolean tableExists(Connection connection, String schema, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT 1 FROM information_schema.tables " +
                             "WHERE table_schema = '%s' AND table_name = '%s'".formatted(schema, table)
             )) {
            return rs.next();
        }
    }

    private int countRows(Connection connection, String schema, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM %s.%s".formatted(schema, table))) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private boolean columnExists(Connection connection, String schema, String table, String column) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT 1 FROM information_schema.columns " +
                             "WHERE table_schema = '%s' AND table_name = '%s' AND column_name = '%s'"
                                     .formatted(schema, table, column)
             )) {
            return rs.next();
        }
    }
}
