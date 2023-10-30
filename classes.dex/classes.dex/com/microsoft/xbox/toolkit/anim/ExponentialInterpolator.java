package com.microsoft.xbox.toolkit.anim;

public class ExponentialInterpolator extends XLEInterpolator {
    private float exponent;

    public ExponentialInterpolator(float exponent, EasingMode easingMode) {
        super(easingMode);
        this.exponent = exponent;
    }

    protected float getInterpolationCore(float normalizedTime) {
        return (float) ((Math.pow(2.718281828459045d, (double) (this.exponent * normalizedTime)) - 1.0d) / (Math.pow(2.718281828459045d, (double) this.exponent) - 1.0d));
    }
}
