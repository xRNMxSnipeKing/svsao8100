package com.microsoft.xbox.toolkit.anim;

import android.view.animation.Interpolator;

public abstract class XLEAnimationReversible extends XLEAnimation {
    private float currentAlpha = 0.0f;

    private class AnimationReversibleInterpolator implements Interpolator {
        private Interpolator interpolator = null;

        public AnimationReversibleInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public float getInterpolation(float t) {
            if (t < 0.0f || t > 1.0f) {
                throw new IllegalArgumentException("should respect 0<=t<=1");
            }
            XLEAnimationReversible.this.currentAlpha = t;
            return this.interpolator.getInterpolation(t);
        }
    }

    private class AnimationReversibleInterpolatorReversed implements Interpolator {
        private Interpolator interpolator = null;
        private float tOffset = 0.0f;

        public AnimationReversibleInterpolatorReversed(float tOffset, Interpolator interpolator) {
            this.tOffset = tOffset;
            this.interpolator = interpolator;
        }

        protected float getReversedT(float t) {
            if (t >= 0.0f && t <= 1.0f) {
                return this.tOffset * (1.0f - t);
            }
            throw new IllegalArgumentException("should respect 0<=t<=1");
        }

        public float getInterpolation(float t) {
            if (t >= 0.0f && t <= 1.0f) {
                return this.interpolator.getInterpolation(getReversedT(t));
            }
            throw new IllegalArgumentException("should respect 0<=t<=1");
        }
    }

    public abstract void clear();

    public abstract void start();

    public float getCurrentAlpha() {
        return this.currentAlpha;
    }

    public Interpolator getInterpolatorForward(Interpolator underlyingEquation) {
        return new AnimationReversibleInterpolator(underlyingEquation);
    }
}
