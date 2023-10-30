package com.microsoft.xbox.service.model.serialization;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class UTCDateConverter implements Converter<Date> {
    private static final int NO_MS_STRING_LENGTH = 19;
    private static SimpleDateFormat defaultFormatMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
    private static SimpleDateFormat defaultFormatNoMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    public static class UTCDateConverterJSONDeserializer extends JsonDeserializer<Date> {
        public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            return UTCDateConverter.convert(parser.getText());
        }
    }

    public static synchronized Date convert(String value) {
        Date date = null;
        synchronized (UTCDateConverter.class) {
            if (!JavaUtil.isNullOrEmpty(value)) {
                if (value.endsWith("Z")) {
                    value = value.replace("Z", "");
                }
                TimeZone timeZone = null;
                if (value.endsWith("+00:00")) {
                    value = value.replace("+00:00", "");
                } else if (value.endsWith("+01:00")) {
                    value = value.replace("+01:00", "");
                    timeZone = TimeZone.getTimeZone("GMT+01:00");
                } else if (value.contains(".") && value.endsWith("0000")) {
                    value = value.replace("0000", "");
                }
                boolean noMsDate = value.length() == 19;
                if (timeZone == null) {
                    timeZone = TimeZone.getTimeZone("GMT");
                }
                if (noMsDate) {
                    try {
                        defaultFormatNoMs.setTimeZone(timeZone);
                        date = defaultFormatNoMs.parse(value);
                    } catch (ParseException e) {
                        XLELog.Error("UTCDateConverter", e.toString());
                    }
                } else {
                    defaultFormatMs.setTimeZone(timeZone);
                    date = defaultFormatMs.parse(value);
                }
            }
        }
        return date;
    }

    public Date read(InputNode node) {
        try {
            return convert(node.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    public void write(OutputNode node, Date value) {
        node.setValue(defaultFormatNoMs.format(value));
    }
}
