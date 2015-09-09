package com.github.twoleds.configuration;

import java.io.StringWriter;

import static org.junit.Assert.*;

public class ConfigurationBuilderTest {

    @org.junit.Test
    public void testBuilder() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        // @formatter:off
        builder
            .writeComment("Example configuration of an elasticsearch database")
            .startSection("database", "testdb")
                .writeValue("type", "elasticsearch")
                .startSection("node", "node-00")
                    .writeValue("host", "192.168.1.10")
                    .writeValue("port", 9200)
                    .closeSection()
                .startSection("node", "node-01")
                    .writeValue("host", "192.168.1.11")
                    .writeValue("port", 9200)
                    .closeSection()
                .startSection("node", "node-02")
                    .writeValue("host", "192.168.1.12")
                    .writeValue("port", 9200)
                    .closeSection()
            .closeSection();
        // @formatter:on

        builder.flush();
        builder.close();

        assertEquals(
                "# Example configuration of an elasticsearch database\n" +
                        "database testdb {\n" +
                        "\ttype elasticsearch;\n" +
                        "\tnode node-00 {\n" +
                        "\t\thost 192.168.1.10;\n" +
                        "\t\tport 9200;\n" +
                        "\t}\n" +
                        "\tnode node-01 {\n" +
                        "\t\thost 192.168.1.11;\n" +
                        "\t\tport 9200;\n" +
                        "\t}\n" +
                        "\tnode node-02 {\n" +
                        "\t\thost 192.168.1.12;\n" +
                        "\t\tport 9200;\n" +
                        "\t}\n" +
                        "}\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteBoolean() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_boolean_true", true);
        builder.writeValue("test_boolean_false", false);

        builder.flush();
        builder.close();

        assertEquals(
                "test_boolean_true on;\ntest_boolean_false off;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteByte() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_byte_zero", (byte)0);
        builder.writeValue("test_byte_max", Byte.MAX_VALUE);
        builder.writeValue("test_byte_min", Byte.MIN_VALUE);

        builder.flush();
        builder.close();

        assertEquals(
                "test_byte_zero 0;\ntest_byte_max 127;\ntest_byte_min -128;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteCharacter() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_character_null", '\0');
        builder.writeValue("test_character_new_line", '\n');
        builder.writeValue("test_character_special", '\u4545');

        builder.flush();
        builder.close();

        assertEquals(
                "test_character_null \"\\0\";\ntest_character_new_line \"\\n\";\ntest_character_special \"䕅\";\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteComment() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeComment("One line comment");
        builder.writeLine();
        builder.writeComment("Multiline comment\nFirst line\nSecond line");

        builder.flush();
        builder.close();

        assertEquals(
                "# One line comment\n\n# Multiline comment\n# First line\n# Second line\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteDouble() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_double_zero", 0.0);
        builder.writeValue("test_double_infinity", Double.POSITIVE_INFINITY);
        builder.writeValue("test_double_nan", Double.NaN);
        builder.writeValue("test_double_negative", Double.MIN_VALUE);
        builder.writeValue("test_double_positive", Double.MAX_VALUE);

        builder.flush();
        builder.close();

        assertEquals(
                "test_double_zero 0.0;\ntest_double_infinity Infinity;\ntest_double_nan NaN;\n" +
                        "test_double_negative 4.9E-324;\ntest_double_positive 1.7976931348623157E308;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteFloat() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_float_zero", (float)0.0);
        builder.writeValue("test_float_infinity", Float.POSITIVE_INFINITY);
        builder.writeValue("test_float_nan", Float.NaN);
        builder.writeValue("test_float_negative", Float.MIN_VALUE);
        builder.writeValue("test_float_positive", Float.MAX_VALUE);

        builder.flush();
        builder.close();

        assertEquals(
                "test_float_zero 0.0;\ntest_float_infinity Infinity;\ntest_float_nan NaN;\n" +
                        "test_float_negative 1.4E-45;\ntest_float_positive 3.4028235E38;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteInteger() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_integer_zero", 0);
        builder.writeValue("test_integer_max", Integer.MAX_VALUE);
        builder.writeValue("test_integer_min", Integer.MIN_VALUE);

        builder.flush();
        builder.close();

        assertEquals(
                "test_integer_zero 0;\ntest_integer_max 2147483647;\ntest_integer_min -2147483648;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteLong() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_long_zero", (long)0);
        builder.writeValue("test_long_max", Long.MAX_VALUE);
        builder.writeValue("test_long_min", Long.MIN_VALUE);

        builder.flush();
        builder.close();

        assertEquals(
                "test_long_zero 0;\ntest_long_max 9223372036854775807;\ntest_long_min -9223372036854775808;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteShort() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_short_zero", (short)0);
        builder.writeValue("test_short_max", Short.MAX_VALUE);
        builder.writeValue("test_short_min", Short.MIN_VALUE);

        builder.flush();
        builder.close();

        assertEquals(
                "test_short_zero 0;\ntest_short_max 32767;\ntest_short_min -32768;\n",
                writer.toString()
        );

    }

    @org.junit.Test
    public void testWriteString() throws Exception {

        StringWriter writer = new StringWriter();
        ConfigurationBuilder builder = new ConfigurationBuilder(writer);

        builder.writeValue("test_string_empty", "");
        builder.writeValue("test_string_safe", "this_is_safe_string");
        builder.writeValue("test_string_unsafe", "this is unsafe string");

        builder.flush();
        builder.close();

        assertEquals(
                "test_string_empty \"\";\ntest_string_safe this_is_safe_string;\ntest_string_unsafe \"this is unsafe string\";\n",
                writer.toString()
        );

    }

}