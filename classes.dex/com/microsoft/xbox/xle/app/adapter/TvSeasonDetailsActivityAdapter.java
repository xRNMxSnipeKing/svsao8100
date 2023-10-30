package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVEpisodeMediaItem;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.TvSeasonDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class TvSeasonDetailsActivityAdapter extends AdapterBaseWithList {
    private List<EDSV2TVEpisodeMediaItem> episodes;
    private TvEpisodeListAdapter listAdapter;
    private CustomTypefaceTextView seasonNameTextView;
    private CustomTypefaceTextView seasonReleaseYearTextView;
    private CustomTypefaceTextView seasonTitleTextView;
    private CustomTypefaceTextView seriesTitleTextView;
    private SwitchPanel switchPanel;
    private XLEUniformImageView tileImageView;
    private TvSeasonDetailsActivityViewModel viewModel;

    public TvSeasonDetailsActivityAdapter(TvSeasonDetailsActivityViewModel tvSeasonViewModel) {
        this.viewModel = tvSeasonViewModel;
        this.episodes = null;
        this.screenBody = findViewById(R.id.tv_season_activity_body);
        this.content = findViewById(R.id.tv_season_switch_panel);
        this.switchPanel = (SwitchPanel) this.content;
        this.seriesTitleTextView = (CustomTypefaceTextView) findViewById(R.id.tv_season_series_name);
        this.seasonTitleTextView = (CustomTypefaceTextView) findViewById(R.id.tv_season_title_name);
        this.tileImageView = (XLEUniformImageView) findViewById(R.id.tv_season_tile_image);
        this.seasonNameTextView = (CustomTypefaceTextView) findViewById(R.id.tv_season_name);
        this.seasonReleaseYearTextView = (CustomTypefaceTextView) findViewById(R.id.tv_season_release_year);
        this.listView = (XLEListView) findViewById(R.id.tv_season_details_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TvSeasonDetailsActivityAdapter.this.viewModel.NavigateToTvEpisodeDetails((EDSV2TVEpisodeMediaItem) view.getTag());
            }
        });
    }

    public void updateViewOverride() {
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        updateLoadingIndicator(this.viewModel.isBusy());
        XLEUtil.updateTextIfNotNull(this.seriesTitleTextView, JavaUtil.stringToUpper(this.viewModel.getTvSeriesName()));
        XLEUtil.updateTextIfNotNull(this.seasonTitleTextView, this.viewModel.getSeasonsHeader());
        if (this.tileImageView != null) {
            this.tileImageView.setImageURI2(this.viewModel.getImageUrl(), this.viewModel.getDefaultImageRid());
        }
        if (this.seasonNameTextView != null) {
            this.seasonNameTextView.setText(this.viewModel.getSeasonsHeader());
        }
        if (this.seasonReleaseYearTextView != null) {
            this.seasonReleaseYearTextView.setText(this.viewModel.getReleaseYear());
        }
        if (this.episodes != this.viewModel.getEpisodes()) {
            this.episodes = this.viewModel.getEpisodes();
            if (this.listAdapter == null) {
                this.listAdapter = new TvEpisodeListAdapter(XLEApplication.getMainActivity(), R.layout.tv_season_details_list_row, this.episodes);
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
                return;
            }
            this.listView.notifyDataSetChanged();
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return this.switchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                TvSeasonDetailsActivityAdapter.this.viewModel.load(true);
            }
        });
    }
}
