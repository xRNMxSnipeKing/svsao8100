package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.DetailsMoreOrLessView;
import com.microsoft.xbox.xle.ui.DetailsProviderView2;
import com.microsoft.xbox.xle.ui.DetailsProviderView2.OnProviderClickListener;
import com.microsoft.xbox.xle.ui.MediaProgressBar;
import com.microsoft.xbox.xle.viewmodel.AppDetailsActivityViewModel;

public class AppDetailsActivityAdapter extends EDSV2NowPlayingAdapterBase<AppDetailsActivityViewModel> {
    private DetailsMoreOrLessView appDescriptionView;
    private SwitchPanel appSwitchPanel;
    private XLEUniformImageView appTileView;
    private CustomTypefaceTextView appTitleView;
    private CustomTypefaceTextView titleView;

    public AppDetailsActivityAdapter(AppDetailsActivityViewModel vm) {
        this.screenBody = findViewById(R.id.discoverdetails_activity_body);
        this.content = findViewById(R.id.discover_switch_panel);
        this.viewModel = vm;
        this.titleView = (CustomTypefaceTextView) findViewById(R.id.details_title);
        this.appTileView = (XLEUniformImageView) findViewById(R.id.app_details_tile);
        this.appTitleView = (CustomTypefaceTextView) findViewById(R.id.app_details_title);
        this.appDescriptionView = (DetailsMoreOrLessView) findViewById(R.id.app_details_description_more_or_less);
        this.providersView2 = (DetailsProviderView2) findViewById(R.id.app_details_providers2);
        this.providersView2.setOnProviderClickListener(new OnProviderClickListener() {
            public void onProviderClick(EDSV2Provider data) {
                ((AppDetailsActivityViewModel) AppDetailsActivityAdapter.this.viewModel).LaunchAppWithProviderInfo(data);
            }
        });
        View view = findViewById(R.id.app_details_progress_bar);
        if (view != null) {
            this.mediaProgressBar = (MediaProgressBar) view;
        }
        this.appSwitchPanel = (SwitchPanel) this.content;
        this.smartGlassEnabled = findViewById(R.id.app_details_smartglass_enabled);
    }

    public void updateViewOverride() {
        super.updateViewOverride();
        updateLoadingIndicator(((AppDetailsActivityViewModel) this.viewModel).isBusy());
        this.appSwitchPanel.setState(((AppDetailsActivityViewModel) this.viewModel).getViewModelState().ordinal());
        this.appTileView.setImageURI2(((AppDetailsActivityViewModel) this.viewModel).getImageUrl(), ((AppDetailsActivityViewModel) this.viewModel).getDefaultImageRid());
        this.appTitleView.setText(((AppDetailsActivityViewModel) this.viewModel).getTitle());
        this.appDescriptionView.setText(((AppDetailsActivityViewModel) this.viewModel).getDescription());
        if (((AppDetailsActivityViewModel) this.viewModel).getTitle() != null && ((AppDetailsActivityViewModel) this.viewModel).getTitle().length() > 0) {
            XLEUtil.updateTextIfNotNull(this.titleView, JavaUtil.stringToUpper(((AppDetailsActivityViewModel) this.viewModel).getTitle()));
        }
        setCancelableBlocking(((AppDetailsActivityViewModel) this.viewModel).isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                ((AppDetailsActivityViewModel) AppDetailsActivityAdapter.this.viewModel).cancelLaunch();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ((AppDetailsActivityViewModel) AppDetailsActivityAdapter.this.viewModel).load(true);
            }
        });
    }
}
