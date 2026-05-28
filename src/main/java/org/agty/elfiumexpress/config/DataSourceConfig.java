package org.agty.elfiumexpress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://%s:%s/%s?currentSchema=%s".formatted(
                requireString("db.default.server"),
                requireString("db.default.port"),
                requireString("db.default.database"),
                requireString("db.default.schema")
        ));
        dataSource.setUsername(requireString("db.default.user"));
        dataSource.setPassword(requireString("db.default.password"));
        return dataSource;
    }

    private String requireString(String key) {
        String value = LocalConfig.getString(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }
}
