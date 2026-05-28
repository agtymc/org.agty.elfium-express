package org.agty.elfiumexpress.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class PropertyFactory {
    private PropertyFactory() {
    }

    public static Properties loadProperties(String filename) {
        Properties propertyFile = new Properties();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String section = "";
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith(";")) {
                    continue;
                }

                if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    section = trimmed.substring(1, trimmed.length() - 1).trim();
                    continue;
                }

                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex < 0) {
                    continue;
                }

                String key = trimmed.substring(0, separatorIndex).trim();
                String value = trimmed.substring(separatorIndex + 1).trim();

                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                if (!section.isEmpty() && !key.contains(".")) {
                    key = section + "." + key;
                }

                propertyFile.setProperty(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load properties from " + filename, e);
        }

        return propertyFile;
    }
}
