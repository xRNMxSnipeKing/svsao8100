package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SimpleGridAdapter;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel;
import java.util.ArrayList;

public class CollectionTitleGridAdapter extends SimpleGridAdapter<Title> {
    private CollectionActivityViewModel viewModel;

    class CollectionItemViewHolder {
        private final CustomTypefaceTextView dateView;
        private final View gameAchievementsIconView;
        private final TextView gameAchievementsView;
        private final View gameScoreIconView;
        private final TextView gameScoreView;
        private final XLEUniformImageView tileView;
        private final CustomTypefaceTextView titleView;

        public CollectionItemViewHolder(View quickplayView) {
            this.tileView = (XLEUniformImageView) quickplayView.findViewById(R.id.recent_listItem_tile);
            this.gameScoreView = (TextView) quickplayView.findViewById(R.id.game_score);
            this.gameAchievementsView = (TextView) quickplayView.findViewById(R.id.game_achievements);
            this.gameScoreIconView = quickplayView.findViewById(R.id.game_score_icon);
            this.gameAchievementsIconView = quickplayView.findViewById(R.id.game_achievements_icon);
            this.titleView = (CustomTypefaceTextView) quickplayView.findViewById(R.id.recent_item_title);
            this.dateView = (CustomTypefaceTextView) quickplayView.findViewById(R.id.recent_item_date);
        }

        public XLEUniformImageView getTileView() {
            return this.tileView;
        }

        public TextView getGameScoreView() {
            return this.gameScoreView;
        }

        public TextView getGameAchievementsView() {
            return this.gameAchievementsView;
        }

        public View getGameScoreIconView() {
            return this.gameScoreIconView;
        }

        public View getGameAchievementsIconView() {
            return this.gameAchievementsIconView;
        }

        public CustomTypefaceTextView getTitleView() {
            return this.titleView;
        }

        public CustomTypefaceTextView getDateView() {
            return this.dateView;
        }
    }

    public CollectionTitleGridAdapter(Context context, int resourceId, int emptyResourceId, ArrayList<Title> objects, CollectionActivityViewModel vm) {
        super(context, resourceId, emptyResourceId, objects);
        this.viewModel = vm;
    }

    public void setDataObjectAndViewModel(ArrayList<Title> objects, CollectionActivityViewModel vm) {
        this.viewModel = vm;
        updateDataObjects(objects);
    }

    public View getGridView(int index) {
        int i = 8;
        View gridView = super.getGridView(index);
        if (this.viewModel != null) {
            View cellContent = gridView.findViewById(R.id.recent_cell_content);
            boolean isValidContentState;
            if (this.viewModel.getViewModelState() == ListState.ValidContentState) {
                isValidContentState = true;
            } else {
                isValidContentState = false;
            }
            if (cellContent != null) {
                int i2;
                if (getItem(index) == null || !isValidContentState) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                cellContent.setVisibility(i2);
            }
            View cellEmpty = gridView.findViewById(R.id.recent_cell_empty);
            if (cellEmpty != null) {
                if (getItem(index) == null || !isValidContentState) {
                    i = 0;
                }
                cellEmpty.setVisibility(i);
            }
            if (getItem(index) != null && isValidContentState) {
                updatePropertiesForCollection((Title) getItem(index), gridView);
            }
        }
        return gridView;
    }

    public void onItemDestory(View view) {
        if (view != null) {
            view.setOnClickListener(null);
        }
    }

    private void updatePropertiesForCollection(final Title item, View quickplayView) {
        if (quickplayView != null) {
            CollectionItemViewHolder viewHolder;
            if (quickplayView.getTag() != null) {
                viewHolder = (CollectionItemViewHolder) quickplayView.getTag();
            } else {
                viewHolder = new CollectionItemViewHolder(quickplayView);
                quickplayView.setTag(viewHolder);
            }
            if (item.IsGame()) {
                viewHolder.getGameScoreView().setVisibility(0);
                viewHolder.getGameAchievementsView().setVisibility(0);
                viewHolder.getGameScoreIconView().setVisibility(0);
                viewHolder.getGameAchievementsIconView().setVisibility(0);
                viewHolder.getTileView().setImageURI2(item.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(1));
                viewHolder.getGameScoreView().setText(String.format("%d/%d", new Object[]{Integer.valueOf(item.getCurrentGamerScore()), Integer.valueOf(item.getTotalGamerScore())}));
                viewHolder.getGameAchievementsView().setText(String.format("%d/%d", new Object[]{Integer.valueOf(item.getCurrentAchievements()), Integer.valueOf(item.getTotalAchievements())}));
            } else {
                viewHolder.getGameScoreView().setVisibility(8);
                viewHolder.getGameAchievementsView().setVisibility(8);
                viewHolder.getGameScoreIconView().setVisibility(8);
                viewHolder.getGameAchievementsIconView().setVisibility(8);
                if (item.getIsXboxVideo()) {
                    viewHolder.getTileView().setImageURI2(null, R.drawable.xbox_video_boxart);
                } else if (item.getIsXboxMusic()) {
                    viewHolder.getTileView().setImageURI2(null, R.drawable.xbox_music_boxart);
                } else {
                    viewHolder.getTileView().setImageURI2(item.getImageUrl(MeProfileModel.getModel().getLegalLocale()), XLEUtil.getMediaItemDefaultRid(61));
                }
            }
            viewHolder.getTitleView().setText(item.getName());
            if (item.getLastPlayed() != null) {
                viewHolder.getDateView().setText(XLEUtil.dateToDurationSinceNowValidate(item.getLastPlayed()));
            }
            quickplayView.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CollectionTitleGridAdapter.this.viewModel.navigateToAchievementsOrTitleDetail(item);
                }
            }));
        }
    }
}
