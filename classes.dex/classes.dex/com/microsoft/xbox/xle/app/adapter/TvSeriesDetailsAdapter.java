package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.TvSeriesDetailsViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class TvSeriesDetailsAdapter extends AdapterBaseWithList {
    private CustomTypefaceTextView descriptionTextView;
    private CustomTypefaceTextView networkNameTextView;
    private XLEUniformImageView tileImageView;
    private CustomTypefaceTextView titleTextView;
    private SwitchPanel tvSeriesDetailsSwitchPanel;
    private TvSeriesDetailsViewModel viewModel;

    public TvSeriesDetailsAdapter(TvSeriesDetailsViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.tv_series_details_activity_body);
        this.content = findViewById(R.id.tv_series_details_switch_panel);
        this.tvSeriesDetailsSwitchPanel = (SwitchPanel) this.content;
        this.titleTextView = (CustomTypefaceTextView) findViewById(R.id.tv_series_details_title_name);
        this.descriptionTextView = (CustomTypefaceTextView) findViewById(R.id.tv_series_description);
        this.tileImageView = (XLEUniformImageView) findViewById(R.id.tv_series_tile_image);
        this.networkNameTextView = (CustomTypefaceTextView) findViewById(R.id.tv_series_networkname);
        findAndInitializeModuleById(R.id.tv_series_detail_list_module, this.viewModel);
        findAndInitializeModuleById(R.id.tv_series_detail_grid_module, this.viewModel);
    }

    public void updateViewOverride() {
        this.tvSeriesDetailsSwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        updateLoadingIndicator(this.viewModel.isBusy());
        XLEUtil.updateTextIfNotNull(this.titleTextView, JavaUtil.stringToUpper(this.viewModel.getTitle()));
        if (this.descriptionTextView != null) {
            this.descriptionTextView.setText(this.viewModel.getDescription());
        }
        if (this.networkNameTextView != null) {
            this.networkNameTextView.setText(this.viewModel.getNetworkName());
        }
        if (this.tileImageView != null) {
            this.tileImageView.setImageURI2(this.viewModel.getImageUrl(), this.viewModel.getDefaultImageRid());
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return this.tvSeriesDetailsSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                TvSeriesDetailsAdapter.this.viewModel.load(true);
            }
        });
    }
}
