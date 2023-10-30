package com.microsoft.xbox.xle.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicTrackMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XBLSharedUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceToggleButton;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.ToggleTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.DetailsProviderView2;
import com.microsoft.xbox.xle.ui.DetailsProviderView2.OnProviderClickListener;
import com.microsoft.xbox.xle.ui.MediaProgressBar;
import com.microsoft.xbox.xle.viewmodel.AlbumDetailsActivityViewModel;
import java.util.HashMap;
import java.util.List;

public class AlbumDetailsActivityAdapter extends EDSV2NowPlayingAdapterBase<AlbumDetailsActivityViewModel> {
    private CustomTypefaceTextView albumDetailArtist;
    private XLEUniformImageView albumDetailImage;
    private CustomTypefaceToggleButton albumDetailShow;
    private CustomTypefaceTextView albumDetailTitle;
    private CustomTypefaceTextView albumDetailTitleTop;
    private CustomTypefaceTextView albumDetailsReleaseDate;
    private CustomTypefaceTextView albumDetailsReleaseDateAndStudio;
    private LinearLayout allSongListLayout;
    private View nowPlayingTrackView;
    private SwitchPanel switchPanel;
    private List<EDSV2MusicTrackMediaItem> trackList;
    private HashMap<Integer, View> trackNumberToViewMap;

