package com.github.twoleds.configuration;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationParserTest {

    @Test
    public void testParse() throws Exception {

        String confStr = "# Example configuration of an elasticsearch database\n" +
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
                "}\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getString("database"), "testdb");
        assertEquals(conf.getString("database/type"), "elasticsearch");
        assertEquals(conf.getString("database/node:node-00"), "node-00");
        assertEquals(conf.getString("database/node:node-00/host"), "192.168.1.10");
        assertEquals(conf.getShort("database/node:node-00/port"), Short.valueOf((short)9200));
        assertEquals(conf.getString("database/node:node-01"), "node-01");
        assertEquals(conf.getString("database/node:node-01/host"), "192.168.1.11");
        assertEquals(conf.getShort("database/node:node-01/port"), Short.valueOf((short)9200));
        assertEquals(conf.getString("database/node:node-02"), "node-02");
        assertEquals(conf.getString("database/node:node-02/host"), "192.168.1.12");
        assertEquals(conf.getShort("database/node:node-02/port"), Short.valueOf((short)9200));

    }

    @org.junit.Test
    public void testParseBoolean() throws Exception {

        String confStr = "test_boolean_true on;\ntest_boolean_false off;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getBoolean("test_boolean_true"), Boolean.TRUE);
        assertEquals(conf.getBoolean("test_boolean_false"), Boolean.FALSE);

    }

    @org.junit.Test
    public void testParseBoolean2() throws Exception {

        String confStr = "test_boolean_true 1;\ntest_boolean_false 0;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getBoolean("test_boolean_true"), Boolean.TRUE);
        assertEquals(conf.getBoolean("test_boolean_false"), Boolean.FALSE);

    }

    @org.junit.Test
    public void testParseBoolean3() throws Exception {

        String confStr = "test_boolean_true true;\ntest_boolean_false false;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getBoolean("test_boolean_true"), Boolean.TRUE);
        assertEquals(conf.getBoolean("test_boolean_false"), Boolean.FALSE);

    }

    @org.junit.Test
    public void testParseByte() throws Exception {

        String confStr = "test_byte_zero 0;\ntest_byte_max 127;\ntest_byte_min -128;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getByte("test_byte_zero"), Byte.valueOf((byte)0));
        assertEquals(conf.getByte("test_byte_max"), Byte.valueOf(Byte.MAX_VALUE));
        assertEquals(conf.getByte("test_byte_min"), Byte.valueOf(Byte.MIN_VALUE));

    }

    @org.junit.Test
    public void testParseCharacter() throws Exception {

        String confStr = "test_character_null \"\\0\";\ntest_character_new_line \"\\n\";\ntest_character_special \"䕅\";\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getCharacter("test_character_null"), Character.valueOf('\0'));
        assertEquals(conf.getCharacter("test_character_new_line"), Character.valueOf('\n'));
        assertEquals(conf.getCharacter("test_character_special"), Character.valueOf('䕅'));

    }

    @org.junit.Test
    public void testParseDouble() throws Exception {

        String confStr = "test_double_zero 0.0;\ntest_double_infinity Infinity;\ntest_double_nan NaN;\n" +
                "test_double_negative 4.9E-324;\ntest_double_positive 1.7976931348623157E308;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getDouble("test_double_zero"), Double.valueOf(0));
        assertEquals(conf.getDouble("test_double_infinity"), Double.valueOf(Double.POSITIVE_INFINITY));
        assertEquals(conf.getDouble("test_double_nan"), Double.valueOf(Double.NaN));
        assertEquals(conf.getDouble("test_double_negative"), Double.valueOf(Double.MIN_VALUE));
        assertEquals(conf.getDouble("test_double_positive"), Double.valueOf(Double.MAX_VALUE));

    }

    @org.junit.Test
    public void testParseFloat() throws Exception {

        String confStr = "test_float_zero 0.0;\ntest_float_infinity Infinity;\ntest_float_nan NaN;\n" +
                "test_float_negative 1.4E-45;\ntest_float_positive 3.4028235E38;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getFloat("test_float_zero"), Float.valueOf(0));
        assertEquals(conf.getFloat("test_float_infinity"), Float.valueOf(Float.POSITIVE_INFINITY));
        assertEquals(conf.getFloat("test_float_nan"), Float.valueOf(Float.NaN));
        assertEquals(conf.getFloat("test_float_negative"), Float.valueOf(Float.MIN_VALUE));
        assertEquals(conf.getFloat("test_float_positive"), Float.valueOf(Float.MAX_VALUE));

    }

    @org.junit.Test
    public void testParseInteger() throws Exception {

        String confStr = "test_integer_zero 0;\ntest_integer_max 2147483647;\ntest_integer_min -2147483648;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getInteger("test_integer_zero"), Integer.valueOf(0));
        assertEquals(conf.getInteger("test_integer_max"), Integer.valueOf(Integer.MAX_VALUE));
        assertEquals(conf.getInteger("test_integer_min"), Integer.valueOf(Integer.MIN_VALUE));

    }


    @org.junit.Test
    public void testParseLong() throws Exception {

        String confStr = "test_long_zero 0;\ntest_long_max 9223372036854775807;\ntest_long_min -9223372036854775808;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getLong("test_long_zero"), Long.valueOf(0));
        assertEquals(conf.getLong("test_long_max"), Long.valueOf(Long.MAX_VALUE));
        assertEquals(conf.getLong("test_long_min"), Long.valueOf(Long.MIN_VALUE));

    }


    @org.junit.Test
    public void testParseShort() throws Exception {

        String confStr = "test_short_zero 0;\ntest_short_max 32767;\ntest_short_min -32768;\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getShort("test_short_zero"), Short.valueOf((short) 0));
        assertEquals(conf.getShort("test_short_max"), Short.valueOf(Short.MAX_VALUE));
        assertEquals(conf.getShort("test_short_min"), Short.valueOf(Short.MIN_VALUE));

    }

    @org.junit.Test
    public void testParseString() throws Exception {

        String confStr = "test_string_empty \"\";\ntest_string_safe this_is_safe_string;\ntest_string_unsafe \"this is unsafe string\";\n";
        Configuration conf = Configuration.parse(confStr);

        assertEquals(conf.getString("test_string_empty"), "");
        assertEquals(conf.getString("test_string_safe"), "this_is_safe_string");
        assertEquals(conf.getString("test_string_unsafe"), "this is unsafe string");

    }
    
}