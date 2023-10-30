package org.simpleframework.xml.transform;

import java.math.BigInteger;

class BigIntegerTransform implements Transform<BigInteger> {
    BigIntegerTransform() {
    }

    public BigInteger read(String value) {
        return new BigInteger(value);
    }

    public String write(BigInteger value) {
        return value.toString();
    }
}
