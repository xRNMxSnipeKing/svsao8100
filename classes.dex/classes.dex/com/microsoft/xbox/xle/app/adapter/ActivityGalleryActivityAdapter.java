package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEGridView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ActivityGalleryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithGrid;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.net.URI;
import java.util.ArrayList;

public class ActivityGalleryActivityAdapter extends AdapterBaseWithGrid {
    private ActivityGalleryListAdapter listAdapter;
    private ArrayList<URI> screenShotUrls;
    private SwitchPanel switchPanel;
    private TextView titleView;
    private ActivityGalleryActivityViewModel viewModel;

    public ActivityGalleryActivityAdapter(ActivityGalleryActivityViewModel vm) {
        this.screenBody = findViewById(R.id.activity_gallery_activity_body);
        this.content = findViewById(R.id.activity_gallery_switch_panel);
        this.viewModel = vm;
        this.titleView = (TextView) findViewById(R.id.activity_gallery_title);
        this.switchPanel = (SwitchPanel) this.content;
        this.gridView = (XLEGridView) findViewById(R.id.activity_gallery_grid);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ActivityGalleryActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        if (this.viewModel.getScreenshotUrls() != null) {
            if (this.screenShotUrls != this.viewModel.getScreenshotUrls()) {
                this.screenShotUrls = this.viewModel.getScreenshotUrls();
                this.listAdapter = new ActivityGalleryListAdapter(XLEApplication.getMainActivity(), R.layout.activity_gallery_list_item, this.screenShotUrls);
                this.gridView.setAdapter(this.listAdapter);
            } else {
                this.listAdapter.notifyDataSetChanged();
            }
        }
        XLEUtil.updateTextIfNotNull(this.titleView, JavaUtil.stringToUpper(this.viewModel.getTitle()));
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
