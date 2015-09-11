package com.github.twoleds.configuration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Configuration {

    private final String name;
    private final String value;
    private final List<Configuration> children;

    public static Configuration parse(String configuration) throws ConfigurationException {
        try (ConfigurationParser parser = new ConfigurationParser(new StringReader(configuration))) {
            return parser.parse();
        }
    }

    public static Configuration parse(File file) throws ConfigurationException {
        try (ConfigurationParser parser = new ConfigurationParser(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return parser.parse();
        } catch (IOException e) {
            throw new ConfigurationException("An I/O error occurred.", e);
        }
    }

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

    public Configuration query(String query) {
        return this.query(query, Function.identity());
    }

    public <T> T query(String query, Function<Configuration, T> convertFunc) {
        return this.query(query, convertFunc, null);
    }

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
                return null;
        }
    }

    public Boolean getBoolean(String query) {
        return this.getBoolean(query, null);
    }

    public Boolean getBoolean(String query, Boolean defaultValue) {
        return this.query(query, Configuration::getBoolean, defaultValue);
    }

    public Byte getByte() {
        return Byte.parseByte(this.value);
    }

    public Byte getByte(String query) {
        return this.getByte(query, null);
    }

    public Byte getByte(String query, Byte defaultValue) {
        return this.query(query, Configuration::getByte, defaultValue);
    }

    public Character getCharacter() {
        return this.value.charAt(0);
    }

    public Character getCharacter(String query) {
        return this.getCharacter(query, null);
    }

    public Character getCharacter(String query, Character defaultValue) {
        return this.query(query, Configuration::getCharacter, defaultValue);
    }

    public Double getDouble() {
        return Double.parseDouble(this.value);
    }

    public Double getDouble(String query) {
        return this.getDouble(query, null);
    }

    public Double getDouble(String query, Double defaultValue) {
        return this.query(query, Configuration::getDouble, defaultValue);
    }

    public Float getFloat() {
        return Float.parseFloat(this.value);
    }

    public Float getFloat(String query) {
        return this.getFloat(query, null);
    }

    public Float getFloat(String query, Float defaultValue) {
        return this.query(query, Configuration::getFloat, defaultValue);
    }

    public Integer getInteger() {
        return Integer.parseInt(this.value);
    }

    public Integer getInteger(String query) {
        return this.getInteger(query, null);
    }

    public Integer getInteger(String query, Integer defaultValue) {
        return this.query(query, Configuration::getInteger, defaultValue);
    }

    public Long getLong() {
        return Long.parseLong(this.value);
    }

    public Long getLong(String query) {
        return this.getLong(query, null);
    }

    public Long getLong(String query, Long defaultValue) {
        return this.query(query, Configuration::getLong, defaultValue);
    }

    public Short getShort() {
        return Short.parseShort(this.value);
    }

    public Short getShort(String query) {
        return this.getShort(query, null);
    }

    public Short getShort(String query, Short defaultValue) {
        return this.query(query, Configuration::getShort, defaultValue);
    }

    public String getString() {
        return this.value;
    }

    public String getString(String query) {
        return this.getString(query, null);
    }

    public String getString(String query, String defaultValue) {
        return this.query(query, Configuration::getString, defaultValue);
    }

}
