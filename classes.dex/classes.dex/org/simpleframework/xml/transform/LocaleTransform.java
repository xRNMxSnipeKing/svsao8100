package org.simpleframework.xml.transform;

import java.util.Locale;
import java.util.regex.Pattern;

class LocaleTransform implements Transform<Locale> {
    private final Pattern pattern = Pattern.compile("_");

    public Locale read(String locale) throws Exception {
        String[] list = this.pattern.split(locale);
        if (list.length >= 1) {
            return read(list);
        }
        throw new InvalidFormatException("Invalid locale %s", locale);
    }

    private Locale read(String[] locale) throws Exception {
        String[] list = new String[]{"", "", ""};
        for (int i = 0; i < list.length; i++) {
            if (i < locale.length) {
                list[i] = locale[i];
            }
        }
        return new Locale(list[0], list[1], list[2]);
    }

    public String write(Locale locale) {
        return locale.toString();
    }
}
