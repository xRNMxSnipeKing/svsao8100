package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.module.ScreenModuleWithGridLayout;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class CollectionActivityAdapter extends AdapterBaseWithList {
    private SwitchPanel collectionSwitchPanel;
    private View filterContainer;
    private TextView filterTextView;
    private View greenIconButtonContainer;
    private XLEButton listBrowseButton;
    private XLEButton searchRecentItemButton;
    private CollectionActivityViewModel viewModel;

    public CollectionActivityAdapter(CollectionActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.collection_activity_body);
        this.filterContainer = findViewById(R.id.filter_container);
        this.filterTextView = (TextView) findViewById(R.id.filter_text);
        this.collectionSwitchPanel = (SwitchPanel) findViewById(R.id.collection_switch_panel);
        this.greenIconButtonContainer = findViewById(R.id.collection_icon_set);
        this.filterContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CollectionActivityAdapter.this.viewModel.navigateToCollectionFilter();
            }
        });
        this.content = this.collectionSwitchPanel;
        this.listBrowseButton = (XLEButton) findViewById(R.id.list_browse);
        if (this.listBrowseButton != null) {
            this.listBrowseButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CollectionActivityAdapter.this.viewModel.navigateToCollecitonGallery();
                }
            });
        }
        this.searchRecentItemButton = (XLEButton) findViewById(R.id.search_recent_item);
        if (this.searchRecentItemButton != null) {
            this.searchRecentItemButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CollectionActivityAdapter.this.viewModel.navigateToSearchTitles();
                }
            });
        }
        findAndInitializeModuleById(R.id.collection_list_module, this.viewModel);
        findAndInitializeModuleById(R.id.collection_grid_module, this.viewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_search, new OnClickListener() {
            public void onClick(View v) {
                CollectionActivityAdapter.this.viewModel.navigateToSearchTitles();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                CollectionActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        setAppBarButtonEnabled(R.id.appbar_search, !this.viewModel.isBusy());
        this.filterTextView.setText(this.viewModel.getCollectionFilter().getText());
        if (this.collectionSwitchPanel != null) {
            this.collectionSwitchPanel.setState(this.viewModel.getViewModelState().ordinal());
        }
        if (this.greenIconButtonContainer == null) {
            return;
        }
        if (this.viewModel.getViewModelState() == ListState.ValidContentState) {
            this.greenIconButtonContainer.setVisibility(0);
        } else {
            this.greenIconButtonContainer.setVisibility(4);
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return this.collectionSwitchPanel;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }

    protected List<AppBarMenuButton> getTestMenuButtons() {
        if (XLEApplication.Instance.getIsTablet()) {
        }
        return super.getTestMenuButtons();
    }

    protected ScreenModuleWithGridLayout getScreenModuleWithGridLayout() {
        return (ScreenModuleWithGridLayout) findViewById(R.id.collection_grid_module);
    }
}
