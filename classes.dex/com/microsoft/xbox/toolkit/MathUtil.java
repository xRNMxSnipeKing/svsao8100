package com.microsoft.xbox.toolkit;

public class MathUtil {
    public static float positiveMod(float n, int d) {
        if (n < 0.0f) {
            n = (n % ((float) d)) + ((float) d);
            XLEAssert.assertTrue(n >= 0.0f);
        }
        return n % ((float) d);
    }

    public static int positiveMod(int n, int d) {
        if (n < 0) {
            n = (n % d) + d;
            XLEAssert.assertTrue(n >= 0);
        }
        return n % d;
    }

    public static float clamp(float v, float min, float max) {
        return Math.min(Math.max(v, min), max);
    }

    public static int clamp(int v, int min, int max) {
        return Math.min(Math.max(v, min), max);
    }
}
