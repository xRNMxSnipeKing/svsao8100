package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.adapter.NowPlayingGridAdapter;
import com.microsoft.xbox.xle.ui.NowPlayingHeroGridLayout;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class NowPlayingActivityTabletModule extends ScreenModuleLayout {
    private TextView nowPlayingHeaderText;
    private NowPlayingHeroGridLayout nowplayingGrid;
    private NowPlayingActivityViewModel viewModel;

    public NowPlayingActivityTabletModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.nowplaying_activity_content);
    }

    protected void onFinishInflate() {
        this.nowPlayingHeaderText = (TextView) findViewById(R.id.now_playing_header_text);
        this.nowplayingGrid = (NowPlayingHeroGridLayout) findViewById(R.id.nowplaying_grid);
        this.nowplayingGrid.setGridAdapter(new NowPlayingGridAdapter(getContext(), this.nowplayingGrid.isLongAspectRatio()));
    }

    public void updateView() {
        this.nowPlayingHeaderText.setText(this.viewModel.getNowPlayingHeader());
        if (this.viewModel.getShouldShowNowPlaying()) {
            this.nowPlayingHeaderText.setVisibility(0);
        } else {
            this.nowPlayingHeaderText.setVisibility(4);
        }
        this.nowplayingGrid.getGridAdapter().notifyDataChanged();
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (NowPlayingActivityViewModel) vm;
        ((NowPlayingGridAdapter) this.nowplayingGrid.getGridAdapter()).setViewModel(this.viewModel);
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
