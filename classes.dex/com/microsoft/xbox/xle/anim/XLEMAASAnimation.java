package com.microsoft.xbox.xle.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import com.microsoft.xbox.avatar.view.XLEAvatarAnimation;
import com.microsoft.xbox.avatar.view.XLEAvatarAnimationAction;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationAbsListView;
import com.microsoft.xbox.toolkit.anim.XLEAnimationView;
import com.microsoft.xbox.xle.app.XLEApplication;
import java.util.ArrayList;
import java.util.Iterator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class XLEMAASAnimation extends MAASAnimation {
    @Attribute(required = false)
    public XLEAvatarAnimationAction action;
    @ElementList(required = false)
    public ArrayList<XLEAnimationDefinition> animations;
    @Attribute(required = false)
    public boolean fillAfter = true;
    @Attribute(required = false)
    public int offsetMs;
    @Attribute(required = false)
    public TargetType target = TargetType.View;
    @Attribute(required = false)
    public String targetId = null;

    public enum TargetType {
        View,
        ListView,
        GridView,
        AvatarView
    }

    public XLEAnimation compile() {
        return compile(XLEApplication.getMainActivity().findViewByString(this.targetId));
    }

    public XLEAnimation compile(View targetView) {
        XLEAnimation compiled;
        AnimationSet animationSet = null;
        if (this.animations != null && this.animations.size() > 0) {
            animationSet = new AnimationSet(false);
            Iterator i$ = this.animations.iterator();
            while (i$.hasNext()) {
                Animation anim = ((XLEAnimationDefinition) i$.next()).getAnimation();
                if (anim != null) {
                    animationSet.addAnimation(anim);
                }
            }
        }
        switch (this.target) {
            case View:
                XLEAssert.assertNotNull(animationSet);
                compiled = new XLEAnimationView(animationSet);
                ((XLEAnimationView) compiled).setFillAfter(this.fillAfter);
                break;
            case ListView:
            case GridView:
                XLEAssert.assertNotNull(animationSet);
                compiled = new XLEAnimationAbsListView(new LayoutAnimationController(animationSet, ((float) this.offsetMs) / 1000.0f));
                break;
            case AvatarView:
                XLEAssert.assertNotNull(this.action);
                compiled = new XLEAvatarAnimation(this.action);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        compiled.setTargetView(targetView);
        return compiled;
    }
}
