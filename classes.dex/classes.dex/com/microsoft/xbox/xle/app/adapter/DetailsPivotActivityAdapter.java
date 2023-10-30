package com.microsoft.xbox.xle.app.adapter;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.DetailsPivotActivityViewModel;

public class DetailsPivotActivityAdapter extends AdapterBaseNormal {
    private XLEImageViewFast backgroundImageView = null;
    private DetailsPivotActivityViewModel viewModel = null;

    public DetailsPivotActivityAdapter(DetailsPivotActivityViewModel viewModel) {
        this.viewModel = viewModel;
        this.screenBody = findViewById(R.id.details_pivot);
        this.backgroundImageView = (XLEImageViewFast) findViewById(R.id.details_background);
    }

    protected void updateViewOverride() {
        if (this.backgroundImageView == null) {
            return;
        }
        if (this.viewModel.shouldShowBackground()) {
            this.backgroundImageView.setVisibility(0);
            this.backgroundImageView.setImageURI2(this.viewModel.getBackgroundImageUrl(), -1, -1);
            return;
        }
        this.backgroundImageView.setVisibility(8);
    }
}
