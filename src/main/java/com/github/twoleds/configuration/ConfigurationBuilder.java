package com.github.twoleds.configuration;

import java.io.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ConfigurationBuilder implements Closeable, Flushable {

    private final Writer writer;
    private int level;

    public ConfigurationBuilder(Writer writer) throws IOException {
        this.writer = new BufferedWriter(writer);
        this.level = 0;
    }

    public ConfigurationBuilder writeComment(String comment) throws IOException {
        if (comment.indexOf('\n') >= 0) {
            for (String commentLine : comment.split("\r?\n")) {
                this.writeComment(commentLine);
            }
        } else {
            this.writer.write('#');
            this.writer.write(' ');
            this.writer.write(comment);
        }
        return this;
    }

    public ConfigurationBuilder writeLine() throws IOException {
        return this.indent().newLine();
    }

    public <T> ConfigurationBuilder writeValue(String name, T value, Function<T, String> convertFunc) throws IOException {
        return this.writeValue(name, convertFunc.apply(value));
    }

    public ConfigurationBuilder writeValue(String name, boolean value) throws IOException {
        return this.writeValue(name, value ? "on" : "off");
    }

    public ConfigurationBuilder writeValue(String name, byte value) throws IOException {
        return this.writeValue(name, Byte.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, char value) throws IOException {
        return this.writeValue(name, Character.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, double value) throws IOException {
        return this.writeValue(name, Double.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, float value) throws IOException {
        return this.writeValue(name, Float.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, int value) throws IOException {
        return this.writeValue(name, Integer.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, long value) throws IOException {
        return this.writeValue(name, Long.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, short value) throws IOException {
        return this.writeValue(name, Short.toString(value));
    }

    public ConfigurationBuilder writeValue(String name, String value) throws IOException {
        return this.indent().name(name).space().value(value).semicolon().newLine();
    }

    public ConfigurationBuilder startSection(String name) throws IOException {
        return this.indent().name(name).space().openBracket().newLine();
    }

    public ConfigurationBuilder startSection(String name, boolean value) throws IOException {
        return this.startSection(name, value ? "on" : "off");
    }

    public ConfigurationBuilder startSection(String name, byte value) throws IOException {
        return this.startSection(name, Byte.toString(value));
    }

    public ConfigurationBuilder startSection(String name, char value) throws IOException {
        return this.startSection(name, Character.toString(value));
    }

    public ConfigurationBuilder startSection(String name, double value) throws IOException {
        return this.startSection(name, Double.toString(value));
    }

    public ConfigurationBuilder startSection(String name, float value) throws IOException {
        return this.startSection(name, Float.toString(value));
    }

    public ConfigurationBuilder startSection(String name, int value) throws IOException {
        return this.startSection(name, Integer.toString(value));
    }

    public ConfigurationBuilder startSection(String name, long value) throws IOException {
        return this.startSection(name, Long.toString(value));
    }

    public ConfigurationBuilder startSection(String name, short value) throws IOException {
        return this.startSection(name, Short.toString(value));
    }

    public ConfigurationBuilder startSection(String name, String value) throws IOException {
        return this.indent().name(name).space().value(value).space().openBracket().newLine().levelUp();
    }

    public ConfigurationBuilder closeSection() throws IOException {
        return this.levelDown().indent().closeBracket().newLine();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    private ConfigurationBuilder closeBracket() throws IOException {
        this.writer.write('}');
        return this;
    }

    private ConfigurationBuilder indent() throws IOException {
        for (int i = 0; i < this.level; i++) {
            this.writer.write('\t');
        }
        return this;
    }

    private ConfigurationBuilder levelDown() {
        this.level--;
        return this;
    }

    private ConfigurationBuilder levelUp() {
        this.level++;
        return this;
    }

    private ConfigurationBuilder name(String name) throws IOException {
        if (!this.isNameValid(name)) {
            throw new RuntimeException();
        }
        this.writer.write(name);
        return this;
    }

    private ConfigurationBuilder newLine() throws IOException {
        this.writer.write('\n');
        return this;
    }

    private ConfigurationBuilder openBracket() throws IOException {
        this.writer.write('{');
        return this;
    }

    private ConfigurationBuilder semicolon() throws IOException {
        this.writer.write(';');
        return this;
    }

    private ConfigurationBuilder space() throws IOException {
        this.writer.write(' ');
        return this;
    }

    private ConfigurationBuilder value(String value) throws IOException {
        if (this.isValueSafe(value)) {
            this.writer.write(value);
        } else {
            this.writer.write('"');
            for (int i = 0, l = value.length(); i < l; i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '\b':
                        this.writer.write("\\b");
                        break;
                    case '\f':
                        this.writer.write("\\f");
                        break;
                    case '\r':
                        this.writer.write("\\r");
                        break;
                    case '\n':
                        this.writer.write("\\n");
                        break;
                    case '\t':
                        this.writer.write("\\t");
                        break;
                    case '\\':
                        this.writer.write("\\\\");
                        break;
                    case '\"':
                        this.writer.write("\\\"");
                        break;
                    default:
                        this.writer.write(c);
                        break;
                }
            }
            this.writer.write('"');
        }
        return this;
    }

    private boolean isNameValid(String value) {
        for (int i = 0, l = value.length(); i < l; i++) {
            char c = value.charAt(i);
            if ((c >= 'a') && (c <= 'z')) continue;
            if ((c >= 'A') && (c <= 'Z')) continue;
            if ((c >= '0') && (c <= '9') && i > 0) continue;
            if ((c == '_')) continue;
            return false;
        }
        return true;
    }

    private boolean isValueSafe(String value) {
        for (int i = 0, l = value.length(); i < l; i++) {
            char c = value.charAt(i);
            if ((c >= 'a') && (c <= 'z')) continue;
            if ((c >= 'A') && (c <= 'Z')) continue;
            if ((c >= '0') && (c <= '9')) continue;
            if ((c == '_') || (c == '/') || (c == '.')) continue;
            if ((c == '[') || (c == ']') || (c == ':')) continue;
            return false;
        }
        return true;
    }

}
