package com.github.twoleds.configuration;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Configuration {

    private final String name;
    private final String value;
    private final List<Configuration> children;

    /**
     * It parses configuration values direct from the specified input string.
     * If the input string is not valid it throws an exception.
     *
     * @throws ConfigurationException If the input string is not valid.
     */
    public static Configuration parse(String configuration) throws ConfigurationException {
        try (ConfigurationParser parser = new ConfigurationParser(new StringReader(configuration))) {
            return parser.parse();
        }
    }

    /**
     * It parses configuration values from the specified input file. If the
     * configuration from the file is not valid it throws an exception.
     *
     * @throws ConfigurationException If the input file is not valid.
     */
    public static Configuration parse(File file) throws ConfigurationException {
        try (ConfigurationParser parser = new ConfigurationParser(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return parser.parse();
        } catch (IOException e) {
            throw new ConfigurationException("An I/O error occurred.", e);
        }
    }

    /**
     * It parses configuration values from the specified input URL. If the
     * configuration from the URL is not valid it throws an exception.
     *
     * @throws ConfigurationException If the input file is not valid.
     */
    public static Configuration parse(URL url) throws ConfigurationException {
        try (ConfigurationParser parser = new ConfigurationParser(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return parser.parse();
        } catch (IOException e) {
            throw new ConfigurationException("An I/O error occurred.", e);
        }
    }

    /* package */ Configuration(String name, String value, List<Configuration> children) {
        this.name = name;
        this.value = value;
        this.children = children;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    private String[] parseCond(String[] path) {
        String[] cond = new String[path.length];
        for (int i = 0; i < path.length; i++) {
            int index = path[i].indexOf(':');
            if (index >= 0) {
                cond[i] = path[i].substring(index + 1);
                path[i] = path[i].substring(0, index);
            }
        }
        return cond;
    }

    /**
     * It finds and returns a sub-tree of this configuration. If the sub-tree
     * cannot be found by the specified <code>query</code> parameter it returns
     * a <code>null</code> value.
     */
    public Configuration query(String query) {
        return this.query(query, Function.identity());
    }

    /**
     * It finds, converts a returns a configuration value as a custom value.
     * If the configuration value cannot be found by the specified <code>query</code>
     * parameter it returns a <code>null</code> value. For conversion is used the
     * specified function from the parameter <code>convertFunc</code>.
     */
    public <T> T query(String query, Function<Configuration, T> convertFunc) {
        return this.query(query, convertFunc, null);
    }

    /**
     * It finds, converts and returns a configuration value as a custom value.
     * If the configuration value cannot be found by the specified parameter
     * <code>query</code> it returns a default value from the parameter
     * <code>defaultValue</code>. For conversion is used the specified function from
     * the parameter <code>convertFunc</code>.
     */
    public <T> T query(String query, Function<Configuration, T> convertFunc, T defaultValue) {
        return this.query(query.split("/"), convertFunc, defaultValue);
    }

    private <T> T query(String[] path, Function<Configuration, T> convertFunc, T defaultValue) {
        T result = this.query(path, this.parseCond(path), convertFunc, 0);
        return result != null ? result : defaultValue;
    }

    private <T> T query(String[] path, String[] cond, Function<Configuration, T> convertFunc, int level) {
        T result = null;
        if (this.children != null) {
            for (Configuration configuration : this.children) {
                if (configuration.getName().equals(path[level])) {
                    if (cond[level] == null || configuration.getValue().equals(cond[level])) {
                        if (level == path.length - 1) {
                            result = convertFunc.apply(configuration);
                            break;
                        } else {
                            result = configuration.query(path, cond, convertFunc, level + 1);
                            if (result != null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<Configuration> queryAll(String query) {
        return this.queryAll(query, Function.identity());
    }

    public <T> List<T> queryAll(String query, Function<Configuration, T> convertFunc) {
        return this.queryAll(query.split("/"), convertFunc);
    }

    private <T> List<T> queryAll(String[] path, Function<Configuration, T> convertFunc) {
        return this.queryAll(path, this.parseCond(path), convertFunc, new ArrayList<>(), 0);
    }

    private <T> List<T> queryAll(String[] path, String[] cond, Function<Configuration, T> convertFunc, List<T> result, int level) {
        if (this.children != null) {
            for (Configuration configuration : this.children) {
                if (configuration.getName().equals(path[level])) {
                    if (cond[level] == null || configuration.getValue().equals(cond[level])) {
                        if (level == path.length - 1) {
                            result.add(convertFunc.apply(configuration));
                        } else {
                            configuration.queryAll(path, cond, convertFunc, result, level + 1);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * It converts and returns a configuration value as a boolean value. If
     * the configuration value cannot be converted to a boolean value it
     * throws a runtime exception.
     */
    public Boolean getBoolean() {
        switch (this.value.toLowerCase()) {
            case "on":
            case "true":
            case "1":
                return true;
            case "off":
            case "false":
            case "0":
                return false;
            default:
                throw new NumberFormatException();
        }
    }

    /**
     * It finds, converts and returns a configuration value as a boolean
     * value. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns the <code>null</code> value. If the
     * configuration value cannot be converted to a boolean value it throws
     * a runtime exception.
     */
    public Boolean getBoolean(String query) {
        return this.getBoolean(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a boolean
     * value. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns a default value from the parameter
     * <code>defaultValue</code>. If the configuration value cannot be converted
     * to a boolean value it throws a runtime exception.
     */
    public Boolean getBoolean(String query, Boolean defaultValue) {
        return this.query(query, Configuration::getBoolean, defaultValue);
    }

    /**
     * It converts and returns a configuration value as a byte value. If the
     * configuration value cannot be converted to a byte value it throws
     * a runtime exception.
     */
    public Byte getByte() {
        return Byte.parseByte(this.value);
    }

    /**
     * It finds, converts and returns a configuration value as a byte value.
     * If the configuration value cannot be found by the specified <code>query</code>
     * parameter it returns the <code>null</code> value. If the configuration value
     * cannot be converted to a byte value it throws a runtime exception.
     */
    public Byte getByte(String query) {
        return this.getByte(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a byte value.
     * If the configuration value cannot be found by the specified <code>query</code>
     * parameter it returns a default value from the parameter
     * <code>defaultValue</code>. If the configuration value cannot be converted to
     * a byte value it throws a runtime exception.
     */
    public Byte getByte(String query, Byte defaultValue) {
        return this.query(query, Configuration::getByte, defaultValue);
    }

    /**
     * It converts and returns a configuration value as a character value.
     */
    public Character getCharacter() {
        return this.value.charAt(0);
    }

    /**
     * It finds, converts and returns a configuration value as a character
     * value. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns a <code>null</code> value.
     */
    public Character getCharacter(String query) {
        return this.getCharacter(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a character
     * value. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns a default value from the parameter
     * <code>defaultValue</code>.
     */
    public Character getCharacter(String query, Character defaultValue) {
        return this.query(query, Configuration::getCharacter, defaultValue);
    }

    /**
     * It converts and returns a configuration value as a number with double
     * precision (<code>double</code>). If the configuration value cannot be
     * converted to a number it throws a runtime exception.
     */
    public Double getDouble() {
        return Double.parseDouble(this.value);
    }

    /**
     * It finds, converts and returns a configuration value as a number with
     * double precision (<code>double</code>). If the configuration value cannot be
     * found by the specified <code>query</code> parameter it returns the <code>null</code>
     * value. If the configuration value cannot be converted to a number it
     * throws a runtime exception.
     */
    public Double getDouble(String query) {
        return this.getDouble(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a number with
     * double precision (<code>double</code>). If the configuration value cannot be
     * found by the specified <code>query</code> parameter it returns a default value
     * from the parameter <code>defaultValue</code>. If the configuration value
     * cannot be converted to a number it throws a runtime exception.
     */
    public Double getDouble(String query, Double defaultValue) {
        return this.query(query, Configuration::getDouble, defaultValue);
    }

    /**
     * It converts and returns a configuration value as a number with single
     * precision (float). If the configuration value cannot be converted to an
     * number it throws a runtime exception.
     */
    public Float getFloat() {
        return Float.parseFloat(this.value);
    }

    /**
     * It finds, converts and returns a configuration value as a number with
     * single precision (<code>float</code>). If the configuration value cannot be
     * found by the specified `<code>query</code> parameter it returns the <code>null</code>
     * value. If the configuration value cannot be converted to a number it
     * throws a runtime exception.
     */
    public Float getFloat(String query) {
        return this.getFloat(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a number with
     * single precision (<code>float</code>). If the configuration value cannot be
     * found by the specified <code>query</code> parameter it returns a default value
     * from the parameter <code>defaultValue</code>. If the configuration value
     * cannot be converted to a number it throws a runtime exception.
     */
    public Float getFloat(String query, Float defaultValue) {
        return this.query(query, Configuration::getFloat, defaultValue);
    }

    /**
     * It converts and returns a configuration value as an integer. If the
     * configuration value cannot be converted to an integer it throws
     * a runtime exception.
     */
    public Integer getInteger() {
        return Integer.parseInt(this.value);
    }

    /**
     * It finds, converts and returns a configuration value as an integer. If
     * the configuration value cannot be found by the specified <code>query</code>
     * parameter it returns the <code>null</code> value. If the configuration value
     * cannot be converted to an integer it throws a runtime exception.
     */
    public Integer getInteger(String query) {
        return this.getInteger(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as an integer.
     * If the configuration value cannot be found by the specified <code>query</code>
     * parameter it returns a default value from parameter <code>defaultValue</code>.
     * If the configuration value cannot be converted to an integer it throws
     * a runtime exception.
     */
    public Integer getInteger(String query, Integer defaultValue) {
        return this.query(query, Configuration::getInteger, defaultValue);
    }

    /**
     * It converts and returns a configuration value as a long integer. If
     * the configuration value cannot be converted to a long integer it
     * throws a runtime exception.
     */
    public Long getLong() {
        return Long.parseLong(this.value);
    }

    /**
     * It finds, converts and returns a configuration value as anlong
     * integer. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns the <code>null</code> value. If the
     * configuration value cannot be converted to a long integer it throws
     * a runtime exception.
     */
    public Long getLong(String query) {
        return this.getLong(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a long
     * integer. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns a default value from parameter
     * <code>defaultValue</code>. If the configuration value cannot be converted to
     * a long integer it throws a runtime exception.
     */
    public Long getLong(String query, Long defaultValue) {
        return this.query(query, Configuration::getLong, defaultValue);
    }

    /**
     * It converts and returns a configuration value as a short integer. If
     * the configuration value cannot be converted to a short integer it
     * throws a runtime exception.
     */
    public Short getShort() {
        return Short.parseShort(this.value);
    }

    /**
     * It finds, converts and returns a configuration value as a short
     * integer. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns the <code>null</code> value. If the
     * configuration value cannot be converted to a short integer it throws
     * a runtime exception.
     */
    public Short getShort(String query) {
        return this.getShort(query, null);
    }

    /**
     * It finds, converts and returns a configuration value as a short
     * integer. If the configuration value cannot be found by the specified
     * <code>query</code> parameter it returns a default value from the parameter
     * <code>defaultValue</code>. If the configuration value cannot be converted to
     * a short integer it throws a runtime exception.
     */
    public Short getShort(String query, Short defaultValue) {
        return this.query(query, Configuration::getShort, defaultValue);
    }

    /**
     * It returns a configuration value as a string value. It returns the
     * configuration value as-is without any conversion.
     */
    public String getString() {
        return this.value;
    }

    /**
     * It finds and returns a configuration value as a string value. If the
     * configuration value cannot be found by the specified <code>query</code>
     * parameter it returns the <code>null</code> value. It returns the configuration
     * value as-is without any conversion.
     */
    public String getString(String query) {
        return this.getString(query, null);
    }

    /**
     * It finds and returns a configuration value as a string value. If the
     * configuration value cannot be found by the specified <code>query</code>
     * parameter it returns a default value from the parameter
     * <code>defaultValue</code>. It returns the configuration value as-is without
     * any conversion.
     */
    public String getString(String query, String defaultValue) {
        return this.query(query, Configuration::getString, defaultValue);
    }

}
