package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithList;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel.CollectionFilter;
import com.microsoft.xbox.xle.viewmodel.CollectionFilterActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;

public class CollectionFilterActivityAdapter extends AdapterBaseWithList {
    private CollectionFilterListAdapter listAdapter;
    private CollectionFilterActivityViewModel viewModel;

    public CollectionFilterActivityAdapter(CollectionFilterActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.jfilter_search_activity_body);
        this.listView = (XLEListView) findViewById(R.id.jfilter_search_list_view);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long arg3) {
                CollectionFilter filter = (CollectionFilter) view.getTag();
                if (filter != null) {
                    CollectionFilterActivityAdapter.this.viewModel.onFilterSelected(filter);
                }
            }
        });
    }

    protected SwitchPanel getSwitchPanel() {
        return null;
    }

    protected ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public void updateViewOverride() {
        if (this.listAdapter == null) {
            this.listAdapter = new CollectionFilterListAdapter(XLEApplication.MainActivity, R.layout.search_filters_list_row, this.viewModel.getSelectedFilter());
            this.listView.setAdapter(this.listAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }
}
