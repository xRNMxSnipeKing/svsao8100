package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.DetailsMoreOrLessView;
import com.microsoft.xbox.xle.ui.SmartGlassPlayButton;
import com.microsoft.xbox.xle.viewmodel.ActivityOverviewActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;

public class ActivityOverviewActivityAdapter extends AdapterBaseNormal {
    private XLEButton buyButton;
    private DetailsMoreOrLessView descriptionsView;
    private SwitchPanel detailSwitchPanel;
    private TextView deviceRequirementsView;
    private View divider;
    private TextView nameView;
    private SmartGlassPlayButton playButton;
    private TextView providersView;
    private LinearLayout purchaseContainer;
    private TextView purchaseStateView;
    private View purchaseVerificationFailedView;
    private XLEUniformImageView tileView;
    private TextView titleView;
    private XLEButton upgradeGoldButton;
    private ActivityOverviewActivityViewModel viewModel;

    public ActivityOverviewActivityAdapter(ActivityOverviewActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.activity_overview_activity_body);
        this.content = findViewById(R.id.activity_overview_switch_panel);
        this.titleView = (TextView) findViewById(R.id.activity_overview_title);
        this.detailSwitchPanel = (SwitchPanel) this.content;
        this.tileView = (XLEUniformImageView) findViewById(R.id.activity_overview_tile);
        this.nameView = (TextView) findViewById(R.id.activity_overview_name);
        this.descriptionsView = (DetailsMoreOrLessView) findViewById(R.id.activity_overview_description);
        this.providersView = (TextView) findViewById(R.id.activity_overview_providers);
        this.deviceRequirementsView = (TextView) findViewById(R.id.activity_overview_device_requirement);
        this.purchaseVerificationFailedView = findViewById(R.id.activity_overview_purchase_verification_failed);
        this.purchaseStateView = (TextView) findViewById(R.id.activity_overview_purchase_state);
        this.playButton = (SmartGlassPlayButton) findViewById(R.id.activity_overview_play);
        this.purchaseContainer = (LinearLayout) findViewById(R.id.activity_overview_purchase_container);
        this.upgradeGoldButton = (XLEButton) findViewById(R.id.activity_overview_upgrade_gold);
        this.buyButton = (XLEButton) findViewById(R.id.activity_overview_buy);
        this.divider = findViewById(R.id.activity_overview_divider);
        this.upgradeGoldButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityOverviewActivityAdapter.this.viewModel.upgradeToGold();
            }
        });
        this.buyButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityOverviewActivityAdapter.this.viewModel.purchaseActivity();
            }
        });
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivityOverviewActivityAdapter.this.viewModel.launchActivity();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ActivityOverviewActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        int i;
        int i2 = 8;
        int i3 = 0;
        updateLoadingIndicator(this.viewModel.isBusy());
        this.detailSwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.titleView, JavaUtil.stringToUpper(this.viewModel.getTitle()));
        this.nameView.setText(this.viewModel.getTitle());
        this.tileView.setImageURI2(this.viewModel.getTileImageUrl(), R.drawable.activity_1x1_missing);
        this.descriptionsView.setText(this.viewModel.getDescription());
        if (JavaUtil.isNullOrEmpty(this.viewModel.getProviderText())) {
            this.providersView.setVisibility(8);
        } else {
            this.providersView.setVisibility(0);
            this.providersView.setText(this.viewModel.getProviderText());
        }
        if (JavaUtil.isNullOrEmpty(this.viewModel.getDeviceRequirementString())) {
            this.deviceRequirementsView.setVisibility(8);
        } else {
            this.deviceRequirementsView.setVisibility(0);
            this.deviceRequirementsView.setText(this.viewModel.getDeviceRequirementString());
        }
        View view = this.purchaseVerificationFailedView;
        if (this.viewModel.isPurchaseVerificationFailed()) {
            i = 0;
        } else {
            i = 8;
        }
        view.setVisibility(i);
        this.purchaseStateView.setText(this.viewModel.getPurchaseStateText());
        this.buyButton.setText(this.viewModel.getDisplayPurchasePrice());
        SmartGlassPlayButton smartGlassPlayButton = this.playButton;
        if (this.viewModel.shouldShowLaunchButton()) {
            i = 0;
        } else {
            i = 8;
        }
        smartGlassPlayButton.setVisibility(i);
        LinearLayout linearLayout = this.purchaseContainer;
        if (!this.viewModel.shouldShowLaunchButton()) {
            i2 = 0;
        }
        linearLayout.setVisibility(i2);
        this.purchaseContainer.removeAllViews();
        if (this.viewModel.shouldShowUpgradeGoldButton()) {
            this.purchaseContainer.addView(this.upgradeGoldButton);
            this.purchaseContainer.addView(this.divider);
            this.purchaseContainer.addView(this.buyButton);
        } else if (this.viewModel.shouldShowBuyButton()) {
            this.purchaseContainer.addView(this.buyButton);
            this.purchaseContainer.addView(this.divider);
            this.purchaseContainer.addView(this.upgradeGoldButton);
        }
        XLEButton xLEButton = this.upgradeGoldButton;
        if (this.viewModel.shouldShowUpgradeGoldButton()) {
            i = 0;
        } else {
            i = 4;
        }
        xLEButton.setVisibility(i);
        XLEButton xLEButton2 = this.buyButton;
        if (!this.viewModel.shouldShowBuyButton()) {
            i3 = 4;
        }
        xLEButton2.setVisibility(i3);
        this.buyButton.setBackgroundResource(this.viewModel.shouldShowUpgradeGoldButton() ? R.drawable.gray_button_states : R.drawable.button_states);
    }
}
