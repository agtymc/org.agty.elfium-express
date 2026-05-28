package org.agty.elfiumexpress.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(LocalConfig.getString("session.datasource.pool-name", "spring-users-session-pool"));
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql://%s:%s/%s?currentSchema=%s".formatted(
                requireString("db.default.server"),
                requireString("db.default.port"),
                requireString("db.default.database"),
                requireString("db.default.schema")
        ));
        hikariConfig.setUsername(requireString("db.default.user"));
        hikariConfig.setPassword(requireString("db.default.password"));
        hikariConfig.setMaximumPoolSize(LocalConfig.getInt("session.datasource.maximum-pool-size", 10));
        hikariConfig.setMinimumIdle(LocalConfig.getInt("session.datasource.minimum-idle", 2));
        hikariConfig.setConnectionTimeout(LocalConfig.getLong("session.datasource.connection-timeout-ms", 30000));
        hikariConfig.setIdleTimeout(LocalConfig.getLong("session.datasource.idle-timeout-ms", 600000));
        hikariConfig.setMaxLifetime(LocalConfig.getLong("session.datasource.max-lifetime-ms", 1800000));
        hikariConfig.setAutoCommit(true);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        return new HikariDataSource(hikariConfig);
    }

    private String requireString(String key) {
        String value = LocalConfig.getString(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }
}
