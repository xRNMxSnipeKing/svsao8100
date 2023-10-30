package com.microsoft.xbox.toolkit.ui.appbar;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;

public class ApplicationBarView extends FrameLayout {
    private XLEButton[] appBarButtons;
    private LinearLayout iconButtonContainer;
    private LinearLayout menuButtonContainer;
    private ImageView menuOptionButton;
    private XLEUniformImageView nowPlayingTile;
    private ProgressBar progressBar;

    private class BlockTouchDelegate extends TouchDelegate {
        public BlockTouchDelegate(View delegateView) {
            super(new Rect(), delegateView);
        }

        public boolean onTouchEvent(MotionEvent event) {
            XLELog.Diagnostic("ApplicationBar", "Swallowing touch inside the linear layout");
            return true;
        }
    }

    public ApplicationBarView(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
        Initialize();
    }

    public ApplicationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XLEButton[] getAppBarButtons() {
        return this.appBarButtons;
    }

    public XLEUniformImageView getNowPlayingTile() {
        return this.nowPlayingTile;
    }

    public ImageView getMenuOptionButton() {
        return this.menuOptionButton;
    }

    public LinearLayout getIconButtonContainer() {
        return this.iconButtonContainer;
    }

    public LinearLayout getMenuButtonContainer() {
        return this.menuButtonContainer;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    private void Initialize() {
        setBackgroundColor(XboxApplication.Resources.getColor(XboxApplication.Instance.getColorRValue("appbarbackground")));
        this.appBarButtons = new XLEButton[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            XLEAssert.assertTrue("All children of ApplicationBar must be of XLEButton type.", getChildAt(i) instanceof XLEButton);
            this.appBarButtons[i] = (XLEButton) getChildAt(i);
        }
        removeAllViews();
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(XboxApplication.Instance.getLayoutRValue("expanded_appbar"), this, true);
        this.nowPlayingTile = (XLEUniformImageView) findViewById(XboxApplication.Instance.getIdRValue("expanded_appbar_now_playing_tile"));
        this.nowPlayingTile.setFixDimension(3, XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarHeight")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarHeight")));
        this.menuOptionButton = (ImageView) findViewById(XboxApplication.Instance.getIdRValue("expanded_appbar_menu_dots"));
        this.iconButtonContainer = (LinearLayout) findViewById(XboxApplication.Instance.getIdRValue("expanded_appbar_button_container"));
        this.iconButtonContainer.setTouchDelegate(new BlockTouchDelegate(this.iconButtonContainer));
        this.menuButtonContainer = (LinearLayout) findViewById(XboxApplication.Instance.getIdRValue("expanded_appbar_menu_container"));
        this.progressBar = (ProgressBar) findViewById(XboxApplication.Instance.getIdRValue("expanded_appbar_now_playing_connecting"));
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        Initialize();
    }

    public boolean isLayoutRequested() {
        boolean override = super.isLayoutRequested();
        if (isEnabled()) {
            return override;
        }
        XLELog.Diagnostic("ApplicationBarView", "Animating. Overriding isLayoutRequested to false");
        return false;
    }

    public void addIconButton(XLEButton button) {
        XLEAssert.assertFalse("Menu items should only be added to the overflow app bar", button instanceof AppBarMenuButton);
        LayoutParams params = new LayoutParams(button.getLayoutParams());
        params.width = XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarButtonWidth"));
        params.height = XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarButtonHeight"));
        params.setMargins(XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarButtonMarginLeft")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarMarginTop")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarButtonMarginRight")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarMarginBottom")));
        params.gravity = 17;
        this.iconButtonContainer.removeView(button);
        this.iconButtonContainer.addView(button, params);
    }

    public void addMenuButton(XLEButton button) {
        this.menuButtonContainer.removeView(button);
        this.menuButtonContainer.addView(button);
        button.setPadding(XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarMenuItemMarginLeft")), XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarMenuItemMarginTop")), 0, XboxApplication.Resources.getDimensionPixelSize(XboxApplication.Instance.getDimenRValue("applicationBarMenuItemMarginBottom")));
    }

    public void cleanup() {
        int i;
        for (i = 0; i < this.menuButtonContainer.getChildCount(); i++) {
            this.menuButtonContainer.getChildAt(i).setOnClickListener(null);
        }
        for (i = 0; i < this.iconButtonContainer.getChildCount(); i++) {
            this.iconButtonContainer.getChildAt(i).setOnClickListener(null);
        }
        this.menuOptionButton.setOnClickListener(null);
        this.nowPlayingTile.setOnClickListener(null);
        this.iconButtonContainer.removeAllViewsInLayout();
        this.menuButtonContainer.removeAllViewsInLayout();
    }

    public void setIsLoading(boolean loading) {
        if (loading) {
            this.progressBar.setVisibility(0);
        } else {
            this.progressBar.setVisibility(8);
        }
    }
}
