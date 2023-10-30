package org.simpleframework.xml.transform;

import java.math.BigDecimal;

class BigDecimalTransform implements Transform<BigDecimal> {
    BigDecimalTransform() {
    }

    public BigDecimal read(String value) {
        return new BigDecimal(value);
    }

    public String write(BigDecimal value) {
        return value.toString();
    }
}
