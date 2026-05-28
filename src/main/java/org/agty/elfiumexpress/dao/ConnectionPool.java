package org.agty.elfiumexpress.dao;

import org.agty.elfiumexpress.config.DbConfig;
import org.agty.elfiumexpress.config.LocalConfig;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConnectionPool {
    private static final int DEFAULT_MAX_POOL_SIZE = 32;
    private static final int DEFAULT_MAX_LIFETIME_MIN = 30;
    private static final long DEFAULT_BORROW_TIMEOUT_MS = 300L;
    private static final Map<String, AgtySQLPool> POOLS = new ConcurrentHashMap<>();

    static {
        PoolDbConfigFactory.register("default", DbConfig::getConfig);
    }

    private ConnectionPool() {
    }

    private static int getConfiguredInt(String key, int defaultValue, int minValue) {
        return Math.max(minValue, LocalConfig.getInt(key, defaultValue));
    }

    private static long getConfiguredLong(String key, long defaultValue, long minValue) {
        return Math.max(minValue, LocalConfig.getLong(key, defaultValue));
    }

    private static AgtySQLPool createPool(String poolName) {
        String normalizedName = normalizedName(poolName);
        String keyPrefix = "db.pool." + normalizedName;
        return new AgtySQLPool(
                PoolDbConfigFactory.getConfig(normalizedName),
                getConfiguredInt(keyPrefix + ".max.size", DEFAULT_MAX_POOL_SIZE, 1),
                Duration.ofMinutes(getConfiguredInt(keyPrefix + ".max.lifetime.min", DEFAULT_MAX_LIFETIME_MIN, 1)),
                Duration.ofMillis(getConfiguredLong(keyPrefix + ".borrow.timeout.ms", DEFAULT_BORROW_TIMEOUT_MS, 1L))
        );
    }

    public static AgtySQLPool get(String name) {
        String key = normalizedName(name);
        return POOLS.computeIfAbsent(key, ConnectionPool::createPool);
    }

    private static String normalizedName(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Connection pool name must not be empty");
        }
        return trimmed;
    }

    public static final AgtySQLPool POOL = new AgtySQLPool(
            DbConfig.getConfig(),
            getConfiguredInt("db.pool.max.size", DEFAULT_MAX_POOL_SIZE, 1),
            Duration.ofMinutes(getConfiguredInt("db.pool.max.lifetime.min", DEFAULT_MAX_LIFETIME_MIN, 1)),
            Duration.ofMillis(getConfiguredLong("db.pool.borrow.timeout.ms", DEFAULT_BORROW_TIMEOUT_MS, 1L))
    );
}
