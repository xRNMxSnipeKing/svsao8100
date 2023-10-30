package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumMediaItem;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.ArtistAlbumListAdapter;
import com.microsoft.xbox.xle.viewmodel.ArtistDetailsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class ArtistDetailsContentListModule extends ScreenModuleWithList {
    private List<EDSV2MusicAlbumMediaItem> artistAlbums;
    private ArtistAlbumListAdapter listAdapter;
    private XLEListView listView;
    private ArtistDetailsActivityViewModel viewModel;

    public ArtistDetailsContentListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.artist_activity_content_list);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.artist_album_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ArtistDetailsContentListModule.this.viewModel.NavigateToAlbumDetails((EDSV2MusicAlbumMediaItem) view.getTag());
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getViewModelState() == ListState.ValidContentState && this.artistAlbums != this.viewModel.getArtistAlbums()) {
            this.artistAlbums = this.viewModel.getArtistAlbums();
            if (this.listAdapter == null) {
                this.listAdapter = new ArtistAlbumListAdapter(XLEApplication.getMainActivity(), R.layout.artist_album_list_row, this.artistAlbums);
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
        this.viewModel = (ArtistDetailsActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
