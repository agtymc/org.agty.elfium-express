package org.agty.elfiumexpress.config;

import org.agty.utils.AgtyUtils;

import java.util.Properties;

public final class LocalConfig {
    private static Properties properties;

    private LocalConfig() {
    }

    private static Properties getProperties() {
        if (properties == null) {
            properties = PropertyFactory.loadProperties("config/config.ini");
        }
        return properties;
    }

    public static String getString(String key) {
        return getProperties().getProperty(key);
    }

    public static String getString(String key, String defaultValue) {
        String value = getProperties().getProperty(key);
        return AgtyUtils.stringNonNullOrEmpty(value) ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key, Integer.toString(defaultValue)));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(getString(key, Long.toString(defaultValue)));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, Boolean.toString(defaultValue));
        return Boolean.parseBoolean(value);
    }
}
