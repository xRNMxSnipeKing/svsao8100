package com.microsoft.xbox.xle.app.adapter;

import android.text.Html;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.viewmodel.AboutActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;

public class AboutActivityAdapter extends AdapterBaseNormal {
    private CustomTypefaceTextView aboutText;
    private AboutActivityViewModel viewModel;

    public AboutActivityAdapter(AboutActivityViewModel vm) {
        this.viewModel = vm;
        this.aboutText = (CustomTypefaceTextView) findViewById(R.id.about_content);
        this.screenBody = findViewById(R.id.about_activity_body);
    }

    public void updateViewOverride() {
        this.aboutText.setText(Html.fromHtml(XboxApplication.Resources.getString(R.string.about_content)));
    }
}
