package org.simpleframework.xml.transform;

import java.util.Date;

class DateTransform<T extends Date> implements Transform<T> {
    private final DateFactory<T> factory;

    public DateTransform(Class<T> type) throws Exception {
        this.factory = new DateFactory(type);
    }

    public synchronized T read(String text) throws Exception {
        Long time;
        time = Long.valueOf(DateType.getDate(text).getTime());
        return this.factory.getInstance(time);
    }

    public synchronized String write(T date) throws Exception {
        return DateType.getText(date);
    }
}
