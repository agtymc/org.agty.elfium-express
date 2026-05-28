package org.agty.elfiumexpress.dao;

import org.agty.agtysql.config.AgtySqlConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class PoolDbConfigFactory {
    private static final Map<String, Supplier<AgtySqlConfig>> PROVIDERS = new ConcurrentHashMap<>();

    private PoolDbConfigFactory() {
    }

    public static void register(String poolName, Supplier<AgtySqlConfig> provider) {
        String key = normalize(poolName);
        if (provider == null) {
            throw new IllegalArgumentException("Config provider must not be null");
        }
        PROVIDERS.put(key, provider);
    }

    public static AgtySqlConfig getConfig(String poolName) {
        String key = normalize(poolName);
        Supplier<AgtySqlConfig> provider = PROVIDERS.get(key);
        if (provider == null) {
            throw new IllegalStateException("No DB config provider registered for pool: " + key);
        }
        AgtySqlConfig config = provider.get();
        if (config == null) {
            throw new IllegalStateException("DB config provider returned null for pool: " + key);
        }
        return config;
    }

    private static String normalize(String name) {
        String value = name == null ? "" : name.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Pool name must not be empty");
        }
        return value;
    }
}
