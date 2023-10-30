package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AbstractRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class TVEpisodeRelatedActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel switchPanel;
    private CustomTypefaceTextView toastRelatedListEmpty;
    private CustomTypefaceTextView toastRelatedListError;
    private CustomTypefaceTextView tvEpisodeTitle;
    private AbstractRelatedActivityViewModel viewModel;

    public TVEpisodeRelatedActivityAdapter(AbstractRelatedActivityViewModel viewMode) {
        this.viewModel = viewMode;
        this.screenBody = findViewById(R.id.details_related_body);
        this.content = findViewById(R.id.details_related_switch_panel);
        this.switchPanel = (SwitchPanel) this.content;
        this.toastRelatedListError = (CustomTypefaceTextView) findViewById(R.id.toast_details_related_list_error);
        this.toastRelatedListEmpty = (CustomTypefaceTextView) findViewById(R.id.details_related_empty);
        this.tvEpisodeTitle = (CustomTypefaceTextView) findViewById(R.id.details_related_content_title);
        findAndInitializeModuleById(R.id.details_related_list_module, this.viewModel);
        findAndInitializeModuleById(R.id.details_related_grid_module, this.viewModel);
    }

    public void updateViewOverride() {
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.tvEpisodeTitle, JavaUtil.stringToUpper(this.viewModel.getTitle()));
        updateLoadingIndicator(this.viewModel.isBusy());
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                TVEpisodeRelatedActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    protected SwitchPanel getSwitchPanel() {
        return this.switchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
