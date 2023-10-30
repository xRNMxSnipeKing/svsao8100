package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumMediaItem;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.ToggleTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEImageView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.DetailsMoreOrLessView;
import com.microsoft.xbox.xle.ui.SmartGlassPlayButton;
import com.microsoft.xbox.xle.ui.StarRatingWithUserCountView;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel.ActivityType;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NowPlayingActivityPhoneModule extends ScreenModuleLayout {
    private static final int MAX_RELATED = 4;
    private LinearLayout albumItemContainer;
    private SwitchPanel connectSwitchPanel;
    private EDSV2ActivityItem currentHeroActivity;
    private TextView descriptionHeaderText;
    private DetailsMoreOrLessView detailsDescriptionMoreOrLess;
    private SmartGlassPlayButton heroActivityPlayView;
    private TextView heroActivityProviderPriceView;
    private XLEImageViewFast heroActivityTileView;
    private TextView heroActivityTitleView;
    private View heroActivityView;
    private TextView listHeaderText;
    private View noLastPlayedView;
    private TextView nowPlayingHeaderText;
    private View nowPlayingNotConnected;
    private View nowPlayingNowOnXbox;
    private TextView nowPlayingProgress;
    private TextView nowPlayingProviderText;
    private TextView nowPlayingSubTitle;
    private XLEUniformImageView nowPlayingTile;
    private TextView nowPlayingTitle;
    private LinearLayout quickplayItemContainer;
    private List<Title> quickplayList;
    private ArrayList<EDSV2MusicAlbumMediaItem> relatedAlbums;
    private SwitchPanel relatedContentSwitchPanel;
    private LinearLayout relatedItemContainer;
    private ArrayList<EDSV2MediaItem> relatedItems;
    private TextView relatedNoContentText;
    private SwitchPanel relatedSwitchPanel;
    private NowPlayingActivityViewModel viewModel;

    public NowPlayingActivityPhoneModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.nowplaying_activity_content);
    }

    protected void onFinishInflate() {
        this.relatedSwitchPanel = (SwitchPanel) findViewById(R.id.related_switch_panel);
        this.relatedContentSwitchPanel = (SwitchPanel) findViewById(R.id.related_content_switch_panel);
        this.quickplayItemContainer = (LinearLayout) findViewById(R.id.home_quickplay_item_container);
        this.relatedItemContainer = (LinearLayout) findViewById(R.id.home_related_item_container);
        this.albumItemContainer = (LinearLayout) findViewById(R.id.home_album_item_container);
        this.relatedItems = null;
        this.relatedAlbums = null;
        this.connectSwitchPanel = (SwitchPanel) findViewById(R.id.connect_switch_panel);
        this.nowPlayingNotConnected = findViewById(R.id.now_playing_not_connected);
        this.nowPlayingNotConnected.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NowPlayingActivityPhoneModule.this.viewModel.connectToConsole();
            }
        });
        this.nowPlayingNowOnXbox = findViewById(R.id.now_play_panel);
        this.nowPlayingNowOnXbox.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NowPlayingActivityPhoneModule.this.viewModel.navigateToNowPlayingDetails();
            }
        });
        this.noLastPlayedView = findViewById(R.id.home_no_last_played_view);
        this.nowPlayingHeaderText = (TextView) findViewById(R.id.now_playing_header_text);
        this.nowPlayingTile = (XLEUniformImageView) findViewById(R.id.now_playing_tile);
        this.nowPlayingTitle = (TextView) findViewById(R.id.now_playing_title);
        this.nowPlayingSubTitle = (TextView) findViewById(R.id.now_playing_subtitle);
        this.nowPlayingProviderText = (TextView) findViewById(R.id.now_playing_provider_text);
        this.nowPlayingProgress = (TextView) findViewById(R.id.now_playing_progress);
        this.heroActivityView = findViewById(R.id.now_playing_activity);
        this.heroActivityTileView = (XLEImageViewFast) findViewById(R.id.activity_image);
        this.heroActivityTitleView = (TextView) findViewById(R.id.activity_title);
        this.heroActivityProviderPriceView = (TextView) findViewById(R.id.activity_price);
        this.heroActivityPlayView = (SmartGlassPlayButton) findViewById(R.id.activity_play);
        this.relatedNoContentText = (TextView) findViewById(R.id.related_nocontent_text);
        this.descriptionHeaderText = (TextView) findViewById(R.id.home_description_title_text);
        this.detailsDescriptionMoreOrLess = (DetailsMoreOrLessView) findViewById(R.id.home_nowplaying_details_description_more_or_less);
        this.listHeaderText = (TextView) findViewById(R.id.home_related_title_text);
    }

    public void updateView() {
        int i;
        this.connectSwitchPanel.setState(this.viewModel.getConnectionState().ordinal());
        this.nowPlayingHeaderText.setText(this.viewModel.getNowPlayingHeader());
        this.nowPlayingNowOnXbox.setVisibility(this.viewModel.getShouldShowNowPlaying() ? 0 : 8);
        View view = this.noLastPlayedView;
        if (this.viewModel.getShouldShowNowPlaying()) {
            i = 8;
        } else {
            i = 0;
        }
        view.setVisibility(i);
        this.nowPlayingTile.setImageURI2(this.viewModel.getNowPlayingTileUrl(), -1, this.viewModel.getNowPlayingDefaultImageRid());
        this.nowPlayingTitle.setText(this.viewModel.getNowPlayingTitle());
        if (this.viewModel.getNowPlayingSubTitle() == null) {
            this.nowPlayingSubTitle.setVisibility(8);
        } else {
            this.nowPlayingSubTitle.setVisibility(0);
            this.nowPlayingSubTitle.setText(this.viewModel.getNowPlayingSubTitle());
        }
        this.nowPlayingProviderText.setText(this.viewModel.getProviderName());
        refreshActivity();
        if (this.viewModel.getShouldShowRelated()) {
            this.listHeaderText.setVisibility(0);
            this.listHeaderText.setText(this.viewModel.getRelatedHeader());
            this.relatedSwitchPanel.setVisibility(0);
            this.relatedSwitchPanel.setState(this.viewModel.getListState().ordinal());
            if (this.viewModel.getListState() == ListState.NoContentState) {
                this.relatedNoContentText.setText(this.viewModel.getRelatedNoContentText());
            }
            this.relatedContentSwitchPanel.setState(this.viewModel.getRelatedContentType().ordinal());
        } else {
            this.listHeaderText.setVisibility(8);
            this.relatedSwitchPanel.setVisibility(8);
        }
        if (!this.viewModel.getShouldShowDescription() || JavaUtil.isNullOrEmpty(this.viewModel.getDescription())) {
            this.descriptionHeaderText.setVisibility(8);
            this.detailsDescriptionMoreOrLess.setVisibility(8);
        } else {
            this.descriptionHeaderText.setVisibility(0);
            this.detailsDescriptionMoreOrLess.setVisibility(0);
            this.detailsDescriptionMoreOrLess.setText(this.viewModel.getDescription());
        }
        switch (this.viewModel.getRelatedContentType()) {
            case QuickPlay:
                this.quickplayItemContainer.setVisibility(0);
                this.albumItemContainer.setVisibility(8);
                this.relatedItemContainer.setVisibility(8);
                refreshQuickplay();
                return;
            case Related:
                this.relatedItemContainer.setVisibility(0);
                this.quickplayItemContainer.setVisibility(8);
                this.albumItemContainer.setVisibility(8);
                refreshRelated();
                return;
            case Album:
                this.albumItemContainer.setVisibility(0);
                this.quickplayItemContainer.setVisibility(8);
                this.relatedItemContainer.setVisibility(8);
                refreshAlbum();
                return;
            default:
                this.quickplayItemContainer.setVisibility(8);
                this.albumItemContainer.setVisibility(8);
                this.relatedItemContainer.setVisibility(8);
                return;
        }
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (NowPlayingActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    private void refreshQuickplay() {
        if (this.quickplayList != this.viewModel.getQuickplayList()) {
            this.quickplayList = this.viewModel.getQuickplayList();
            if (this.quickplayList != null) {
                cleanContainer(this.quickplayItemContainer);
                for (int i = 0; i < this.quickplayList.size(); i++) {
                    View view = getQuickplayRow((Title) this.quickplayList.get(i));
                    final Title title = (Title) this.quickplayList.get(i);
                    if (view != null) {
                        view.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                NowPlayingActivityPhoneModule.this.viewModel.launchOnConsole(title);
                            }
                        }));
                        this.quickplayItemContainer.addView(view, i);
                    }
                }
            }
        }
    }

    private void refreshAlbum() {
        if (this.relatedAlbums != this.viewModel.getRelatedAlbums()) {
            this.relatedAlbums = this.viewModel.getRelatedAlbums();
            if (this.relatedAlbums != null) {
                cleanContainer(this.albumItemContainer);
                int i = 0;
                while (i < this.relatedAlbums.size() && i < 4) {
                    View view = getRelatedRow((EDSV2MediaItem) this.relatedAlbums.get(i));
                    final EDSV2MediaItem item = (EDSV2MediaItem) this.relatedAlbums.get(i);
                    if (view != null) {
                        view.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                NowPlayingActivityPhoneModule.this.viewModel.navigateToRelated(item);
                            }
                        }));
                        this.albumItemContainer.addView(view, i);
                    }
                    i++;
                }
            }
        }
        this.relatedAlbums = this.viewModel.getRelatedAlbums();
    }

    private void refreshRelated() {
        if (this.relatedItems != this.viewModel.getRelated()) {
            this.relatedItems = this.viewModel.getRelated();
            if (this.relatedItems != null) {
                cleanContainer(this.relatedItemContainer);
                int i = 0;
                while (i < this.relatedItems.size() && i < 4) {
                    View view = getRelatedRow((EDSV2MediaItem) this.relatedItems.get(i));
                    final EDSV2MediaItem item = (EDSV2MediaItem) this.relatedItems.get(i);
                    if (view != null) {
                        view.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                NowPlayingActivityPhoneModule.this.viewModel.navigateToRelated(item);
                            }
                        }));
                        this.relatedItemContainer.addView(view, i);
                    }
                    i++;
                }
            }
        }
    }

    private void refreshActivity() {
        int i = 0;
        ActivityType activityType = this.viewModel.getActivityType();
        if (activityType == ActivityType.Hero) {
            EDSV2ActivityItem hero = this.viewModel.getHeroActivity();
            XLEAssert.assertNotNull("Activity type should not be Hero if the hero activity is null!", hero);
            this.heroActivityView.setVisibility(0);
            if (this.currentHeroActivity != hero) {
                this.currentHeroActivity = hero;
                this.heroActivityTileView.setImageURI2(this.currentHeroActivity.getImageUrl(), -1, R.drawable.activity_1x1_missing);
                this.heroActivityTitleView.setText(this.currentHeroActivity.getTitle());
                this.heroActivityProviderPriceView.setText(this.currentHeroActivity.getPriceString());
                SmartGlassPlayButton smartGlassPlayButton = this.heroActivityPlayView;
                if (!this.currentHeroActivity.isPurchased()) {
                    i = 8;
                }
                smartGlassPlayButton.setVisibility(i);
                this.heroActivityView.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        NowPlayingActivityPhoneModule.this.viewModel.navigateToActivityDetails(NowPlayingActivityPhoneModule.this.currentHeroActivity);
                    }
                }));
                this.heroActivityPlayView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        NowPlayingActivityPhoneModule.this.viewModel.launchActivity(NowPlayingActivityPhoneModule.this.currentHeroActivity);
                    }
                });
            }
        } else if (activityType == ActivityType.Controller) {
            this.currentHeroActivity = null;
            this.heroActivityView.setVisibility(0);
            this.heroActivityTileView.setImageResource(R.drawable.controlleractivityicon);
            this.heroActivityTitleView.setText(R.string.now_playing_controller);
            this.heroActivityProviderPriceView.setText(null);
            this.heroActivityPlayView.setVisibility(0);
            this.heroActivityPlayView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    NowPlayingActivityPhoneModule.this.viewModel.navigateToRemote(true);
                }
            });
            this.heroActivityView.setOnClickListener(null);
        } else {
            this.currentHeroActivity = null;
            this.heroActivityView.setVisibility(8);
            this.heroActivityView.setOnClickListener(null);
            this.heroActivityPlayView.setOnClickListener(null);
        }
    }

    private void cleanContainer(ViewGroup view) {
        int childCount = view.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = view.getChildAt(i);
            XLEAssert.assertNotNull(child);
            child.setOnClickListener(null);
        }
        view.removeAllViews();
    }

    private View getRelatedRow(EDSV2MediaItem item) {
        View view = LayoutInflater.from(XLEApplication.MainActivity).inflate(R.layout.search_results_list_row, null);
        if (view != null) {
            view.setTag(item);
            XLEUniformImageView imageView = (XLEUniformImageView) view.findViewById(R.id.search_data_result_tile_image);
            CustomTypefaceTextView nameView = (CustomTypefaceTextView) view.findViewById(R.id.search_data_result_name);
            CustomTypefaceTextView artistView = (CustomTypefaceTextView) view.findViewById(R.id.search_data_result_artist);
            XLEImageView typeImage = (XLEImageView) view.findViewById(R.id.search_data_result_type_image);
            CustomTypefaceTextView releaseDateView = (CustomTypefaceTextView) view.findViewById(R.id.tv_episode_details_release_date);
            StarRatingWithUserCountView starView = (StarRatingWithUserCountView) view.findViewById(R.id.search_data_result_rating_with_count);
            View smartglassView = view.findViewById(R.id.smartglassicon);
            if (smartglassView != null) {
                int i;
                if (item.getHasSmartGlassActivity()) {
                    i = 0;
                } else {
                    i = 8;
                }
                smartglassView.setVisibility(i);
            }
            if (imageView != null) {
                imageView.setImageURI2(item.getImageUrl(), XLEUtil.getMediaItemDefaultRid(item.getMediaType()));
                nameView.setText(item.getTitle());
                releaseDateView.setText(item.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(item.getReleaseDate()) : "");
                artistView.setText("");
                starView.setVisibility(8);
                switch (item.getMediaType()) {
                    case 1:
                    case 5:
                    case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
                    case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
                    case EDSV2MediaType.MEDIATYPE_METROGAME /*62*/:
                        typeImage.setImageResource(R.drawable.xboxgame);
                        break;
                    case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                        typeImage.setImageResource(R.drawable.xboxapp);
                        break;
                    case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                        typeImage.setImageResource(R.drawable.movie);
                        break;
                    case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
                    case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
                    case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
                    case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                        typeImage.setImageResource(R.drawable.tv);
                        break;
                    case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                    case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                        typeImage.setImageResource(R.drawable.musictrack);
                        break;
                    case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
                        typeImage.setImageResource(R.drawable.musictrack);
                        break;
                    case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                        typeImage.setImageResource(R.drawable.musicvideo);
                        break;
                    default:
                        XLELog.Error("NowplayingAdapter", "unhandled related type " + item.getMediaType());
                        XLEAssert.assertTrue(false);
                        break;
                }
                if (JavaUtil.isNullOrEmpty(artistView.getText().toString())) {
                    artistView.setVisibility(8);
                }
            }
        }
        return view;
    }

    private View getQuickplayRow(Title item) {
        View view = LayoutInflater.from(XLEApplication.MainActivity).inflate(R.layout.quickplay_list_row, null);
        if (view != null) {
            view.setTag(item);
            XLEUniformImageView tileView = (XLEUniformImageView) view.findViewById(R.id.quickplay_listItem_tile);
            if (item.IsGame()) {
                tileView.setImageURI2(item.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(1));
            } else if (item.getIsXboxVideo()) {
                tileView.setImageURI2(null, R.drawable.xbox_video_boxart);
            } else if (item.getIsXboxMusic()) {
                tileView.setImageURI2(null, R.drawable.xbox_music_boxart);
            } else {
                tileView.setImageURI2(item.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(61));
            }
            ((ToggleTypefaceTextView) view.findViewById(R.id.quickplay_listItem_sender)).setText(item.getName());
        }
        return view;
    }
}
