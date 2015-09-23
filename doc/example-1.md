# Example - Multiple database connections

This example shows a simple configuration of multiple database connections.
Every connections has own URL, username or password. The source code contains
simple method which returns a connection to database by the name of a database
from the configuration file. The method uses query to fetching configuration
values from the configuration file.

## Configuration file
```plain
database "first" {
    url "jdbc:mysql://10.0.0.10/first";
    username "testuser";
    password "testuser";
}

database "second" {
    url "jdbc:mysql://10.0.0.11/second";
    username "testuser";
}

database "third" {
    url "jdbc:mysql://10.0.0.12/third";
}
```

## Source code
```java
import com.github.twoleds.configuration.Configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {

    public static Connection getConnection(Configuration conf, String dbname) throws SQLException {
        return DriverManager.getConnection(
                conf.getString("database:" + dbname + "/url"), // for example: database:first/url
                conf.getString("database:" + dbname + "/username", ""),
                conf.getString("database:" + dbname + "/password", "")
        );
    }

    public static void main(String[] args) throws Exception {

        // load the configuration from a file
        Configuration conf = Configuration.parse(new File("/path/to/configuration/file.conf"));

        // returns a connection to the first database
        Connection first = getConnection(conf, "first");

        // returns a connection to the second database
        Connection second = getConnection(conf, "second");

        // returns a connection to the third database
        Connection thrid = getConnection(conf, "thrid");

    }

}
```