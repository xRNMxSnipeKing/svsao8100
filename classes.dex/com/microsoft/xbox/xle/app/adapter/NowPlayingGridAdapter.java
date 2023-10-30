package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumMediaItem;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.HeroGridAdapter;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.ui.SmartGlassPlayButton;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel.ActivityType;
import java.util.ArrayList;
import java.util.List;

public class NowPlayingGridAdapter extends HeroGridAdapter<Object> {
    private static ArrayList<Object> emptyHeroData = new ArrayList();
    private int indexForCellBelowHero;
    private int indexForCellBelowHeroWithoutHero;
    private NowPlayingActivityViewModel viewModel;

    static {
        emptyHeroData.add(new Object());
    }

    public NowPlayingGridAdapter(Context context, boolean isLongAspectRatio) {
        super(context, R.layout.nowplaying_gridcell_hero, R.layout.nowplaying_gridcell, R.layout.nowplaying_gridcell, emptyHeroData);
        if (isLongAspectRatio) {
            this.indexForCellBelowHero = XLEApplication.Resources.getInteger(R.integer.nowPlayingIndexForCellBelowHero);
            this.indexForCellBelowHeroWithoutHero = XLEApplication.Resources.getInteger(R.integer.nowPlayingIndexForCellBelowHeroWithoutHero);
            return;
        }
        this.indexForCellBelowHero = XLEApplication.Resources.getInteger(R.integer.nowPlayingIndexForCellBelowHeroNotLong);
        this.indexForCellBelowHeroWithoutHero = XLEApplication.Resources.getInteger(R.integer.nowPlayingIndexForCellBelowHeroWithoutHeroNotLong);
    }

    public void setViewModel(NowPlayingActivityViewModel vm) {
        this.viewModel = vm;
    }

    public View getGridView(int index) {
        View gridView = super.getGridView(index);
        if (this.viewModel != null) {
            if (index == 0) {
                updateHeroCell(gridView);
            } else if (index == this.indexForCellBelowHero) {
                updateBottomCell(gridView);
            } else {
                updateCellForRelatedOrQuickplay(index < this.indexForCellBelowHero ? index - 1 : index - 2, gridView);
            }
        }
        return gridView;
    }

