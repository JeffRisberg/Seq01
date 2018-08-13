package com.company.common.config;

import com.google.common.annotations.VisibleForTesting;
import com.company.common.base.config.AppConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 */
public class EnvironmentBasedAppConfig implements AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentBasedAppConfig.class);

    @Override
    public String getString(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(translateKey(key))).orElse(defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getConvertedValue(System.getenv(translateKey(key)), defaultValue, Integer::parseInt);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getConvertedValue(System.getenv(translateKey(key)), defaultValue, Long::parseLong);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return getConvertedValue(System.getenv(translateKey(key)), defaultValue, Double::parseDouble);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getConvertedValue(System.getenv(translateKey(key)), defaultValue, Boolean::parseBoolean);
    }

    @Override
    public Iterator<String> getKeys(String prefix) {
        return System.getenv().keySet().stream().filter(k -> k.startsWith(prefix)).iterator();
    }

    @Override
    public Configuration getUnderlyingConfig() {
        Map<String, String> environmentConfig = System.getenv();
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        for (String k : environmentConfig.keySet()) {
            propertiesConfiguration.addProperty(k, environmentConfig.get(k));
        }

        return propertiesConfiguration;
    }

    @Override
    public void setOverrideProperty(String key, Object value) {
        throw new UnsupportedOperationException("setOverrideProperty method is not supported for environment configuration");
    }

    /**
     * As environment variables should be a word consisting only of alphanumeric characters and underscores,
     * and beginning with an alphabetic character or an  underscore. This utility is translating our archaius
     * config keys, replacing dots by underscores and putting all the key in uppercase.
     *
     * @param key configuration key
     * @return config key with dots replaced by underscores and in uppercase
     */
    @VisibleForTesting
    String translateKey(String key) {
        String translatedKey;
        if (key.indexOf('.') != -1) {
            translatedKey = key.replace('.', '_').toUpperCase();
        } else {
            translatedKey = key;
        }
        return translatedKey;
    }

    /**
     * Utility to get a value from a specific type from a given string.
     *
     * @param string       string to convert
     * @param defaultValue default value to use when key not defined or conversion error
     * @param converter    function that converts from the string to type T
     * @param <T>          type of the expected value
     * @return a value of type T from the given string
     */
    private <T> T getConvertedValue(String string, T defaultValue, Function<String, T> converter) {
        Optional<String> optValue = Optional.ofNullable(string);
        if (optValue.isPresent()) {
            try {
                return converter.apply(optValue.get());
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}