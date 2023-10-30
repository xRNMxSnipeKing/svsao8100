package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.SettingsActivityViewModel;

public class SettingsActivityAdapter extends AdapterBaseNormal {
    private XLEButton aboutTitle;
    private Switch autoLaunchSmartGlassTileView;
    private XLEButton autoLaunchSmartGlassTitle;
    private XLEButton privacySetting;
    private XLEButton signOutTitle;
    private Switch soundTileView;
    private XLEButton soundTitle;
    private SettingsActivityViewModel viewModel;
    private XLEButton whatsNewTitle;

    public SettingsActivityAdapter(SettingsActivityViewModel vm) {
        this.screenBody = findViewById(R.id.setting_activity_body);
        this.content = findViewById(R.id.setting_content);
        this.soundTitle = (XLEButton) findViewById(R.id.setting_sound_title);
        this.soundTileView = (Switch) findViewById(R.id.setting_sound_status_tile);
        this.whatsNewTitle = (XLEButton) findViewById(R.id.setting_whats_new_title);
        this.signOutTitle = (XLEButton) findViewById(R.id.setting_signout_title);
        this.privacySetting = (XLEButton) findViewById(R.id.setting_privacy_setting);
        this.autoLaunchSmartGlassTitle = (XLEButton) findViewById(R.id.setting_auto_launch_smart_glass_title);
        this.autoLaunchSmartGlassTileView = (Switch) findViewById(R.id.setting_auto_launch_smart_glass_status_tile);
        this.aboutTitle = (XLEButton) findViewById(R.id.setting_about_title);
        this.viewModel = vm;
    }

    public void updateViewOverride() {
        this.soundTileView.setChecked(this.viewModel.getSoundStatus());
        this.autoLaunchSmartGlassTileView.setChecked(this.viewModel.getAutoLaunchSmartGlassStatus());
        this.soundTileView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsActivityAdapter.this.viewModel.setSoundStatus(isChecked);
            }
        });
        this.soundTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivityAdapter.this.soundTileView.setChecked(!SettingsActivityAdapter.this.soundTileView.isChecked());
            }
        });
        this.autoLaunchSmartGlassTileView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsActivityAdapter.this.viewModel.setAutoLaunchSmartGlassStatus(isChecked);
            }
        });
        this.autoLaunchSmartGlassTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivityAdapter.this.autoLaunchSmartGlassTileView.setChecked(!SettingsActivityAdapter.this.autoLaunchSmartGlassTileView.isChecked());
            }
        });
        this.privacySetting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivityAdapter.this.viewModel.navigateToPrivacySettings();
            }
        });
        this.whatsNewTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivityAdapter.this.viewModel.NavigateToWhatsNew();
            }
        });
        this.signOutTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivityAdapter.this.viewModel.signOut();
            }
        });
        this.aboutTitle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivityAdapter.this.viewModel.NavigateToAboutScreen();
            }
        });
    }
}
