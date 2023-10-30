package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVSeasonMediaItem;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.TvSeasonListAdapter;
import com.microsoft.xbox.xle.viewmodel.TvSeriesDetailsViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class TvSeriesContentListModule extends ScreenModuleWithList {
    private TvSeasonListAdapter listAdapter;
    private XLEListView listView;
    private List<EDSV2TVSeasonMediaItem> seasons;
    private TvSeriesDetailsViewModel viewModel;

    public TvSeriesContentListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.tv_series_detail_activity_content_list);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.seasons_list_view);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                TvSeriesContentListModule.this.viewModel.NavigateToTvSeasonDetails((EDSV2TVSeasonMediaItem) view.getTag());
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getViewModelState() == ListState.ValidContentState && this.seasons != this.viewModel.getSeasons()) {
            this.seasons = this.viewModel.getSeasons();
            if (this.listAdapter == null) {
                this.listAdapter = new TvSeasonListAdapter(XLEApplication.getMainActivity(), R.layout.tv_series_details_select_list_item, this.seasons);
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
                return;
            }
            this.listView.notifyDataSetChanged();
        }
    }

    public void onDestroy() {
        this.listView.setOnItemClickListener(null);
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (TvSeriesDetailsViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
