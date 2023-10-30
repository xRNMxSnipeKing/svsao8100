package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel;

public class WhatsNewActivityAdapter extends AdapterBaseWithList {
    private XLEButton skipButton;
    private WhatsNewActivityViewModel viewModel;
    private XLEButton xboxOneUpsellButton;

    public WhatsNewActivityAdapter(WhatsNewActivityViewModel vm) {
        this.screenBody = findViewById(R.id.whatsnew_activity_body);
        this.skipButton = (XLEButton) findViewById(R.id.what_new_skip);
        this.xboxOneUpsellButton = (XLEButton) findViewById(R.id.whats_new_xboxone_upsell);
        this.viewModel = vm;
        findAndInitializeModuleById(R.id.whats_new_list_module, this.viewModel);
        findAndInitializeModuleById(R.id.whats_new_grid_module, this.viewModel);
    }

    public void onStart() {
        super.onStart();
        if (this.xboxOneUpsellButton != null) {
            this.xboxOneUpsellButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    XLEUtil.gotoXboxOneUpSell();
                }
            });
        }
    }

    public void onStop() {
        super.onStop();
        if (this.xboxOneUpsellButton != null) {
            this.xboxOneUpsellButton.setOnClickListener(null);
        }
    }

    public void updateViewOverride() {
        if (this.viewModel.getIsShowStartNowButton()) {
            this.skipButton.setVisibility(8);
        } else {
            this.skipButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    WhatsNewActivityAdapter.this.viewModel.navigateToMainPivot();
                }
            });
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return null;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
