package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.ui.HorizontalListView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;
import com.microsoft.xbox.xle.viewmodel.CollectionGalleryActivityViewModel;
import java.util.ArrayList;

public class CollectionGalleryActivityAdapter extends AdapterBaseNormal {
    private SwitchPanel collectionGallerySwitchPanel;
    private View filterContainer;
    private TextView filterTextView;
    private CollectionGalleryListAdapter listAdapter;
    private HorizontalListView listView;
    private XLEButton searchRecentItemButton;
    private ArrayList<Title> titlesList;
    private CollectionGalleryActivityViewModel viewModel;

    public CollectionGalleryActivityAdapter(CollectionGalleryActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.collection_gallery_activity_body);
        this.filterContainer = findViewById(R.id.filter_container);
        this.filterTextView = (TextView) findViewById(R.id.filter_text);
        this.collectionGallerySwitchPanel = (SwitchPanel) findViewById(R.id.collection_gallery_switch_panel);
        this.filterContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CollectionGalleryActivityAdapter.this.viewModel.navigateToCollectionFilter();
            }
        });
        this.content = this.collectionGallerySwitchPanel;
        this.searchRecentItemButton = (XLEButton) findViewById(R.id.search_recent_item);
        this.searchRecentItemButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CollectionGalleryActivityAdapter.this.viewModel.navigateToSearchTitles();
            }
        });
        this.listView = (HorizontalListView) findViewById(R.id.collection_gallery_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() != null) {
                    CollectionGalleryActivityAdapter.this.viewModel.navigateToAchievementsOrTitleDetail((Title) view.getTag());
                }
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                CollectionGalleryActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        this.filterContainer.setVisibility(this.viewModel.getFilterContainerVisibility());
        this.filterTextView.setText(this.viewModel.getCollectionFilter().getText());
        this.collectionGallerySwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        boolean isNeedCreatNewAdapter = false;
        if (!(this.viewModel.getCollectionFilter() != CollectionFilter.All || this.viewModel.getAllTitleList() == null || this.titlesList == this.viewModel.getAllTitleList())) {
            this.titlesList = this.viewModel.getAllTitleList();
            isNeedCreatNewAdapter = true;
        }
        if (!(this.viewModel.getCollectionFilter() != CollectionFilter.Games || this.viewModel.getGamesList() == null || this.titlesList == this.viewModel.getGamesList())) {
            this.titlesList = this.viewModel.getGamesList();
            isNeedCreatNewAdapter = true;
        }
        if (!(this.viewModel.getCollectionFilter() != CollectionFilter.Apps || this.viewModel.getAppsList() == null || this.titlesList == this.viewModel.getAppsList())) {
            this.titlesList = this.viewModel.getAppsList();
            isNeedCreatNewAdapter = true;
        }
        if (this.titlesList == null) {
            return;
        }
        if (isNeedCreatNewAdapter) {
            this.listAdapter = new CollectionGalleryListAdapter(XLEApplication.getMainActivity(), R.layout.collection_gallery_list_row, this.titlesList);
            this.listView.setAdapter(this.listAdapter);
            restoreListPosition();
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void onStop() {
        super.onStop();
        this.viewModel.setListPosition(this.listView.getFirstVisiblePosition(), this.listView.getCurrentX());
    }

    protected void restoreListPosition() {
        if (this.viewModel != null && this.listView != null) {
            this.listView.scrollTo(this.viewModel.getAndResetListOffset());
        }
    }

    public void onDestroy() {
        this.listView.setOnItemClickListener(null);
    }
}
