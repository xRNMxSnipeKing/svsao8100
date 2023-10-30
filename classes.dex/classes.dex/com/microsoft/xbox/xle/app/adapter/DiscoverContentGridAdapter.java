package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.HeroGridAdapter;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.DiscoverActivityViewModel2;
import java.util.ArrayList;

public class DiscoverContentGridAdapter extends HeroGridAdapter<EDSV2MediaItem> {
    private int featuresNum;
    private DiscoverActivityViewModel2 viewModel;

    public DiscoverContentGridAdapter(Context context, int heroResourceId, int resourceId, int emptyResourceId, ArrayList<EDSV2MediaItem> discoverItemList, DiscoverActivityViewModel2 viewModel, int featuresNum) {
        super(context, heroResourceId, resourceId, emptyResourceId, discoverItemList);
        this.featuresNum = featuresNum;
        this.viewModel = viewModel;
    }

    public void setDataObjectAndViewModel(ArrayList<EDSV2MediaItem> objects, DiscoverActivityViewModel2 vm) {
        this.viewModel = vm;
        updateDataObjects(objects);
    }

    public View getGridView(int index) {
        int i = 8;
        View gridView = super.getGridView(index);
        gridView.setBackgroundResource(R.color.emptyCardBackground);
        if (this.viewModel != null) {
            View cellContent = gridView.findViewById(R.id.discover_cell_content);
            boolean isValidContentState;
            if (this.viewModel.getViewModelState() == ListState.ValidContentState) {
                isValidContentState = true;
            } else {
                isValidContentState = false;
            }
            if (!(cellContent == null || this.viewModel == null)) {
                int i2;
                if (getItem(index) == null || !isValidContentState) {
                    i2 = 8;
                } else {
                    i2 = 0;
                }
                cellContent.setVisibility(i2);
            }
            View cellEmpty = gridView.findViewById(R.id.discover_cell_empty);
            if (!(cellEmpty == null || this.viewModel == null)) {
                if (getItem(index) == null || !isValidContentState) {
                    i = 0;
                }
                cellEmpty.setVisibility(i);
            }
            if (getItem(index) != null && isValidContentState) {
                if (index == 0) {
                    updateHeroViewProperties((EDSV2MediaItem) getItem(index), gridView);
                } else {
                    updateRestViewProperties((EDSV2MediaItem) getItem(index), gridView, index);
                }
            }
        }
        return gridView;
    }

    private void updateHeroViewProperties(final EDSV2MediaItem discoverItem, View gridView) {
        CustomTypefaceTextView itemType = (CustomTypefaceTextView) gridView.findViewById(R.id.gridItem_type);
        CustomTypefaceTextView itemTitle = (CustomTypefaceTextView) gridView.findViewById(R.id.gridItem_title);
        XLEImageViewFast smartGlassIcon = (XLEImageViewFast) gridView.findViewById(R.id.smartglassicon);
        ((XLEUniformImageView) gridView.findViewById(R.id.discover_hero_gridItem_image)).setImageURI2(discoverItem.getImageUrl(), XLEUtil.getMediaItemDefaultRid(discoverItem.getMediaType()));
        itemType.setText(XLEUtil.getMediaItemDefaultTypeName(discoverItem.getMediaType()));
        itemTitle.setText(discoverItem.getTitle());
        smartGlassIcon.setVisibility(discoverItem.getHasSmartGlassActivity() ? 0 : 4);
        gridView.setBackgroundResource(R.drawable.tile_bg_light);
        gridView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DiscoverContentGridAdapter.this.viewModel.navigateToItemDetails(discoverItem);
            }
        });
    }

    private void updateRestViewProperties(final EDSV2MediaItem discoverItem, View gridView, int index) {
        CustomTypefaceTextView itemType = (CustomTypefaceTextView) gridView.findViewById(R.id.gridItem_type);
        CustomTypefaceTextView itemTitle = (CustomTypefaceTextView) gridView.findViewById(R.id.gridItem_title);
        XLEImageViewFast smartGlassIcon = (XLEImageViewFast) gridView.findViewById(R.id.smartglassicon);
        ((XLEUniformImageView) gridView.findViewById(R.id.discover_gridItem_image)).setImageURI2(discoverItem.getImageUrl(), XLEUtil.getMediaItemDefaultRid(discoverItem.getMediaType()));
        itemType.setText(XLEUtil.getMediaItemDefaultTypeName(discoverItem.getMediaType()));
        itemTitle.setText(discoverItem.getTitle());
        smartGlassIcon.setVisibility(discoverItem.getHasSmartGlassActivity() ? 0 : 4);
        gridView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DiscoverContentGridAdapter.this.viewModel.navigateToItemDetails(discoverItem);
            }
        });
        if (index < this.featuresNum) {
            gridView.setBackgroundResource(R.drawable.tile_bg_light);
            return;
        }
        gridView.setBackgroundResource(R.drawable.tile_bg_dark);
        itemTitle.setTextColor(-1);
    }

    public void onItemDestory(View view) {
        if (view != null) {
            view.setOnClickListener(null);
        }
    }
}
