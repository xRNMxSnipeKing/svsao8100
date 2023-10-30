package com.microsoft.xbox.xle.anim;

import android.view.View;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class XLEMAASAnimationPackageDirection extends MAASAnimation {
    @Element(required = false)
    public XLEMAASAnimation inAnimation;
    @Element(required = false)
    public XLEMAASAnimation outAnimation;

    public XLEAnimation compile(MAASAnimationType type, View targetView) {
        XLEMAASAnimation anim = type == MAASAnimationType.ANIMATE_IN ? this.inAnimation : this.outAnimation;
        if (anim == null) {
            return null;
        }
        return anim.compile(targetView);
    }
}
