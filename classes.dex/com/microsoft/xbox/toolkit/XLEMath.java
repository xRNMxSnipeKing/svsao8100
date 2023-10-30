package com.microsoft.xbox.toolkit;

public class XLEMath {
    public static boolean isPowerOf2(int value) {
        return value > 0 && ((value - 1) & value) == 0;
    }

    public static int signum(long v) {
        if (v > 0) {
            return 1;
        }
        if (v < 0) {
            return -1;
        }
        return 0;
    }

    public static int int32LowBit(int x) {
        if (x == 0) {
            return 0;
        }
        int zeros = 0;
        while ((x & 1) == 0) {
            zeros++;
            x >>= 1;
        }
        return 1 << zeros;
    }
}
