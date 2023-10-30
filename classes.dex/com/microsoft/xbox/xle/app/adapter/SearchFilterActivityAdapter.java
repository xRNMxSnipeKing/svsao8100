package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterCount;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.SearchFilterActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class SearchFilterActivityAdapter extends AdapterBaseWithList {
    private SearchFilterListAdapter listAdapter;
    private List<EDSV2SearchFilterCount> searchFilterList;
    private SearchFilterActivityViewModel viewModel;

    public SearchFilterActivityAdapter(SearchFilterActivityViewModel vm) {
        this.screenBody = findViewById(R.id.jfilter_search_activity_body);
        this.listView = (XLEListView) findViewById(R.id.jfilter_search_list_view);
        this.viewModel = vm;
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long arg3) {
                EDSV2SearchFilterCount searchFilter = (EDSV2SearchFilterCount) view.getTag();
                if (searchFilter.getFilterType() != null) {
                    SearchFilterActivityAdapter.this.viewModel.NavigateToFilteredSearchResults(searchFilter.getFilterType());
                }
            }
        });
    }

    public void updateViewOverride() {
        if (this.viewModel.getSearchFiltersList() == null) {
            return;
        }
        if (this.listAdapter == null || this.searchFilterList != this.viewModel.getSearchFiltersList()) {
            this.searchFilterList = this.viewModel.getSearchFiltersList();
            this.listAdapter = new SearchFilterListAdapter(XLEApplication.MainActivity, R.layout.search_filters_list_row, this.searchFilterList);
            this.listView.setAdapter(this.listAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    protected SwitchPanel getSwitchPanel() {
        return null;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
