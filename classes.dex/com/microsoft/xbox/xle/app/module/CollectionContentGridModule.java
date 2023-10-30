package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.ui.AbstractGridLayout;
import com.microsoft.xbox.toolkit.ui.SimpleGridLayout;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.CollectionTitleGridAdapter;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class CollectionContentGridModule extends ScreenModuleWithGridLayout {
    private ArrayList<Title> appsList;
    private SwitchPanel collectionSwitchPanel;
    private ArrayList<Title> gamesList;
    private CollectionTitleGridAdapter gridAdapter;
    private SimpleGridLayout gridLayout;
    private ArrayList<Title> titlesList;
    private CollectionActivityViewModel viewModel;

    public CollectionContentGridModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.collection_activity_content);
    }

    protected void onFinishInflate() {
        this.gridLayout = (SimpleGridLayout) findViewById(R.id.collection_grid);
        this.collectionSwitchPanel = (SwitchPanel) findViewById(R.id.collection_switch_panel);
        this.gridAdapter = new CollectionTitleGridAdapter(XLEApplication.getMainActivity(), R.layout.recent_grid_cell, R.layout.recent_grid_cell, null, this.viewModel);
        this.gridLayout.setGridAdapter(this.gridAdapter);
    }

    public void updateView() {
        this.collectionSwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        getGridLayout().getGridAdapter().setGridLayoutModelState(this.viewModel.getViewModelState().ordinal());
        if (this.viewModel.getCollectionFilter() == CollectionFilter.All && this.viewModel.getAllTitleList() != null) {
            if (this.titlesList != this.viewModel.getAllTitleList()) {
                this.titlesList = this.viewModel.getAllTitleList();
                this.gridAdapter.setDataObjectAndViewModel(this.titlesList, this.viewModel);
            } else {
                this.gridAdapter.notifyDataChanged();
            }
        }
        if (this.viewModel.getCollectionFilter() == CollectionFilter.Games && this.viewModel.getGamesList() != null) {
            if (this.gamesList != this.viewModel.getGamesList()) {
                this.gamesList = this.viewModel.getGamesList();
                this.gridAdapter.setDataObjectAndViewModel(this.gamesList, this.viewModel);
            } else {
                this.gridAdapter.notifyDataChanged();
            }
        }
        if (this.viewModel.getCollectionFilter() == CollectionFilter.Apps && this.viewModel.getAppsList() != null) {
            if (this.appsList != this.viewModel.getAppsList()) {
                this.appsList = this.viewModel.getAppsList();
                this.gridAdapter.setDataObjectAndViewModel(this.appsList, this.viewModel);
                return;
            }
            this.gridAdapter.notifyDataChanged();
        }
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (CollectionActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    protected SwitchPanel getSwitchPanel() {
        return this.collectionSwitchPanel;
    }

    protected AbstractGridLayout getGridLayout() {
        return this.gridLayout;
    }
}
