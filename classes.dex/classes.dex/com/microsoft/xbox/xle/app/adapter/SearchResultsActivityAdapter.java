package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.ui.SearchBarView;
import com.microsoft.xbox.xle.ui.SearchBarView.OnSearchBarListener;
import com.microsoft.xbox.xle.ui.SearchBarView.OnShowOrDismissKeyboardListener;
import com.microsoft.xbox.xle.ui.XLEHandleImeRootView;
import com.microsoft.xbox.xle.ui.XLEHandleImeRootView.HandleImeInterface;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.SearchResultsActivityViewModel;

public class SearchResultsActivityAdapter extends AdapterBaseNormal {
    private View filterContainer;
    private TextView filterTextView;
    private XLEHandleImeRootView handleImeRootView;
    private SearchBarView searchBar;
    private CustomTypefaceTextView searchResultTitleView;
    private SwitchPanel switchPanel;
    private SearchResultsActivityViewModel viewModel;

    public SearchResultsActivityAdapter(SearchResultsActivityViewModel searchResultsViewModel) {
        this.viewModel = searchResultsViewModel;
        View view = findViewById(R.id.search_data_result_activity_layout);
        if (view != null) {
            this.handleImeRootView = (XLEHandleImeRootView) view;
            this.handleImeRootView.setHandleImeInterface(new HandleImeInterface() {
                public void onDismissKeyboard() {
                    ApplicationBarManager.getInstance().show();
                }

                public void onShowKeyboard() {
                    ApplicationBarManager.getInstance().hide();
                }
            });
        }
        this.screenBody = findViewById(R.id.search_data_result_activity_body);
        this.content = findViewById(R.id.search_data_result_switch_panel);
        this.switchPanel = (SwitchPanel) this.content;
        view = findViewById(R.id.search_result_bar);
        if (view != null) {
            this.searchBar = (SearchBarView) view;
        }
        this.filterContainer = findViewById(R.id.filter_container);
        this.filterTextView = (TextView) findViewById(R.id.filter_text);
        this.filterContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SearchResultsActivityAdapter.this.viewModel.NavigateToSearchFilter();
            }
        });
        view = findViewById(R.id.search_data_result_title);
        if (view != null) {
            this.searchResultTitleView = (CustomTypefaceTextView) view;
        }
        findAndInitializeModuleById(R.id.search_data_result_vertical_list, this.viewModel);
        findAndInitializeModuleById(R.id.search_data_result_horizontal_list, this.viewModel);
    }

    public void updateViewOverride() {
        this.switchPanel.setState(this.viewModel.getViewModelState().ordinal());
        this.titleBar.updateIsLoading(this.viewModel.isBusy());
        if (this.searchBar != null) {
            if (!JavaUtil.isNullOrEmpty(this.viewModel.getSearchTag()) && JavaUtil.isNullOrEmpty(this.searchBar.getSearchTag())) {
                this.searchBar.setSearchTag(this.viewModel.getSearchTag());
            }
            if (this.viewModel.isBusy()) {
                this.searchBar.disableSearch();
            } else {
                this.searchBar.enableSearch();
            }
        }
        if (this.searchResultTitleView != null) {
            this.searchResultTitleView.setText(this.viewModel.getSearchResultTitle());
        }
        if (this.viewModel.getSearchResult() != null) {
            this.filterTextView.setText(this.viewModel.getSearchDataCount());
        } else {
            this.filterTextView.setText(this.viewModel.getSearchFilterTypeString());
        }
    }

    public void onPause() {
        super.onPause();
        if (this.searchBar != null) {
            this.searchBar.setOnShowOrDismissKeyboardListener(null);
            this.searchBar.setOnSearchBarListener(null);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.searchBar != null) {
            this.searchBar.setOnShowOrDismissKeyboardListener(new OnShowOrDismissKeyboardListener() {
                public void showKeyboard(View view) {
                    SearchResultsActivityAdapter.this.showKeyboard(view);
                }

                public void dismissKeyboard() {
                    SearchResultsActivityAdapter.this.dismissKeyboard();
                }
            });
            this.searchBar.setOnSearchBarListener(new OnSearchBarListener() {
                public void onSearch() {
                    SearchResultsActivityAdapter.this.viewModel.search(SearchResultsActivityAdapter.this.searchBar.getSearchTag());
                }

                public void onClear() {
                }
            });
        }
    }

    public void onSetInactive() {
        dismissKeyboard();
        super.onSetInactive();
    }
}
