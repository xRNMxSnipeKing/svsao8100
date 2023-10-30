package com.microsoft.xbox.toolkit;

import android.graphics.Rect;
import android.text.format.DateUtils;
import android.view.View;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class JavaUtil {
    private static final String HEX_PREFIX = "0x";
    private static final Date MIN_DATE = new Date(100, 1, 1);

    public static class BooleanJSONDeserializer extends JsonDeserializer<Boolean> {
        public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            return Boolean.valueOf(JavaUtil.parseBoolean(parser.getText()));
        }
    }

    public static class HexLongJSONDeserializer extends JsonDeserializer<Long> {
        public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            return Long.valueOf(JavaUtil.parseHexLong(parser.getText()));
        }
    }

    public static String getShortClassName(Class cls) {
        String[] tokens = cls.getName().split("\\.");
        return tokens[tokens.length - 1];
    }

    public static boolean stringsEqual(String lhs, String rhs) {
        boolean z = true;
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == null || rhs == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return lhs.equals(rhs);
    }

    public static boolean stringsEqualCaseInsensitive(String lhs, String rhs) {
        boolean z = true;
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == null || rhs == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return lhs.equalsIgnoreCase(rhs);
    }

    public static boolean stringsEqualNonNullCaseInsensitive(String lhs, String rhs) {
        boolean z = true;
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return lhs.equalsIgnoreCase(rhs);
    }

    public static boolean tryParseBoolean(String booleanString, boolean defaultValue) {
        try {
            defaultValue = Boolean.parseBoolean(booleanString);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static String getLocalizedDateString(Date date) {
        try {
            return DateUtils.formatDateTime(XboxApplication.Instance.getApplicationContext(), date.getTime(), 131088);
        } catch (Exception e) {
            XLELog.Error("JavaUtil", "getLocalizedDateString: " + e.toString());
            return null;
        }
    }

    public static String getLocalizedDateStringValidated(Date date) {
        if (!date.after(MIN_DATE) || date.compareTo(new Date()) > 0) {
            return null;
        }
        return getLocalizedDateString(date);
    }

    public static String getDateStringAsMonthDateYear(Date date) {
        if (date != null) {
            return DateFormat.getDateInstance(1).format(date);
        }
        return "";
    }

    public static String getTimeStringMMSS(long timeInSeconds) {
        return DateUtils.formatElapsedTime(timeInSeconds);
    }

    public static int parseInteger(String intString) {
        int value = 0;
        try {
            value = Integer.parseInt(intString, 10);
        } catch (Exception e) {
        }
        return value;
    }

    private static boolean parseBoolean(String boolString) {
        boolean value = false;
        try {
            value = Boolean.parseBoolean(boolString);
        } catch (Exception e) {
        }
        return value;
    }

    private static long parseHexLong(String hexLong) {
        if (hexLong == null) {
            return 0;
        }
        if (hexLong.startsWith(HEX_PREFIX)) {
            return parseHexLongExpectHex(hexLong);
        }
        long j = 0;
        try {
            return Long.parseLong(hexLong, 10);
        } catch (Exception e) {
            return j;
        }
    }

    private static long parseHexLongExpectHex(String hexLong) {
        XLEAssert.assertTrue(hexLong.startsWith(HEX_PREFIX));
        long value = 0;
        try {
            value = Long.parseLong(hexLong.substring(HEX_PREFIX.length()), 16);
        } catch (Exception e) {
        }
        return value;
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isTouchPointInsideView(float touchRawX, float touchRawY, View view) {
        int[] coordinates = new int[2];
        view.getLocationOnScreen(coordinates);
        return new Rect(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight()).contains((int) touchRawX, (int) touchRawY);
    }

    public static String urlEncode(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String urlDecode(String src) {
        try {
            return URLDecoder.decode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String stringToUpper(String value) {
        if (value == null) {
            return null;
        }
        return value.toUpperCase();
    }

    public static boolean containsFlag(int value, int flagToCheck) {
        return (value & flagToCheck) == flagToCheck;
    }

    public static String concatenateStringsWithDelimiter(String str1, String str2, String str3, String delimiter) {
        return concatenateStringsWithDelimiter(str1, str2, str3, delimiter, true);
    }

    public static String concatenateStringsWithDelimiter(String str1, String str2, String str3, String delimiter, boolean addSpaceBeforeDelimiter) {
        delimiter = (addSpaceBeforeDelimiter ? MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR : "") + delimiter + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
        StringBuilder sb = new StringBuilder();
        if (!isNullOrEmpty(str1)) {
            sb.append(str1);
        }
        if (!isNullOrEmpty(str2)) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(str2);
        }
        if (!isNullOrEmpty(str3)) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(str3);
        }
        return sb.toString();
    }

    public static String concatenateStringsWithDelimiter(String delimiter, boolean addSpaceBeforeDelimiter, String... strs) {
        delimiter = (addSpaceBeforeDelimiter ? MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR : "") + delimiter + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
        StringBuilder sb = new StringBuilder();
        if (strs.length == 0) {
            return "";
        }
        for (int idx = 0; idx < strs.length; idx++) {
            if (!isNullOrEmpty(strs[idx])) {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                sb.append(strs[idx]);
            }
        }
        return sb.toString();
    }

    public static String concatenateUrlWithLinkAndParam(String link, String param, String tag) {
        StringBuffer buffer = new StringBuffer();
        if (!isNullOrEmpty(link)) {
            buffer.append(link);
        }
        if (!isNullOrEmpty(param)) {
            if (buffer.length() > 0) {
                buffer.append(tag);
            }
            buffer.append(param);
        }
        return buffer.toString();
    }

    public static Date JSONDateToJavaDate(String input) {
        if (isNullOrEmpty(input)) {
            return null;
        }
        String millistr = input.substring(6, input.length() - 2);
        String tzstr = null;
        int plusidx = millistr.indexOf(43);
        int minusidx = millistr.indexOf(45);
        if (plusidx >= 0) {
            tzstr = millistr.substring(plusidx + 1);
            millistr = millistr.substring(0, plusidx);
        }
        if (minusidx >= 0) {
            tzstr = millistr.substring(minusidx + 1);
            millistr = millistr.substring(0, minusidx);
        }
        if (tzstr != null) {
            XLEAssert.assertTrue(tzstr.equals("0000"));
        }
        return new Date(Long.parseLong(millistr));
    }

    public static <T> List<T> listIteratorToList(ListIterator<T> i) {
        ArrayList<T> rv = new ArrayList();
        while (i != null && i.hasNext()) {
            rv.add(i.next());
        }
        return rv;
    }
}
