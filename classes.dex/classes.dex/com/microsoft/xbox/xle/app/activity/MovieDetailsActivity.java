package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.MovieDetailsActivityViewModel;

public class MovieDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new MovieDetailsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.discover_movie_details_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, true);
    }

    protected String getActivityName() {
        return "MovieDetails";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
