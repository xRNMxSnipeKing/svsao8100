package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.AppItemViewHolder;
import com.microsoft.xbox.xle.app.adapter.CollectionTitleListAdapter;
import com.microsoft.xbox.xle.app.adapter.GameItemViewHolder;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class CollectionContentListModule extends ScreenModuleWithList {
    private ArrayList<Title> appsList;
    private ArrayList<Title> gamesList;
    private CollectionTitleListAdapter listAdapter;
    private XLEListView listView;
    private ArrayList<Title> titlesList;
    private CollectionActivityViewModel viewModel;

    public CollectionContentListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.collection_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.collection_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (view.getTag() != null) {
                    Title title = null;
                    if (view.getTag() instanceof GameItemViewHolder) {
                        title = ((GameItemViewHolder) view.getTag()).getGame();
                    } else if (view.getTag() instanceof AppItemViewHolder) {
                        title = (Title) ((AppItemViewHolder) view.getTag()).getKey();
                    }
                    XLEAssert.assertNotNull(title);
                    if (title != null) {
                        CollectionContentListModule.this.viewModel.navigateToAchievementsOrTitleDetail(title);
                    }
                }
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getCollectionFilter() == CollectionFilter.All && this.viewModel.getAllTitleList() != null) {
            if (this.titlesList != this.viewModel.getAllTitleList()) {
                this.titlesList = this.viewModel.getAllTitleList();
                this.listAdapter = new CollectionTitleListAdapter(XLEApplication.getMainActivity(), R.layout.simple_list_row, this.titlesList);
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
            } else {
                this.listView.notifyDataSetChanged();
            }
        }
        if (this.viewModel.getCollectionFilter() == CollectionFilter.Games && this.viewModel.getGamesList() != null) {
            if (this.gamesList != this.viewModel.getGamesList()) {
                this.gamesList = this.viewModel.getGamesList();
                this.listAdapter = new CollectionTitleListAdapter(XLEApplication.getMainActivity(), R.layout.simple_list_row, this.gamesList);
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
            } else {
                this.listView.notifyDataSetChanged();
            }
        }
        if (this.viewModel.getCollectionFilter() == CollectionFilter.Apps && this.viewModel.getAppsList() != null) {
            if (this.appsList != this.viewModel.getAppsList()) {
                this.appsList = this.viewModel.getAppsList();
                this.listAdapter = new CollectionTitleListAdapter(XLEApplication.getMainActivity(), R.layout.simple_list_row, this.appsList);
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
        this.viewModel = (CollectionActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
