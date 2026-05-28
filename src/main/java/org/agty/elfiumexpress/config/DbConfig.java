package org.agty.elfiumexpress.config;

import org.agty.agtysql.config.AgtySqlConfig;

public final class DbConfig {
    private static final String PREFIX = "db.default.";

    private DbConfig() {
    }

    private static String requireString(String key) {
        String value = LocalConfig.getString(PREFIX + key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + PREFIX + key);
        }
        return value;
    }

    private static int requireInt(String key) {
        String value = requireString(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer for config key: " + PREFIX + key + " = " + value, e);
        }
    }

    public static AgtySqlConfig getConfig() {
        return new AgtySqlConfig()
                .setDriver(LocalConfig.getString(PREFIX + "driver", "pgsql"))
                .setServer(requireString("server"))
                .setPort(requireInt("port"))
                .setUser(requireString("user"))
                .setPassword(requireString("password"))
                .setDatabase(requireString("database"))
                .setSchema(requireString("schema"))
                .setPfx(LocalConfig.getString(PREFIX + "pfx", ""))
                .setEncoding(LocalConfig.getString(PREFIX + "encoding", "UTF-8"))
                .setTimeZone(LocalConfig.getString(PREFIX + "serverTimeZone", "UTC"))
                .setLogQuery(LocalConfig.getBoolean(PREFIX + "logquery", false))
                .setDebug(LocalConfig.getBoolean(PREFIX + "debug", false));
    }
}
