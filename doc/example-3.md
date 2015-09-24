# Example - Convert to a structure

You can convert any configuration tree to an own structure. This example shows
it how to do it. The method ```toElasticConfig``` does the conversion from
a configuration sub-tree to the custom structure ```ElasticConfig``` and
the ```ElasticNodeConfig```.

## Configuration file

```plain
database testdb {
    node "node-00" {
        host "192.168.1.10";
        port 9200;
    }
    node "node-01" {
        host "192.168.1.11";
        port 9200;
    }
    node "node-02" {
        host "192.168.1.12";
        port 9200;
    }
}
```

## Source code

```java
import com.github.twoleds.configuration.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {

        String confStr = "database testdb {\n" +
                "    node \"node-00\" {\n" +
                "        host \"192.168.1.10\";\n" +
                "        port 9200;\n" +
                "    }\n" +
                "    node \"node-01\" {\n" +
                "        host \"192.168.1.11\";\n" +
                "        port 9200;\n" +
                "    }\n" +
                "    node \"node-02\" {\n" +
                "        host \"192.168.1.12\";\n" +
                "        port 9200;\n" +
                "    }\n" +
                "}";
        Configuration conf = Configuration.parse(confStr);

        // convert a configuration sub-tree to a custom structure
        ElasticConfig config = conf.query("database", Test::toElasticConfig);

        // print values from the custom structure
        System.out.println("Name of database: " + config.getName());
        for (ElasticNodeConfig nodeConfig : config.getNodes()) {
            System.out.printf(
                    "\tNode: %s Host: %s Port: %d%n",
                    nodeConfig.getName(),
                    nodeConfig.getHost(),
                    nodeConfig.getPort()
            );
        }

    }

    // conversion function creates from a configuration sub-tree
    // a custom configuration structure
    public static ElasticConfig toElasticConfig(Configuration conf) {
        ElasticConfig c = new ElasticConfig(conf.getString()); // testdb
        for (Configuration nodeConf : conf.queryAll("node")) {
            c.nodes.add(new ElasticNodeConfig(
                    nodeConf.getString(), // node-0X
                    nodeConf.getString("host", ""), // 192.168.1.1X
                    nodeConf.getInteger("port", 9200) // 9200
            ));
        }
        return c;
    }

    public static class ElasticConfig {

        private final String name;
        private final List<ElasticNodeConfig> nodes;

        ElasticConfig(String name) {
            this.name = name;
            this.nodes = new ArrayList<>();
        }

        public String getName() {
            return this.name;
        }

        public List<ElasticNodeConfig> getNodes() {
            return Collections.unmodifiableList(this.nodes);
        }

    }

    public static class ElasticNodeConfig {

        private final String name;
        private final String host;
        private final int port;

        ElasticNodeConfig(String name, String host, int port) {
            this.name = name;
            this.host = host;
            this.port = port;
        }

        public String getName() {
            return this.name;
        }

        public String getHost() {
            return this.host;
        }

        public int getPort() {
            return this.port;
        }

    }

}
```