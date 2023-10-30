package com.microsoft.xbox.xle.anim;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.AnimationFunctionType;
import com.microsoft.xbox.toolkit.anim.AnimationProperty;
import com.microsoft.xbox.toolkit.anim.BackEaseInterpolator;
import com.microsoft.xbox.toolkit.anim.EasingMode;
import com.microsoft.xbox.toolkit.anim.ExponentialInterpolator;
import com.microsoft.xbox.toolkit.anim.HeightAnimation;
import com.microsoft.xbox.toolkit.anim.SineInterpolator;
import com.microsoft.xbox.toolkit.anim.XLEInterpolator;
import org.simpleframework.xml.Attribute;

public class XLEAnimationDefinition {
    @Attribute(required = false)
    public int delayMs;
    @Attribute(required = false)
    public String dimen;
    @Attribute(required = false)
    public int durationMs;
    @Attribute(required = false)
    public EasingMode easing = EasingMode.EaseIn;
    @Attribute(required = false)
    public float from;
    @Attribute(required = false)
    public float parameter;
    @Attribute(required = false)
    public float pivotX = 0.5f;
    @Attribute(required = false)
    public float pivotY = 0.5f;
    @Attribute(required = false)
    public AnimationProperty property;
    @Attribute(required = false)
    public float to;
    @Attribute(required = false)
    public AnimationFunctionType type;

    public Animation getAnimation() {
        Interpolator interpolator = getInterpolator();
        Animation animation = null;
        switch (this.property) {
            case Alpha:
                animation = new AlphaAnimation(this.from, this.to);
                break;
            case Scale:
                animation = new ScaleAnimation(this.from, this.to, this.from, this.to, 1, this.pivotX, 1, this.pivotY);
                break;
            case PositionX:
                animation = new TranslateAnimation(1, this.from, 1, this.to, 1, 0.0f, 1, 0.0f);
                break;
            case PositionY:
                animation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, this.from, 1, this.to);
                break;
            case Height:
                int dimId = XboxApplication.MainActivity.findDimensionIdByName(this.dimen);
                int height = 0;
                if (dimId >= 0) {
                    height = XboxApplication.Resources.getDimensionPixelSize(dimId);
                }
                animation = new HeightAnimation(0, height);
                break;
        }
        if (animation == null) {
            return null;
        }
        animation.setDuration((long) this.durationMs);
        animation.setInterpolator(interpolator);
        animation.setStartOffset((long) this.delayMs);
        return animation;
    }

    private Interpolator getInterpolator() {
        switch (this.type) {
            case Sine:
                return new SineInterpolator(this.easing);
            case Exponential:
                return new ExponentialInterpolator(this.parameter, this.easing);
            case BackEase:
                return new BackEaseInterpolator(this.parameter, this.easing);
            default:
                return new XLEInterpolator(this.easing);
        }
    }
}
