# A library for hierarchical configuration files

A library for parsing and building hierarchical configuration files. Format of
configuration files is inspired by nginx configuration files and it's partially
compatible.

## Example
```
# Example configuration of an elasticsearch database
database testdb {
    type "elasticsearch";
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

## Usage
```java
Configuration conf = Configuration.parse(new File("path/to/configuration/file"));

System.out.printf("Configuration of the database \"%s\":%n", conf.getString("database"));
System.out.printf("\tType: %s%n", conf.getString("database/type"));
for (Configuration nodeConf : conf.queryAll("database/node")) {
    System.out.printf("\tConfiguration of the node \"%s\":%n", nodeConf.getString());
    System.out.printf("\t\tHost: %s%n", nodeConf.getString("host"));
    System.out.printf("\t\tPort: %d%n", nodeConf.getInteger("port"));
}
```

Output from console:
```
Configuration of the database "testdb":
    Type: elasticsearch
    Configuration of the node "node-00":
        Host: 192.168.1.10
        Port: 9200
    Configuration of the node "node-01":
        Host: 192.168.1.11
        Port: 9200
    Configuration of the node "node-02":
        Host: 192.168.1.12
        Port: 9200
```

## License
```
Copyright 2015 Jaroslav Kuba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
