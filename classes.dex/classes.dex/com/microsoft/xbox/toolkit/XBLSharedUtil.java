package com.microsoft.xbox.toolkit;

import java.util.Scanner;
import java.util.regex.MatchResult;

public class XBLSharedUtil {
    private static int SECONDS_PER_HOUR = (SECONDS_PER_MINUTE * 60);
    private static int SECONDS_PER_MINUTE = 60;
    private static final long SECOND_TO_HUNDRED_NANOSECOND = ((long) Math.pow(10.0d, 7.0d));

    public static int durationStringToSeconds(String durationString) {
        if (durationString == null || durationString.length() == 0) {
            return 0;
        }
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        try {
            Scanner scanner = new Scanner(durationString);
            scanner.findInLine("PT(\\d+H)?(\\d+M)?(\\d+S)?");
            MatchResult result = scanner.match();
            for (int i = 1; i <= result.groupCount(); i++) {
                String group = result.group(i);
                if (!(group == null || group.length() == 0)) {
                    switch (group.charAt(group.length() - 1)) {
                        case 'H':
                            hours = JavaUtil.parseInteger(group.substring(0, group.length() - 1));
                            break;
                        case 'M':
                            minutes = JavaUtil.parseInteger(group.substring(0, group.length() - 1));
                            break;
                        case 'S':
                            seconds = JavaUtil.parseInteger(group.substring(0, group.length() - 1));
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IllegalStateException e) {
            XLELog.Error("XBLSharedUtil", "Couldn't parse duration string: " + durationString);
        }
        return ((SECONDS_PER_HOUR * hours) + (SECONDS_PER_MINUTE * minutes)) + seconds;
    }

    public static int durationStringToMinutes(String durationString) {
        return durationStringToSeconds(durationString) / SECONDS_PER_MINUTE;
    }

    public static long secondsToHundredNanoseconds(float seconds) {
        return (long) (((float) SECOND_TO_HUNDRED_NANOSECOND) * seconds);
    }

    public static long hundredNanosecondsToSeconds(long hundredNanoSeconds) {
        return hundredNanoSeconds / SECOND_TO_HUNDRED_NANOSECOND;
    }
}
