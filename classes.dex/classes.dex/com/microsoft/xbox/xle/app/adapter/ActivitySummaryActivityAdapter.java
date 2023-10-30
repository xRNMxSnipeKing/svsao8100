package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ActivitySummaryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class ActivitySummaryActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel activitySwitchPanel;
    private TextView contentTitle;
    private ActivitySummaryActivityViewModel viewModel;

    public ActivitySummaryActivityAdapter(ActivitySummaryActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.activity_summary_body);
        this.content = findViewById(R.id.activity_summary_switch_panel);
        this.contentTitle = (TextView) findViewById(R.id.activity_summary_content_title);
        this.activitySwitchPanel = (SwitchPanel) this.content;
        findAndInitializeModuleById(R.id.activity_summary_list_module, this.viewModel);
        findAndInitializeModuleById(R.id.activity_summary_grid_module, this.viewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ActivitySummaryActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        XLEUtil.updateTextIfNotNull(this.contentTitle, JavaUtil.stringToUpper(this.viewModel.getTitle()));
        this.activitySwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
    }

    protected SwitchPanel getSwitchPanel() {
        return this.activitySwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