    public AlbumDetailsActivityAdapter(AlbumDetailsActivityViewModel vm) {
        this.screenBody = findViewById(R.id.album_details_activity_body);
        this.content = findViewById(R.id.album_detail_list_switch_panel);
        this.viewModel = vm;
        this.albumDetailTitleTop = (CustomTypefaceTextView) findViewById(R.id.album_details_title_top);
        this.albumDetailImage = (XLEUniformImageView) findViewById(R.id.album_details_image_tile);
        this.albumDetailTitle = (CustomTypefaceTextView) findViewById(R.id.album_details_title);
        this.albumDetailArtist = (CustomTypefaceTextView) findViewById(R.id.album_details_artist);
        this.albumDetailsReleaseDate = (CustomTypefaceTextView) findViewById(R.id.album_details_release_date);
        this.albumDetailsReleaseDateAndStudio = (CustomTypefaceTextView) findViewById(R.id.album_details_release_date_and_studio);
        this.albumDetailShow = (CustomTypefaceToggleButton) findViewById(R.id.album_details_show);
        this.providersView2 = (DetailsProviderView2) findViewById(R.id.album_details_providers2);
        this.providersView2.setOnProviderClickListener(new OnProviderClickListener() {
            public void onProviderClick(EDSV2Provider data) {
                ((AlbumDetailsActivityViewModel) AlbumDetailsActivityAdapter.this.viewModel).LaunchWithProviderInfo(data);
            }
        });
        View view = findViewById(R.id.album_details_progress_bar);
        if (view != null) {
            this.mediaProgressBar = (MediaProgressBar) view;
        }
        this.allSongListLayout = (LinearLayout) findViewById(R.id.songs_list_layout);
        this.switchPanel = (SwitchPanel) this.content;
        this.smartGlassEnabled = findViewById(R.id.album_details_smartglass_enabled);
        if (this.albumDetailShow != null) {
            this.albumDetailShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        AlbumDetailsActivityAdapter.this.allSongListLayout.setVisibility(0);
                    } else {
                        AlbumDetailsActivityAdapter.this.allSongListLayout.setVisibility(8);
                    }
                    ((AlbumDetailsActivityViewModel) AlbumDetailsActivityAdapter.this.viewModel).setAutoShowSongList(false);
                }
            });
        } else {
            this.allSongListLayout.setVisibility(0);
        }
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                ((AlbumDetailsActivityViewModel) AlbumDetailsActivityAdapter.this.viewModel).load(true);
            }
        });
    }

    public void updateViewOverride() {
        int i;
        int i2 = 8;
        super.updateViewOverride();
        updateLoadingIndicator(((AlbumDetailsActivityViewModel) this.viewModel).isBusy());
        this.switchPanel.setState(((AlbumDetailsActivityViewModel) this.viewModel).getViewModelState().ordinal());
        XLEUtil.updateTextIfNotNull(this.albumDetailTitleTop, JavaUtil.stringToUpper(((AlbumDetailsActivityViewModel) this.viewModel).getTitle()));
        this.albumDetailImage.setImageURI2(((AlbumDetailsActivityViewModel) this.viewModel).getImageUrl(), ((AlbumDetailsActivityViewModel) this.viewModel).getDefaultImageRid());
        if (this.albumDetailTitle != null) {
            this.albumDetailTitle.setText(((AlbumDetailsActivityViewModel) this.viewModel).getTitle());
        }
        this.albumDetailArtist.setText(((AlbumDetailsActivityViewModel) this.viewModel).getArtist());
        CustomTypefaceTextView customTypefaceTextView = this.albumDetailArtist;
        if (((AlbumDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
            i = 0;
        } else {
            i = 8;
        }
        customTypefaceTextView.setVisibility(i);
        if (this.albumDetailsReleaseDate != null) {
            this.albumDetailsReleaseDate.setText(((AlbumDetailsActivityViewModel) this.viewModel).getReleaseYear());
            customTypefaceTextView = this.albumDetailsReleaseDate;
            if (((AlbumDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
                i = 0;
            } else {
                i = 8;
            }
            customTypefaceTextView.setVisibility(i);
        }
        if (this.albumDetailsReleaseDateAndStudio != null) {
            this.albumDetailsReleaseDateAndStudio.setText(((AlbumDetailsActivityViewModel) this.viewModel).getAlbumYearAndStudio());
            customTypefaceTextView = this.albumDetailsReleaseDateAndStudio;
            if (((AlbumDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
                i2 = 0;
            }
            customTypefaceTextView.setVisibility(i2);
        }
        if (this.trackList != ((AlbumDetailsActivityViewModel) this.viewModel).getTracks()) {
            this.trackList = ((AlbumDetailsActivityViewModel) this.viewModel).getTracks();
            this.trackNumberToViewMap = new HashMap();
            if (this.trackList != null) {
                this.allSongListLayout.removeAllViews();
                for (int i3 = 0; i3 < ((AlbumDetailsActivityViewModel) this.viewModel).getTracks().size(); i3++) {
                    EDSV2MusicTrackMediaItem track = (EDSV2MusicTrackMediaItem) ((AlbumDetailsActivityViewModel) this.viewModel).getTracks().get(i3);
                    View itemView = createItemView(track);
                    this.allSongListLayout.addView(itemView, i3);
                    this.trackNumberToViewMap.put(Integer.valueOf(track.getTrackNumber()), itemView);
                }
            }
        }
        if (((AlbumDetailsActivityViewModel) this.viewModel).getNowPlayingTrack() >= 0) {
            setNowPlayingTrack(this.nowPlayingTrackView, false);
            this.nowPlayingTrackView = (View) this.trackNumberToViewMap.get(Integer.valueOf(((AlbumDetailsActivityViewModel) this.viewModel).getNowPlayingTrack()));
            setNowPlayingTrack(this.nowPlayingTrackView, true);
        } else if (this.nowPlayingTrackView != null) {
            setNowPlayingTrack(this.nowPlayingTrackView, false);
            this.nowPlayingTrackView = null;
        }
        if (this.albumDetailShow != null && ((AlbumDetailsActivityViewModel) this.viewModel).isAutoShowSongList() && ((AlbumDetailsActivityViewModel) this.viewModel).getViewModelState() == ListState.ValidContentState) {
            this.albumDetailShow.setChecked(((AlbumDetailsActivityViewModel) this.viewModel).shouldShowMediaProgressBar());
        }
        setCancelableBlocking(((AlbumDetailsActivityViewModel) this.viewModel).isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                ((AlbumDetailsActivityViewModel) AlbumDetailsActivityAdapter.this.viewModel).cancelLaunch();
            }
        });
    }

    private View createItemView(EDSV2MusicTrackMediaItem item) {
        View view = LayoutInflater.from(XLEApplication.MainActivity).inflate(R.layout.album_details_list_row, null);
        if (item != null) {
            ToggleTypefaceTextView albumItemOrder = (ToggleTypefaceTextView) view.findViewById(R.id.album_list_item_order);
            ToggleTypefaceTextView albumItemTitle = (ToggleTypefaceTextView) view.findViewById(R.id.album_list_item_title);
            ToggleTypefaceTextView albumItemDuration = (ToggleTypefaceTextView) view.findViewById(R.id.album_list_item_duration);
            if (albumItemTitle != null) {
                String duration = JavaUtil.getTimeStringMMSS((long) XBLSharedUtil.durationStringToSeconds(item.getDuration()));
                if (item.getDuration() == null || item.getDuration().length() == 0) {
                    duration = (String) XLEApplication.MainActivity.getText(R.string.track_duration_unknown);
                }
                albumItemOrder.setText(String.valueOf(item.getTrackNumber()));
                albumItemTitle.setText(item.getTitle());
                albumItemDuration.setText(duration);
            }
        }
        setNowPlayingTrack(view, false);
        return view;
    }

    private void setNowPlayingTrack(View trackView, boolean nowPlaying) {
        if (trackView != null) {
            ToggleTypefaceTextView albumItemOrder = (ToggleTypefaceTextView) trackView.findViewById(R.id.album_list_item_order);
            ToggleTypefaceTextView albumItemTitle = (ToggleTypefaceTextView) trackView.findViewById(R.id.album_list_item_title);
            ToggleTypefaceTextView albumItemDuration = (ToggleTypefaceTextView) trackView.findViewById(R.id.album_list_item_duration);
            XLEAssert.assertNotNull(albumItemOrder);
            XLEAssert.assertNotNull(albumItemTitle);
            XLEAssert.assertNotNull(albumItemDuration);
            albumItemOrder.setIsPositive(nowPlaying);
            albumItemTitle.setIsPositive(nowPlaying);
            albumItemDuration.setIsPositive(nowPlaying);
        }
    }

    public SwitchPanel getSwitchPanel() {
        return this.switchPanel;
    }
}
