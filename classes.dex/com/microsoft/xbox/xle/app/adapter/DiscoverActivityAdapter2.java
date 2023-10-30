package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.module.ScreenModuleWithGridLayout;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.DiscoverActivityViewModel2;
import java.util.List;

public class DiscoverActivityAdapter2 extends AdapterBaseNormal {
    private SwitchPanel discoverSwitchPanel;
    private DiscoverActivityViewModel2 viewModel;

    public DiscoverActivityAdapter2(DiscoverActivityViewModel2 vm) {
        this.screenBody = findViewById(R.id.discover_activity_body2);
        this.content = findViewById(R.id.discover_switch_panel2);
        this.viewModel = vm;
        this.discoverSwitchPanel = (SwitchPanel) this.content;
        findAndInitializeModuleById(R.id.discover_content_list_module, this.viewModel);
        findAndInitializeModuleById(R.id.discover_content_grid_module, this.viewModel);
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        if (this.discoverSwitchPanel != null) {
            this.discoverSwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                DiscoverActivityAdapter2.this.viewModel.load(true);
            }
        });
    }

    protected SwitchPanel getSwitchPanel() {
        return this.discoverSwitchPanel;
    }

    protected List<AppBarMenuButton> getTestMenuButtons() {
        if (XLEApplication.Instance.getIsTablet()) {
        }
        return super.getTestMenuButtons();
    }

    protected ScreenModuleWithGridLayout getScreenModuleWithGridLayout() {
        return (ScreenModuleWithGridLayout) findViewById(R.id.discover_content_grid_module);
    }

    public void onStop() {
        super.onStop();
    }
}
