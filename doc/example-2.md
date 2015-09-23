# Example - Getting custom value

You can define a custom function for getting a custom value from configuration.
The function has to an input parameter of type ```Configuration``` and it has
to return the specified custom value. The function converts a configuration
node to the custom value.

## Configuration file

```plain
math {
    big_integer 1234567890098765432112345678900987654321;
}
```

## Source code

```java
import com.github.twoleds.configuration.Configuration;

import java.math.BigInteger;

public class Test {

    // it converts configuration node to an instance of BigInteger
    public static BigInteger toBigInteger(Configuration conf) {
        return new BigInteger(conf.getValue());
    }

    public static void main(String[] args) throws Exception {

        // parse configuration from a string/file/URL
        String confStr = "math { big_integer 1234567890098765432112345678900987654321; }";
        Configuration conf = Configuration.parse(confStr);

        // get a BigInteger value from the configuration
        BigInteger value = conf.query("math/big_integer", Test::toBigInteger, BigInteger.ZERO);

    }

}
```