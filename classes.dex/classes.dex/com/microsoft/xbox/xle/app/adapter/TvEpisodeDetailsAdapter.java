package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.DetailsMoreOrLessView;
import com.microsoft.xbox.xle.ui.DetailsProviderView2;
import com.microsoft.xbox.xle.ui.DetailsProviderView2.OnProviderClickListener;
import com.microsoft.xbox.xle.ui.MediaProgressBar;
import com.microsoft.xbox.xle.viewmodel.TvEpisodeDetailsActivityViewModel;

public class TvEpisodeDetailsAdapter extends EDSV2NowPlayingAdapterBase<TvEpisodeDetailsActivityViewModel> {
    private CustomTypefaceTextView detailTitleTextView;
    private DetailsMoreOrLessView detailsDescriptionMoreOrLess;
    private XLEUniformImageView episodeImageView;
    private CustomTypefaceTextView episodeNameTextView;
    private SwitchPanel switchPanel;
    private CustomTypefaceTextView yearRatingDurationTextView;

    public TvEpisodeDetailsAdapter(TvEpisodeDetailsActivityViewModel tvEpisodeViewModel) {
        this.viewModel = tvEpisodeViewModel;
        this.screenBody = findViewById(R.id.tv_episode_details_activity_body);
        this.content = findViewById(R.id.tv_episode_details_switch_panel);
        this.switchPanel = (SwitchPanel) this.content;
        this.detailTitleTextView = (CustomTypefaceTextView) findViewById(R.id.tv_episode_details_title);
        this.episodeNameTextView = (CustomTypefaceTextView) findViewById(R.id.tv_episode_details_name);
        this.yearRatingDurationTextView = (CustomTypefaceTextView) findViewById(R.id.tv_episode_details_year_rating_duration);
        this.episodeImageView = (XLEUniformImageView) findViewById(R.id.tv_episode_details_tile);
        this.providersView2 = (DetailsProviderView2) findViewById(R.id.tv_episode_details_providers2);
        this.providersView2.setOnProviderClickListener(new OnProviderClickListener() {
            public void onProviderClick(EDSV2Provider data) {
                ((TvEpisodeDetailsActivityViewModel) TvEpisodeDetailsAdapter.this.viewModel).LaunchWithProviderInfo(data);
            }
        });
        this.detailsDescriptionMoreOrLess = (DetailsMoreOrLessView) findViewById(R.id.tv_episode_details_description_more_or_less);
        View view = findViewById(R.id.tv_episode_details_progress_bar);
        if (view != null) {
            this.mediaProgressBar = (MediaProgressBar) view;
        }
        this.smartGlassEnabled = findViewById(R.id.tv_episode_details_smartglass_enabled);
    }

    public void updateViewOverride() {
        super.updateViewOverride();
        this.switchPanel.setState(((TvEpisodeDetailsActivityViewModel) this.viewModel).getViewModelState().ordinal());
        updateLoadingIndicator(((TvEpisodeDetailsActivityViewModel) this.viewModel).isBusy());
        XLEUtil.updateTextIfNotNull(this.detailTitleTextView, JavaUtil.stringToUpper(((TvEpisodeDetailsActivityViewModel) this.viewModel).getTitle()));
        this.episodeNameTextView.setText(((TvEpisodeDetailsActivityViewModel) this.viewModel).getTvEpisodeName());
        this.detailsDescriptionMoreOrLess.setText(((TvEpisodeDetailsActivityViewModel) this.viewModel).getDescription());
        this.episodeImageView.setImageURI2(((TvEpisodeDetailsActivityViewModel) this.viewModel).getImageUrl(), ((TvEpisodeDetailsActivityViewModel) this.viewModel).getDefaultImageRid());
        if (XboxApplication.Instance.getIsTablet()) {
            this.yearRatingDurationTextView.setText(((TvEpisodeDetailsActivityViewModel) this.viewModel).getMonthDateYearRatingDuration());
        } else {
            this.yearRatingDurationTextView.setText(((TvEpisodeDetailsActivityViewModel) this.viewModel).getYearRatingDuration());
        }
        this.yearRatingDurationTextView.setVisibility(((TvEpisodeDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState ? 0 : 8);
        setCancelableBlocking(((TvEpisodeDetailsActivityViewModel) this.viewModel).isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                ((TvEpisodeDetailsActivityViewModel) TvEpisodeDetailsAdapter.this.viewModel).cancelLaunch();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ((TvEpisodeDetailsActivityViewModel) TvEpisodeDetailsAdapter.this.viewModel).load(true);
            }
        });
    }
}
