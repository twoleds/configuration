package com.github.twoleds.configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationParser implements AutoCloseable {

    private final Reader reader;
    private final StringBuffer buffer;
    private State state;

    private int column;
    private int line;

    public ConfigurationParser(Reader reader) {
        this.reader = new BufferedReader(reader);
        this.buffer = new StringBuffer();
        this.state = State.START;

        this.column = 1;
        this.line = 1;
    }

    @Override
    public void close() throws ConfigurationException {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new ConfigurationException("An I/O error occurred.", e);
        }
    }

    public Configuration parse() throws ConfigurationException {
        return this.parse("", "");
    }

    private Configuration parse(String name, String value) throws ConfigurationException {

        String tmpName = "";
        String tmpValue = "";
        List<Configuration> tmpChildren = new ArrayList<>();

        try {
            for (int c = this.reader.read(); c >= 0; c = this.reader.read()) {
                switch (this.state) {

                    case COMMENT:

                        if (c == '\n') {
                            this.state = State.START;
                        }

                        break;

                    case NAME:

                        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '_')) {
                            this.buffer.append((char)c);
                            break;
                        }

                        if ((c == ' ') || (c == '\t')) {
                            tmpName = this.buffer.toString();
                            this.buffer.setLength(0);
                            this.state = State.VALUE_START;
                            break;
                        }

                        throw new ConfigurationException(
                                String.format(
                                        "An invalid character \"%c\" on line %d at column %d.",
                                        c, this.line, this.column
                                )
                        );

                    case START:

                        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                            break;
                        }

                        if ((c == '#')) {
                            this.state = State.COMMENT;
                            break;
                        }

                        if ((c == '}')) {
                            return new Configuration(name, value, tmpChildren);
                        }

                        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_')) {
                            this.state = State.NAME;
                            this.buffer.setLength(0);
                            this.buffer.append((char)c);
                            break;
                        }

                        throw new ConfigurationException(
                                String.format(
                                        "An invalid character \"%c\" on line %d at column %d.",
                                        c, this.line, this.column
                                )
                        );

                    case VALUE:

                        if ((c == '"')) {
                            tmpValue = this.buffer.toString();
                            this.buffer.setLength(0);
                            this.state = State.VALUE_END;
                            break;
                        }

                        if (c == '\\') {
                            this.state = State.VALUE_ESCAPE;
                            break;
                        }

                        this.buffer.append((char)c);
                        break;

                    case VALUE_DIRECT:

                        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '-') || (c == '.') || (c == '_')) {
                            this.buffer.append((char)c);
                            break;
                        }

                        if (c == ';') {
                            tmpValue = this.buffer.toString();
                            this.buffer.setLength(0);
                            tmpChildren.add(new Configuration(tmpName, tmpValue, null));
                            this.state = State.START;
                            break;
                        }

                        if (c == '{') {
                            tmpValue = this.buffer.toString();
                            this.buffer.setLength(0);
                            this.state = State.START;
                            tmpChildren.add(this.parse(tmpName, tmpValue));
                            break;
                        }

                        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                            tmpValue = this.buffer.toString();
                            this.buffer.setLength(0);
                            this.state = State.VALUE_END;
                            break;
                        }

                        throw new ConfigurationException(
                                String.format(
                                        "An invalid character \"%c\" on line %d at column %d.",
                                        c, this.line, this.column
                                )
                        );

                    case VALUE_END:

                        if ((c == ' ') || (c == '\t') || (c == '\r') || (c == '\n')) {
                            break;
                        }

                        if (c == '{') {
                            this.state = State.START;
                            tmpChildren.add(this.parse(tmpName, tmpValue));
                            break;
                        }

                        if ((c == ';')) {
                            tmpChildren.add(new Configuration(tmpName, tmpValue, null));
                            this.state = State.START;
                            break;
                        }

                        throw new ConfigurationException(
                                String.format(
                                        "An invalid character \"%c\" on line %d at column %d.",
                                        c, this.line, this.column
                                )
                        );

                    case VALUE_ESCAPE:

                        switch ((char)c) {
                            case '0':
                                this.buffer.append('\0');
                                break;
                            case '"':
                                this.buffer.append('"');
                                break;
                            case '\\':
                                this.buffer.append('\\');
                                break;
                            case 'b':
                                this.buffer.append('\b');
                                break;
                            case 'f':
                                this.buffer.append('\f');
                                break;
                            case 'n':
                                this.buffer.append('\n');
                                break;
                            case 'r':
                                this.buffer.append('\r');
                                break;
                            case 't':
                                this.buffer.append('\t');
                                break;
                            default:
                                throw new ConfigurationException(
                                        String.format(
                                                "An invalid character \"%c\" on line %d at column %d.",
                                                c, this.line, this.column
                                        )
                                );
                        }

                        this.state = State.VALUE;
                        break;

                    case VALUE_START:

                        if ((c == ' ') || (c == '\t')) {
                            break;
                        }

                        if ((c == '"')) {
                            this.state = State.VALUE;
                            break;
                        }

                        if ((c == '{')) {
                            this.state = State.START;
                            tmpChildren.add(this.parse(tmpName, tmpValue));
                            break;
                        }

                        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '-') || (c == '.') || (c == '_')) {
                            this.state = State.VALUE_DIRECT;
                            this.buffer.setLength(0);
                            this.buffer.append((char)c);
                            break;
                        }

                        throw new ConfigurationException(
                                String.format(
                                        "An invalid character \"%c\" on line %d at column %d.",
                                        c, this.line, this.column
                                )
                        );

                }

                if (c == '\n') {
                    this.column = 1;
                    this.line++;
                } else {
                    this.column++;
                }

            }

        } catch (IOException ex) {
            throw new ConfigurationException(
                    String.format("An I/O error occurred on line %d at column %d.", this.line, this.column), ex
            );
        }

        return new Configuration(name, value, tmpChildren);

    }

    private enum State {
        START,
        COMMENT,
        NAME,
        VALUE,
        VALUE_DIRECT,
        VALUE_START,
        VALUE_END,
        VALUE_ESCAPE,
    }

}
