package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.XLEApplication;

public class PrivacySettingView extends LinearLayout {
    private String description;
    private TextView descriptionView;
    private String header;
    private TextView headerView;
    private OnSelectionChanged onSelectionChanged;
    private XLEButton optionBlocked;
    private XLEButton optionEveryone;
    private XLEButton optionFriends;
    private int selectedColor;
    private int selectedOption = -1;
    private int unselectedColor;

    public interface OnSelectionChanged {
        void run(int i);
    }

    public PrivacySettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PrivacySettingView);
        this.header = a.getString(0);
        this.description = a.getString(1);
        this.selectedColor = XLEApplication.Resources.getColor(R.color.XboxGreen);
        this.unselectedColor = XLEApplication.Resources.getColor(R.color.blockinggray);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.privacy_setting_item, this, true);
        setLayoutParams(new LayoutParams(-1, -2));
        this.headerView = (TextView) findViewById(R.id.privacy_setting_title);
        this.descriptionView = (TextView) findViewById(R.id.privacy_setting_description);
        this.optionEveryone = (XLEButton) findViewById(R.id.privacy_setting_everyone);
        this.optionFriends = (XLEButton) findViewById(R.id.privacy_setting_friends);
        this.optionBlocked = (XLEButton) findViewById(R.id.privacy_setting_blocked);
    }

    public PrivacySettingView(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    protected void onFinishInflate() {
        this.headerView.setText(this.header);
        this.descriptionView.setText(this.description);
        this.optionEveryone.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PrivacySettingView.this.setSelectedOption(0);
            }
        });
        this.optionFriends.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PrivacySettingView.this.setSelectedOption(1);
            }
        });
        this.optionBlocked.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PrivacySettingView.this.setSelectedOption(2);
            }
        });
    }

    public void setSelectedOption(int id) {
        String str = "Privacy options should be 0, 1, or 2";
        boolean z = id >= 0 && id <= 2;
        XLEAssert.assertTrue(str, z);
        if (this.selectedOption != id) {
            this.selectedOption = id;
            switch (id) {
                case 0:
                    this.optionEveryone.setBackgroundColor(this.selectedColor);
                    this.optionFriends.setBackgroundColor(this.unselectedColor);
                    this.optionBlocked.setBackgroundColor(this.unselectedColor);
                    break;
                case 1:
                    this.optionEveryone.setBackgroundColor(this.unselectedColor);
                    this.optionFriends.setBackgroundColor(this.selectedColor);
                    this.optionBlocked.setBackgroundColor(this.unselectedColor);
                    break;
                case 2:
                    this.optionEveryone.setBackgroundColor(this.unselectedColor);
                    this.optionFriends.setBackgroundColor(this.unselectedColor);
                    this.optionBlocked.setBackgroundColor(this.selectedColor);
                    break;
            }
            if (this.onSelectionChanged != null) {
                this.onSelectionChanged.run(this.selectedOption);
            }
        }
    }

    public int getSelectedOption() {
        return this.selectedOption;
    }

    public void setOnSelectionChanged(OnSelectionChanged runnable) {
        this.onSelectionChanged = runnable;
    }
}
