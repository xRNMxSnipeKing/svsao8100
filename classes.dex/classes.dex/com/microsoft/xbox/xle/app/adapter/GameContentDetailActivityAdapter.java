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
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.GameContentDetailActivityViewModel;

public class GameContentDetailActivityAdapter extends AdapterBaseNormal {
    private DetailsMoreOrLessView desc;
    private CustomTypefaceTextView gameContentTitle;
    private XLEUniformImageView image;
    private CustomTypefaceTextView name;
    private DetailsProviderView2 providersView2;
    private CustomTypefaceTextView publisher;
    private StarRatingWithUserCountView rating;
    private RatingLevelAndDescriptorsView ratingLevelAndDescriptors;
    private CustomTypefaceTextView releaseYear;
    private SwitchPanel switchPanel;
    private GameContentDetailActivityViewModel viewModel;

    public GameContentDetailActivityAdapter(GameContentDetailActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.game_content_activity_body);
        this.content = findViewById(R.id.game_content_switch_panel);
        this.gameContentTitle = (CustomTypefaceTextView) findViewById(R.id.game_content_title);
        this.image = (XLEUniformImageView) findViewById(R.id.game_content_tile_image);
        this.name = (CustomTypefaceTextView) findViewById(R.id.game_content_name);
        this.publisher = (CustomTypefaceTextView) findViewById(R.id.game_content_product_company);
        this.releaseYear = (CustomTypefaceTextView) findViewById(R.id.game_content_release_year);
        this.rating = (StarRatingWithUserCountView) findViewById(R.id.game_content_user_ratingCount);
        this.desc = (DetailsMoreOrLessView) findViewById(R.id.game_content_description_more_or_less);
        this.providersView2 = (DetailsProviderView2) findViewById(R.id.game_content_providers2);
        this.providersView2.setOnProviderClickListener(new OnProviderClickListener() {
            public void onProviderClick(EDSV2Provider data) {
                GameContentDetailActivityAdapter.this.viewModel.LaunchWithProviderInfo(data);
            }
        });
        this.switchPanel = (SwitchPanel) this.content;
        this.ratingLevelAndDescriptors = (RatingLevelAndDescriptorsView) findViewById(R.id.game_content_rating_level_and_descriptors);
    }

    public void updateViewOverride() {
        int i = 0;
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.gameContentTitle, JavaUtil.stringToUpper(this.viewModel.getTitle()));
        this.name.setText(this.viewModel.getTitle());
        if (this.publisher != null) {
            this.publisher.setText(this.viewModel.getDeveloper());
            this.publisher.setVisibility(this.viewModel.getViewModelState() == ListState.ValidContentState ? 0 : 8);
        }
        if (this.releaseYear != null) {
            this.releaseYear.setText(this.viewModel.getReleaseYear());
        }
        this.rating.setAverageUserRatingAndUserCount(this.viewModel.getAverageUserRating(), (long) this.viewModel.getUserRatingCount());
        this.desc.setText(this.viewModel.getDescription());
        if (this.viewModel.getProviders() != null) {
            this.providersView2.setProviders(this.viewModel.getProviders(), this.viewModel.getMediaType());
        }
        this.image.setImageURI2(this.viewModel.getImageUrl(), this.viewModel.getDefaultImageRid());
        this.rating.setVisibility(this.viewModel.getViewModelState() == ListState.ValidContentState ? 0 : 8);
        RatingLevelAndDescriptorsView ratingLevelAndDescriptorsView = this.ratingLevelAndDescriptors;
        if (this.viewModel.getViewModelState() != ListState.ValidContentState) {
            i = 8;
        }
        ratingLevelAndDescriptorsView.setVisibility(i);
        this.ratingLevelAndDescriptors.setRatingLevelAndDescriptors(this.viewModel.getRatingId(), this.viewModel.getParentalRating(), this.viewModel.getRatingDescriptors());
        setCancelableBlocking(this.viewModel.isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                GameContentDetailActivityAdapter.this.viewModel.cancelLaunch();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                GameContentDetailActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public SwitchPanel getSwitchPanel() {
        return this.switchPanel;
    }
}
