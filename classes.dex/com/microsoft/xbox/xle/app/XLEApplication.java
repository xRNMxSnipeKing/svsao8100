package com.microsoft.xbox.xle.app;

import android.util.TypedValue;
import com.microsoft.smartglass.R;
import com.microsoft.smartglass.R.color;
import com.microsoft.smartglass.R.dimen;
import com.microsoft.smartglass.R.drawable;
import com.microsoft.smartglass.R.id;
import com.microsoft.smartglass.R.layout;
import com.microsoft.smartglass.R.raw;
import com.microsoft.smartglass.R.string;
import com.microsoft.smartglass.R.style;
import com.microsoft.smartglass.R.styleable;
import com.microsoft.xbox.service.network.managers.xblshared.XBLSharedServiceManager;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import com.microsoft.xbox.toolkit.system.SystemUtil;

public class XLEApplication extends XboxApplication {
    public static XLEActivity getMainActivity() {
        return (XLEActivity) MainActivity;
    }

    public void trackError(String errorMessage) {
        XboxMobileOmnitureTracking.TrackError(errorMessage);
    }

    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        XBLSharedServiceManager.setEnvironment(environment);
    }

    public void appInitializationCode() {
        super.appInitializationCode();
        XBLSharedServiceManager.initialize();
        ApplicationBarManager.getInstance().loadAnimations();
    }

    protected Class getStringRClass() {
        return string.class;
    }

    protected Class getDrawableRClass() {
        return drawable.class;
    }

    protected Class getRawRClass() {
        return raw.class;
    }

    protected Class getIdRClass() {
        return id.class;
    }

    protected Class getStyleRClass() {
        return style.class;
    }

    protected Class getStyleableRClass() {
        return styleable.class;
    }

    protected Class getLayoutRClass() {
        return layout.class;
    }

    protected Class getDimenRClass() {
        return dimen.class;
    }

    protected Class getColorRClass() {
        return color.class;
    }

    public boolean supportsButtonSounds() {
        return true;
    }

    public boolean getIsTablet() {
        return Resources.getBoolean(R.bool.isLandscapeOnly);
    }

    public boolean isAspectRatioLong() {
        TypedValue aspectRatioVal = new TypedValue();
        Resources.getValue(R.vals.widthHeightLongAspectRatio, aspectRatioVal, true);
        if (SystemUtil.getScreenWidthHeightAspectRatio() >= aspectRatioVal.getFloat()) {
            return true;
        }
        return false;
    }
}
