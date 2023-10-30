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
import com.microsoft.xbox.xle.ui.MetacriticRatingView;
import com.microsoft.xbox.xle.viewmodel.MovieDetailsActivityViewModel;

public class MovieDetailsActivityAdapter extends EDSV2NowPlayingAdapterBase<MovieDetailsActivityViewModel> {
    private DetailsMoreOrLessView detailsDescriptionMoreOrLess;
    private MetacriticRatingView metacriticRatingView;
    private CustomTypefaceTextView movieMediaTitle;
    private CustomTypefaceTextView movieProductionCompany;
    private CustomTypefaceTextView movieReleaseData;
    private CustomTypefaceTextView movieReleaseDataRatingStudioDuration;
    private SwitchPanel movieSwitchPanel;
    private XLEUniformImageView movieTileView;
    private CustomTypefaceTextView movieTitleView;

    public MovieDetailsActivityAdapter(MovieDetailsActivityViewModel vm) {
        this.screenBody = findViewById(R.id.movie_details_activity_body);
        this.content = findViewById(R.id.discover_details_switch_panel);
        this.viewModel = vm;
        this.movieMediaTitle = (CustomTypefaceTextView) findViewById(R.id.movie_details_tile_name);
        this.movieTileView = (XLEUniformImageView) findViewById(R.id.movie_details_tile);
        this.movieTitleView = (CustomTypefaceTextView) findViewById(R.id.movie_details_name);
        this.movieReleaseData = (CustomTypefaceTextView) findViewById(R.id.movie_details_release_data);
        this.movieReleaseDataRatingStudioDuration = (CustomTypefaceTextView) findViewById(R.id.movie_details_release_year_rating_studio_duration);
        this.movieProductionCompany = (CustomTypefaceTextView) findViewById(R.id.movie_details_production_company);
        this.detailsDescriptionMoreOrLess = (DetailsMoreOrLessView) findViewById(R.id.movie_details_description_more_or_less);
        this.providersView2 = (DetailsProviderView2) findViewById(R.id.movie_details_providers2);
        this.providersView2.setOnProviderClickListener(new OnProviderClickListener() {
            public void onProviderClick(EDSV2Provider provider) {
                ((MovieDetailsActivityViewModel) MovieDetailsActivityAdapter.this.viewModel).LaunchWithProviderInfo(provider);
            }
        });
        View view = findViewById(R.id.movie_details_progress_bar);
        if (view != null) {
            this.mediaProgressBar = (MediaProgressBar) view;
        }
        this.movieSwitchPanel = (SwitchPanel) this.content;
        this.smartGlassEnabled = findViewById(R.id.movie_details_smartglass_enabled);
        this.metacriticRatingView = (MetacriticRatingView) findViewById(R.id.movie_details_metacriticrating);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ((MovieDetailsActivityViewModel) MovieDetailsActivityAdapter.this.viewModel).load(true);
            }
        });
    }

    public void updateViewOverride() {
        int i = 0;
        super.updateViewOverride();
        updateLoadingIndicator(((MovieDetailsActivityViewModel) this.viewModel).isBusy());
        this.movieSwitchPanel.setState(((MovieDetailsActivityViewModel) this.viewModel).getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.movieMediaTitle, JavaUtil.stringToUpper(((MovieDetailsActivityViewModel) this.viewModel).getTitle()));
        this.movieTileView.setImageURI2(((MovieDetailsActivityViewModel) this.viewModel).getImageUrl(), ((MovieDetailsActivityViewModel) this.viewModel).getDefaultImageRid());
        this.movieTitleView.setText(((MovieDetailsActivityViewModel) this.viewModel).getTitle());
        this.detailsDescriptionMoreOrLess.setText(((MovieDetailsActivityViewModel) this.viewModel).getDescription());
        this.metacriticRatingView.setRating(((MovieDetailsActivityViewModel) this.viewModel).getMetaCriticReviewScore());
        if (this.movieReleaseData != null) {
            this.movieReleaseData.setText(((MovieDetailsActivityViewModel) this.viewModel).getMovieReleaseData());
            this.movieReleaseData.setVisibility(((MovieDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState ? 0 : 8);
        }
        if (this.movieProductionCompany != null) {
            int i2;
            this.movieProductionCompany.setText(((MovieDetailsActivityViewModel) this.viewModel).getStudio());
            CustomTypefaceTextView customTypefaceTextView = this.movieProductionCompany;
            if (((MovieDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            customTypefaceTextView.setVisibility(i2);
        }
        if (this.movieReleaseDataRatingStudioDuration != null) {
            this.movieReleaseDataRatingStudioDuration.setText(((MovieDetailsActivityViewModel) this.viewModel).getMovieReleaseParentRatingStudioDuration());
            customTypefaceTextView = this.movieReleaseDataRatingStudioDuration;
            if (((MovieDetailsActivityViewModel) this.viewModel).getViewModelState() != ListState.ValidContentState) {
                i = 8;
            }
            customTypefaceTextView.setVisibility(i);
        }
        setCancelableBlocking(((MovieDetailsActivityViewModel) this.viewModel).isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                ((MovieDetailsActivityViewModel) MovieDetailsActivityAdapter.this.viewModel).cancelLaunch();
            }
        });
    }
}
