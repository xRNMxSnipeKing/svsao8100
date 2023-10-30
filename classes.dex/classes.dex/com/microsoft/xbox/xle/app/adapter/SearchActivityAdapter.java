package com.microsoft.xbox.xle.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.SearchTermData;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.ui.SearchBarView;
import com.microsoft.xbox.xle.ui.SearchBarView.OnSearchBarListener;
import com.microsoft.xbox.xle.ui.SearchBarView.OnShowOrDismissKeyboardListener;
import com.microsoft.xbox.xle.ui.XLEHandleImeRootView;
import com.microsoft.xbox.xle.ui.XLEHandleImeRootView.HandleImeInterface;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.SearchActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class SearchActivityAdapter extends AdapterBaseWithList {
    private XLEHandleImeRootView handleImeRootView;
    private ArrayAdapter<SearchTermData> listAdapter;
    private List<SearchTermData> popularNow;
    private SearchBarView searchBar;
    private SearchActivityViewModel viewModel;

    public SearchActivityAdapter(SearchActivityViewModel searchViewModel) {
        this.viewModel = searchViewModel;
        this.popularNow = null;
        this.handleImeRootView = (XLEHandleImeRootView) findViewById(R.id.search_data_activity_layout);
        this.screenBody = findViewById(R.id.search_data_activity_body);
        this.searchBar = (SearchBarView) findViewById(R.id.search_bar);
        this.listView = (XLEListView) findViewById(R.id.search_data_popular_now_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object tag = view.getTag();
                if (tag != null) {
                    SearchActivityAdapter.this.viewModel.NavigateToSearchResultDetails(((SearchTermData) tag).getValue());
                }
            }
        });
    }

    public void updateViewOverride() {
        if (!(JavaUtil.isNullOrEmpty(this.viewModel.getSearchTag()) || this.viewModel.getSearchTag().equals(this.searchBar.getSearchTag()))) {
            this.searchBar.setSearchTag(this.viewModel.getSearchTag());
        }
        if (this.viewModel.getPopularNow() != null) {
            if (this.listAdapter == null || this.popularNow != this.viewModel.getPopularNow()) {
                this.popularNow = this.viewModel.getPopularNow();
                this.listAdapter = new PopularNowListAdapter(XLEApplication.getMainActivity(), R.layout.popular_now_list_row, this.popularNow);
                if (this.listView.getHeaderViewsCount() == 0) {
                    this.listView.addHeaderView(((LayoutInflater) XLEApplication.getMainActivity().getSystemService("layout_inflater")).inflate(R.layout.popular_now_listview_header, null), null, false);
                }
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
            } else {
                this.listView.notifyDataSetChanged();
            }
        }
        if (this.popularNow == null || this.popularNow.size() <= 0) {
            this.listView.setVisibility(8);
        } else {
            this.listView.setVisibility(0);
        }
    }

    protected SwitchPanel getSwitchPanel() {
        return null;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public void onSetInactive() {
        dismissKeyboard();
        this.viewModel.setSearchTag(this.searchBar.getSearchTag());
        this.searchBar.onSetInactive();
        this.handleImeRootView.setHandleImeInterface(null);
        this.searchBar.setOnSearchBarListener(null);
        this.searchBar.setOnShowOrDismissKeyboardListener(null);
        super.onSetInactive();
    }

    public void onSetActive() {
        this.handleImeRootView.setHandleImeInterface(new HandleImeInterface() {
            public void onDismissKeyboard() {
                ApplicationBarManager.getInstance().show();
            }

            public void onShowKeyboard() {
                ApplicationBarManager.getInstance().hide();
            }
        });
        this.searchBar.setOnSearchBarListener(new OnSearchBarListener() {
            public void onSearch() {
                String searchTag = SearchActivityAdapter.this.searchBar.getSearchTag();
                if (!JavaUtil.isNullOrEmpty(searchTag)) {
                    SearchActivityAdapter.this.viewModel.NavigateToSearchResultDetails(searchTag);
                }
            }

            public void onClear() {
                SearchActivityAdapter.this.viewModel.onSearchBarClear();
            }
        });
        this.searchBar.setOnShowOrDismissKeyboardListener(new OnShowOrDismissKeyboardListener() {
            public void showKeyboard(View view) {
                SearchActivityAdapter.this.showKeyboard(view);
            }

            public void dismissKeyboard() {
                SearchActivityAdapter.this.dismissKeyboard();
            }
        });
        this.searchBar.onSetActive();
        super.onSetActive();
    }
}
