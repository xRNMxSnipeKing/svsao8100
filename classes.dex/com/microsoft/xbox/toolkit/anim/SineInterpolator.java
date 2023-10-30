package com.microsoft.xbox.toolkit.anim;

public class SineInterpolator extends XLEInterpolator {
    public SineInterpolator(EasingMode easingMode) {
        super(easingMode);
    }

    protected float getInterpolationCore(float normalizedTime) {
        return (float) (1.0d - Math.sin((1.0d - ((double) normalizedTime)) * 1.5707963267948966d));
    }
}
