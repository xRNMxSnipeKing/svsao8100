package com.microsoft.xbox.toolkit.anim;

public class BackEaseInterpolator extends XLEInterpolator {
    private float amplitude;

    public BackEaseInterpolator(float amplitude, EasingMode easingMode) {
        super(easingMode);
        this.amplitude = amplitude;
    }

    protected float getInterpolationCore(float normalizedTime) {
        normalizedTime = (float) Math.max((double) normalizedTime, 0.0d);
        return (float) (((double) ((normalizedTime * normalizedTime) * normalizedTime)) - (((double) (this.amplitude * normalizedTime)) * Math.sin(((double) normalizedTime) * 3.141592653589793d)));
    }
}
