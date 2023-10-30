package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.GameRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class GameRelatedActivityAdapter extends AdapterBaseWithList {
    private CustomTypefaceTextView gameTitle;
    private SwitchPanel switchPanel;
    private CustomTypefaceTextView toastRelatedListEmpty;
    private CustomTypefaceTextView toastRelatedListError;
    private GameRelatedActivityViewModel viewModel;

    public GameRelatedActivityAdapter(GameRelatedActivityViewModel viewModel) {
        this.viewModel = viewModel;
        this.screenBody = findViewById(R.id.details_related_body);
        this.content = findViewById(R.id.details_related_switch_panel);
        this.switchPanel = (SwitchPanel) this.content;
        this.toastRelatedListError = (CustomTypefaceTextView) findViewById(R.id.toast_details_related_list_error);
        this.toastRelatedListEmpty = (CustomTypefaceTextView) findViewById(R.id.details_related_empty);
        this.gameTitle = (CustomTypefaceTextView) findViewById(R.id.details_related_content_title);
        findAndInitializeModuleById(R.id.details_related_list_module, viewModel);
        findAndInitializeModuleById(R.id.details_related_grid_module, viewModel);
    }

    public void updateViewOverride() {
        XLELog.Diagnostic("RelatedAdapter", "set is loading " + this.viewModel.isBusy());
        updateLoadingIndicator(this.viewModel.isBusy());
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.gameTitle, JavaUtil.stringToUpper(this.viewModel.getTitle()));
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                GameRelatedActivityAdapter.this.viewModel.load(true);
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
