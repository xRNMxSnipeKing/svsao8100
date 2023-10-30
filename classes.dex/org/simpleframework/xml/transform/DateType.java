package org.simpleframework.xml.transform;

import java.text.SimpleDateFormat;
import java.util.Date;

enum DateType {
    FULL("yyyy-MM-dd HH:mm:ss.S z"),
    LONG("yyyy-MM-dd HH:mm:ss z"),
    NORMAL("yyyy-MM-dd z"),
    SHORT("yyyy-MM-dd");
    
    private DateFormat format;

    private static class DateFormat {
        private SimpleDateFormat format;

        public DateFormat(String format) {
            this.format = new SimpleDateFormat(format);
        }

        public synchronized String getText(Date date) throws Exception {
            return this.format.format(date);
        }

        public synchronized Date getDate(String text) throws Exception {
            return this.format.parse(text);
        }
    }

    private DateType(String format) {
        this.format = new DateFormat(format);
    }

    private DateFormat getFormat() {
        return this.format;
    }

    public static String getText(Date date) throws Exception {
        return FULL.getFormat().getText(date);
    }

    public static Date getDate(String text) throws Exception {
        return getType(text).getFormat().getDate(text);
    }

    public static DateType getType(String text) {
        int length = text.length();
        if (length > 23) {
            return FULL;
        }
        if (length > 20) {
            return LONG;
        }
        if (length > 11) {
            return NORMAL;
        }
        return SHORT;
    }
}
