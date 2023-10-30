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
import com.microsoft.xbox.xle.ui.RatingLevelAndDescriptorsView;
import com.microsoft.xbox.xle.ui.StarRatingWithUserCountView;
import com.microsoft.xbox.xle.viewmodel.GameDetailInfoActivityViewModel;

public class GameDetailInfoActivityAdapter extends EDSV2NowPlayingAdapterBase<GameDetailInfoActivityViewModel> {
    private DetailsMoreOrLessView desc;
    private SwitchPanel gameSwitchPanel;
    private CustomTypefaceTextView gameTitle;
    private XLEUniformImageView image;
    private CustomTypefaceTextView name;
    private CustomTypefaceTextView publisher;
    private StarRatingWithUserCountView rating;
    private RatingLevelAndDescriptorsView ratingLevelAndDescriptors;
    private CustomTypefaceTextView yearAndDeveloper;
    private CustomTypefaceTextView yearAndPublisherAndDeveloper;

    public GameDetailInfoActivityAdapter(GameDetailInfoActivityViewModel vm) {
        this.screenBody = findViewById(R.id.gamedetail_info_activity_body);
        this.content = findViewById(R.id.gamedetail_switch_panel);
        this.viewModel = vm;
        this.gameTitle = (CustomTypefaceTextView) findViewById(R.id.gamedetails_title);
        this.name = (CustomTypefaceTextView) findViewById(R.id.search_details_name);
        this.image = (XLEUniformImageView) findViewById(R.id.game_detail_tile_image);
        this.yearAndDeveloper = (CustomTypefaceTextView) findViewById(R.id.game_details_year_and_developer);
        this.yearAndPublisherAndDeveloper = (CustomTypefaceTextView) findViewById(R.id.game_details_year_publisher_developer);
        this.publisher = (CustomTypefaceTextView) findViewById(R.id.search_details_publisher);
        this.rating = (StarRatingWithUserCountView) findViewById(R.id.search_details_average_user_rating);
        this.desc = (DetailsMoreOrLessView) findViewById(R.id.search_details_description_more_or_less);
        this.providersView2 = (DetailsProviderView2) findViewById(R.id.search_details_providers2);
        this.providersView2.setOnProviderClickListener(new OnProviderClickListener() {
            public void onProviderClick(EDSV2Provider data) {
                ((GameDetailInfoActivityViewModel) GameDetailInfoActivityAdapter.this.viewModel).LaunchAppWithProviderInfo(data);
            }
        });
        this.gameSwitchPanel = (SwitchPanel) this.content;
        this.smartGlassEnabled = findViewById(R.id.game_details_smartglass_enabled);
        this.ratingLevelAndDescriptors = (RatingLevelAndDescriptorsView) findViewById(R.id.game_rating_level_and_descriptors);
    }

    public void updateViewOverride() {
        int i = 0;
        super.updateViewOverride();
        updateLoadingIndicator(((GameDetailInfoActivityViewModel) this.viewModel).isBusy());
        if (((GameDetailInfoActivityViewModel) this.viewModel).isNonXboxGame()) {
            updateViewForNonXboxGame();
            return;
        }
        int i2;
        this.gameSwitchPanel.setState(((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.gameTitle, JavaUtil.stringToUpper(((GameDetailInfoActivityViewModel) this.viewModel).getTitle()));
        this.name.setText(((GameDetailInfoActivityViewModel) this.viewModel).getTitle());
        if (this.yearAndDeveloper != null) {
            this.yearAndDeveloper.setText(((GameDetailInfoActivityViewModel) this.viewModel).getGameYearAndDeveloper());
            this.yearAndDeveloper.setVisibility(((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState ? 0 : 8);
        }
        if (this.publisher != null) {
            this.publisher.setText(((GameDetailInfoActivityViewModel) this.viewModel).getPublisher());
            CustomTypefaceTextView customTypefaceTextView = this.publisher;
            if (((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            customTypefaceTextView.setVisibility(i2);
        }
        if (this.yearAndPublisherAndDeveloper != null) {
            this.yearAndPublisherAndDeveloper.setText(((GameDetailInfoActivityViewModel) this.viewModel).getGameYearAndPublisherAndDeveloper());
            customTypefaceTextView = this.yearAndPublisherAndDeveloper;
            if (((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            customTypefaceTextView.setVisibility(i2);
        }
        this.rating.setAverageUserRatingAndUserCount(((GameDetailInfoActivityViewModel) this.viewModel).getAverageUserRating(), (long) ((GameDetailInfoActivityViewModel) this.viewModel).getUserRatingCount());
        this.desc.setText(((GameDetailInfoActivityViewModel) this.viewModel).getDescription());
        this.image.setImageURI2(((GameDetailInfoActivityViewModel) this.viewModel).getImageUrl(), ((GameDetailInfoActivityViewModel) this.viewModel).getDefaultImageRid());
        StarRatingWithUserCountView starRatingWithUserCountView = this.rating;
        if (((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
            i2 = 0;
        } else {
            i2 = 8;
        }
        starRatingWithUserCountView.setVisibility(i2);
        RatingLevelAndDescriptorsView ratingLevelAndDescriptorsView = this.ratingLevelAndDescriptors;
        if (((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState() != ListState.ValidContentState) {
            i = 8;
        }
        ratingLevelAndDescriptorsView.setVisibility(i);
        if (((GameDetailInfoActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
            this.ratingLevelAndDescriptors.setRatingLevelAndDescriptors(((GameDetailInfoActivityViewModel) this.viewModel).getRatingId(), ((GameDetailInfoActivityViewModel) this.viewModel).getParentalRating(), ((GameDetailInfoActivityViewModel) this.viewModel).getRatingDescriptors());
        }
        setCancelableBlocking(((GameDetailInfoActivityViewModel) this.viewModel).isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                ((GameDetailInfoActivityViewModel) GameDetailInfoActivityAdapter.this.viewModel).cancelLaunch();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ((GameDetailInfoActivityViewModel) GameDetailInfoActivityAdapter.this.viewModel).load(true);
            }
        });
    }

    private void updateViewForNonXboxGame() {
        this.gameSwitchPanel.setState(ListState.ValidContentState.ordinal());
        XLEUtil.updateTextIfNotNull(this.gameTitle, JavaUtil.stringToUpper(((GameDetailInfoActivityViewModel) this.viewModel).getTitle()));
        this.name.setText(((GameDetailInfoActivityViewModel) this.viewModel).getTitle());
        this.image.setImageURI2(((GameDetailInfoActivityViewModel) this.viewModel).getImageUrl(), ((GameDetailInfoActivityViewModel) this.viewModel).getDefaultImageRid());
        this.desc.setText(((GameDetailInfoActivityViewModel) this.viewModel).getDescription());
        this.smartGlassEnabled.setVisibility(8);
        this.rating.setVisibility(8);
        this.providersView2.setVisibility(8);
    }
}
