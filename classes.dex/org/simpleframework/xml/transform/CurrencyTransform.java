package org.simpleframework.xml.transform;

import java.util.Currency;

class CurrencyTransform implements Transform<Currency> {
    CurrencyTransform() {
    }

    public Currency read(String symbol) {
        return Currency.getInstance(symbol);
    }

    public String write(Currency currency) {
        return currency.toString();
    }
}
