package org.agty.elfiumexpress.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    private static final String PREFIX = "db.flyway.";
    private static final String DB_PREFIX = "db.default.";

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(
                        buildJdbcUrl(),
                        requireString(DB_PREFIX + "user"),
                        requireString(DB_PREFIX + "password")
                )
                .schemas(requireString(DB_PREFIX + "schema"))
                .defaultSchema(requireString(DB_PREFIX + "schema"))
                .locations(LocalConfig.getString(PREFIX + "locations", "classpath:db/migration"))
                .baselineOnMigrate(LocalConfig.getBoolean(PREFIX + "baseline-on-migrate", true))
                .baselineVersion(LocalConfig.getString(PREFIX + "baseline-version", "1"))
                .baselineDescription(LocalConfig.getString(PREFIX + "baseline-description", "Existing schema baseline"))
                .createSchemas(LocalConfig.getBoolean(PREFIX + "create-schemas", true))
                .load();
    }

    private String buildJdbcUrl() {
        return "jdbc:postgresql://%s:%s/%s?currentSchema=%s".formatted(
                requireString(DB_PREFIX + "server"),
                requireString(DB_PREFIX + "port"),
                requireString(DB_PREFIX + "database"),
                requireString(DB_PREFIX + "schema")
        );
    }

    private String requireString(String key) {
        String value = LocalConfig.getString(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }
}