    protected View createGridView(int index) {
        View v = super.createGridView(index);
        if (index == this.indexForCellBelowHero) {
            return ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.nowplaying_gridcell_belowhero, null);
        }
        return v;
    }

    public void onItemDestory(View view) {
        if (view != null) {
            view.setOnClickListener(null);
        }
    }

    private void updateBottomCell(View gridView) {
        View activityCell = gridView.findViewById(R.id.cell_activity);
        View noActivityCell_1 = gridView.findViewById(R.id.cell_no_activity_1);
        View noActivityCell_2 = gridView.findViewById(R.id.cell_no_activity_2);
        activityCell.setVisibility(8);
        noActivityCell_1.setVisibility(8);
        noActivityCell_2.setVisibility(8);
        View emptyView = gridView.findViewById(R.id.cell_empty);
        if (emptyView != null) {
            emptyView.setVisibility(8);
        }
        XLEUniformImageView heroActivityTileView = (XLEUniformImageView) activityCell.findViewById(R.id.activity_image);
        TextView heroActivityTitleView = (TextView) activityCell.findViewById(R.id.activity_title);
        TextView heroActivityProviderPriceView = (TextView) activityCell.findViewById(R.id.activity_price);
        TextView heroActivityDescription = (TextView) activityCell.findViewById(R.id.activity_description);
        SmartGlassPlayButton heroActivityPlayView = (SmartGlassPlayButton) activityCell.findViewById(R.id.activity_play);
        ActivityType activityType = this.viewModel.getActivityType();
        if (activityType == ActivityType.Hero) {
            final EDSV2ActivityItem currentHeroActivity = this.viewModel.getHeroActivity();
            XLEAssert.assertNotNull("Activity type should not be Hero if the hero activity is null!", currentHeroActivity);
            activityCell.setVisibility(0);
            heroActivityTileView.setImageURI2(currentHeroActivity.getImageUrl(), -1, R.drawable.activity_1x1_missing);
            heroActivityTitleView.setText(currentHeroActivity.getTitle());
            heroActivityProviderPriceView.setText(currentHeroActivity.getPriceString());
            heroActivityPlayView.setVisibility(currentHeroActivity.isPurchased() ? 0 : 8);
            heroActivityDescription.setText(currentHeroActivity.getDescription());
            activityCell.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    NowPlayingGridAdapter.this.viewModel.navigateToActivityDetails(currentHeroActivity);
                }
            }));
            heroActivityPlayView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    NowPlayingGridAdapter.this.viewModel.launchActivity(currentHeroActivity);
                }
            });
        } else if (activityType == ActivityType.Controller) {
            activityCell.setVisibility(0);
            heroActivityTileView.setImageURI2(null, R.drawable.controlleractivityicon);
            heroActivityTitleView.setText(R.string.now_playing_controller);
            heroActivityProviderPriceView.setText(null);
            heroActivityPlayView.setVisibility(0);
            heroActivityDescription.setText(null);
            heroActivityPlayView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    NowPlayingGridAdapter.this.viewModel.navigateToRemote(true);
                }
            });
            activityCell.setOnClickListener(null);
        } else {
            activityCell.setVisibility(8);
            activityCell.setOnClickListener(null);
            heroActivityPlayView.setOnClickListener(null);
            noActivityCell_1.setVisibility(0);
            noActivityCell_2.setVisibility(0);
            updateCellForRelatedOrQuickplay(this.indexForCellBelowHeroWithoutHero, noActivityCell_1);
            updateCellForRelatedOrQuickplay(this.indexForCellBelowHeroWithoutHero + 1, noActivityCell_2);
        }
    }

    private void updateCellForRelatedOrQuickplay(int sideCellIndex, View cellView) {
        View quickplayView = cellView.findViewById(R.id.cell_quickplay);
        View emptyView = cellView.findViewById(R.id.cell_empty);
        View relatedView = cellView.findViewById(R.id.cell_related);
        quickplayView.setVisibility(8);
        emptyView.setVisibility(8);
        relatedView.setVisibility(8);
        if (this.viewModel.getShouldShowNowPlaying()) {
            switch (this.viewModel.getRelatedContentType()) {
                case QuickPlay:
                    List<Title> quickplayList = this.viewModel.getQuickplayList();
                    int sideCellIndexOffset = this.viewModel.isZuneInQuickplayListFirstItem() ? 0 : 1;
                    if (quickplayList != null && quickplayList.size() > sideCellIndex + sideCellIndexOffset) {
                        updatePropertiesForQuickplay((Title) quickplayList.get(sideCellIndex + sideCellIndexOffset), quickplayView);
                        quickplayView.setVisibility(0);
                        break;
                    }
                case Related:
                    ArrayList<EDSV2MediaItem> relatedItems = this.viewModel.getRelated();
                    if (relatedItems != null && relatedItems.size() > sideCellIndex) {
                        updatePropertiesForRelated((EDSV2MediaItem) relatedItems.get(sideCellIndex), relatedView);
                        relatedView.setVisibility(0);
                        break;
                    }
                case Album:
                    ArrayList<EDSV2MusicAlbumMediaItem> relatedAlbums = this.viewModel.getRelatedAlbums();
                    if (relatedAlbums != null && relatedAlbums.size() > sideCellIndex) {
                        updatePropertiesForRelated((EDSV2MediaItem) relatedAlbums.get(sideCellIndex), relatedView);
                        relatedView.setVisibility(0);
                        break;
                    }
            }
            if (quickplayView.getVisibility() != 0 && relatedView.getVisibility() != 0) {
                emptyView.setVisibility(0);
                return;
            }
            return;
        }
        emptyView.setVisibility(0);
    }

    private void updateHeroCell(View heroView) {
        if (this.viewModel.getShouldShowNowPlaying()) {
            heroView.findViewById(R.id.hero_nowplaying).setVisibility(0);
            heroView.findViewById(R.id.hero_nolast).setVisibility(8);
            updateViewPropertiesForNowplaying(heroView.findViewById(R.id.hero_nowplaying));
            return;
        }
        heroView.findViewById(R.id.hero_nolast).setVisibility(0);
        heroView.findViewById(R.id.hero_nowplaying).setVisibility(8);
    }

    private void updateViewPropertiesForNowplaying(View view) {
        TextView nowPlayingTitle = (TextView) view.findViewById(R.id.now_playing_title);
        TextView nowPlayingSubTitle = (TextView) view.findViewById(R.id.now_playing_subtitle);
        TextView nowPlayingProviderText = (TextView) view.findViewById(R.id.now_playing_provider_text);
        ((XLEUniformImageView) view.findViewById(R.id.now_playing_tile)).setImageURI2(this.viewModel.getNowPlayingTileUrl(), -1, this.viewModel.getNowPlayingDefaultImageRid());
        nowPlayingTitle.setText(this.viewModel.getNowPlayingTitle());
        if (this.viewModel.getNowPlayingSubTitle() == null) {
            nowPlayingSubTitle.setVisibility(8);
        } else {
            nowPlayingSubTitle.setVisibility(0);
            nowPlayingSubTitle.setText(this.viewModel.getNowPlayingSubTitle());
        }
        nowPlayingProviderText.setText(this.viewModel.getProviderName());
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NowPlayingGridAdapter.this.viewModel.navigateToNowPlayingDetails();
            }
        });
    }

    private void updatePropertiesForQuickplay(final Title item, View quickplayView) {
        if (quickplayView != null) {
            quickplayView.setTag(item);
            XLEUniformImageView tileView = (XLEUniformImageView) quickplayView.findViewById(R.id.quickplay_listItem_tile);
            TextView gameScoreView = (TextView) quickplayView.findViewById(R.id.game_score);
            TextView gameAchievementsView = (TextView) quickplayView.findViewById(R.id.game_achievements);
            View gameScoreIconView = quickplayView.findViewById(R.id.game_score_icon);
            View gameAchievementsIconView = quickplayView.findViewById(R.id.game_achievements_icon);
            gameScoreView.setVisibility(8);
            gameAchievementsView.setVisibility(8);
            gameScoreIconView.setVisibility(8);
            gameAchievementsIconView.setVisibility(8);
            if (item.IsGame()) {
                tileView.setImageURI2(item.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(1));
                gameScoreView.setText(String.format("%d/%d", new Object[]{Integer.valueOf(item.getCurrentGamerScore()), Integer.valueOf(item.getTotalGamerScore())}));
                gameAchievementsView.setText(String.format("%d/%d", new Object[]{Integer.valueOf(item.getCurrentAchievements()), Integer.valueOf(item.getTotalAchievements())}));
                gameScoreView.setVisibility(0);
                gameAchievementsView.setVisibility(0);
                gameScoreIconView.setVisibility(0);
                gameAchievementsIconView.setVisibility(0);
            } else if (item.getIsXboxVideo()) {
                tileView.setImageURI2(null, R.drawable.xbox_video_boxart);
            } else if (item.getIsXboxMusic()) {
                tileView.setImageURI2(null, R.drawable.xbox_music_boxart);
            } else {
                tileView.setImageURI2(item.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(61));
            }
            ((CustomTypefaceTextView) quickplayView.findViewById(R.id.quickplay_title)).setText(item.getName());
            CustomTypefaceTextView dateView = (CustomTypefaceTextView) quickplayView.findViewById(R.id.quickplay_date);
            if (item.getLastPlayed() != null) {
                dateView.setText(XLEUtil.dateToDurationSinceNowValidate(item.getLastPlayed()));
            }
            quickplayView.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    NowPlayingGridAdapter.this.viewModel.launchOnConsole(item);
                }
            }));
        }
    }

    private void updatePropertiesForRelated(final EDSV2MediaItem item, View view) {
        if (view != null) {
            view.setTag(item);
            XLEUniformImageView imageView = (XLEUniformImageView) view.findViewById(R.id.nowplaying_related_image);
            TextView typeView = (TextView) view.findViewById(R.id.nowplaying_related_type);
            TextView titleView = (TextView) view.findViewById(R.id.nowplaying_related_title);
            View smartglassView = view.findViewById(R.id.nowplaying_related_smartglassicon);
            imageView.setImageURI2(item.getImageUrl(), XLEUtil.getMediaItemDefaultRid(item.getMediaType()));
            typeView.setText(XLEUtil.getNowPlayingRelatedMediaItemTypeName(item.getMediaType()));
            titleView.setText(item.getTitle());
            smartglassView.setVisibility(item.getHasSmartGlassActivity() ? 0 : 4);
            view.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    NowPlayingGridAdapter.this.viewModel.navigateToRelated(item);
                }
            }));
        }
    }
}
