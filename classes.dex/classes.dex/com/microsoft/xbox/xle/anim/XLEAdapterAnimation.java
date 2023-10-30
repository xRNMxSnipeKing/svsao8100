package com.microsoft.xbox.xle.anim;

import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import java.util.ArrayList;
import java.util.Iterator;
import org.simpleframework.xml.ElementList;

public class XLEAdapterAnimation extends MAASAnimation {
    @ElementList
    public ArrayList<XLEMAASAnimation> animations;

    public XLEAnimationPackage compile() {
        XLEAnimationPackage pack = new XLEAnimationPackage();
        Iterator i$ = this.animations.iterator();
        while (i$.hasNext()) {
            pack.add(((XLEMAASAnimation) i$.next()).compile());
        }
        return pack;
    }
}
