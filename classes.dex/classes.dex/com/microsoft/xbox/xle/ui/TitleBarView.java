package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import java.util.HashMap;

public class TitleBarView extends RelativeLayout {
    private SwitchPanel connectSwitchPanel;
    private XLEImageViewFast envelopeImage;
    private boolean hasUnreadMessages;
    private HashMap<String, XLEButton> headerViewMap = new HashMap();
    private LinearLayout headers;
    private boolean isTopLevel;
    private ImageView logoImage;
    private TextView messageCountText;
    private ProgressBar progressBar;
    private TextView titleText;

    public TitleBarView(Context context) {
        super(context);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.title_bar, this, true);
        setLayoutParams(new LayoutParams(-1, XboxApplication.Resources.getDimensionPixelSize(R.dimen.titleBarHeight)));
        this.progressBar = (ProgressBar) findViewById(R.id.title_bar_indeterminateProgressBar);
        this.progressBar.setVisibility(4);
        this.messageCountText = (TextView) findViewById(R.id.title_bar_messagecount);
        this.logoImage = (ImageView) findViewById(R.id.title_bar_xbox_logo);
        this.envelopeImage = (XLEImageViewFast) findViewById(R.id.title_bar_envelope);
        this.headers = (LinearLayout) findViewById(R.id.title_bar_headers);
        this.connectSwitchPanel = (SwitchPanel) findViewById(R.id.title_bar_connect_state_container);
        this.titleText = (TextView) findViewById(R.id.title_bar_title);
        setId(R.id.title_bar);
    }

    private void updateTitleBar() {
        int i;
        int i2 = 0;
        this.logoImage.setVisibility(this.isTopLevel ? 0 : 4);
        XLEImageViewFast xLEImageViewFast = this.envelopeImage;
        if (this.isTopLevel) {
            i = 0;
        } else {
            i = 8;
        }
        xLEImageViewFast.setVisibility(i);
        TextView textView = this.messageCountText;
        if (this.isTopLevel && this.hasUnreadMessages) {
            i = 0;
        } else {
            i = 8;
        }
        textView.setVisibility(i);
        if (this.connectSwitchPanel != null) {
            SwitchPanel switchPanel = this.connectSwitchPanel;
            if (!this.isTopLevel) {
                i2 = 8;
            }
            switchPanel.setVisibility(i2);
        }
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        throw new UnsupportedOperationException();
    }

    public void updateIsLoading(boolean isLoading) {
        this.progressBar.setVisibility(isLoading ? 0 : 4);
    }

    public void updateHasUnreadMessages(boolean hasUnreadMessages) {
        this.hasUnreadMessages = hasUnreadMessages;
        updateTitleBar();
    }

    public void updateIsTopLevel(boolean isTopLevel) {
        this.isTopLevel = isTopLevel;
        updateTitleBar();
    }

    public void updateConnectState(int connectState) {
        if (this.connectSwitchPanel != null) {
            this.connectSwitchPanel.setState(connectState);
        }
    }

    public void clearHeaders() {
        if (this.headers != null) {
            this.headers.removeAllViews();
            for (XLEButton button : this.headerViewMap.values()) {
                button.setOnClickListener(null);
            }
            this.headerViewMap.clear();
        }
    }

    public void addHeader(String headerName, int pivotHeaderIndex, OnClickListener listener) {
        if (this.headers != null && !JavaUtil.isNullOrEmpty(headerName)) {
            View v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.pivot_header, this.headers, false);
            if (v != null) {
                XLEButton text = (XLEButton) v.findViewById(R.id.pivot_header_text);
                if (text != null) {
                    text.setText(headerName);
                    text.setOnClickListener(listener);
                    this.headerViewMap.put(headerName, text);
                    if (this.headers.getChildCount() <= pivotHeaderIndex) {
                        this.headers.addView(v);
                    } else {
                        this.headers.addView(v, pivotHeaderIndex);
                    }
                }
            }
        }
    }

    public void setHeaderActive(String headerName) {
        if (this.headerViewMap.containsKey(headerName)) {
            ((XLEButton) this.headerViewMap.get(headerName)).setEnabled(true);
        }
    }

    public void setHeaderInactive(String headerName) {
        if (this.headerViewMap.containsKey(headerName)) {
            ((XLEButton) this.headerViewMap.get(headerName)).setEnabled(false);
        }
    }

    public void setTitle(String title) {
        if (this.titleText != null) {
            this.titleText.setText(JavaUtil.stringToUpper(title));
        }
    }

    public void onPause() {
    }

    public void onDestroy() {
    }
}
