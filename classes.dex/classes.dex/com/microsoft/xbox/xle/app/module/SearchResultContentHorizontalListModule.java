package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchResultItem;
import com.microsoft.xbox.toolkit.ui.LoadMoreHorizontalListView;
import com.microsoft.xbox.toolkit.ui.LoadMoreHorizontalListView.LoadMoreListener;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.MediaItemListAdapter;
import com.microsoft.xbox.xle.viewmodel.SearchResultsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.List;

public class SearchResultContentHorizontalListModule extends ScreenModuleLayout {
    private ArrayAdapter listAdapter;
    private LoadMoreHorizontalListView listView;
    private List<EDSV2SearchResultItem> searchResults;
    private SearchResultsActivityViewModel viewModel;

    public SearchResultContentHorizontalListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.search_results_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (LoadMoreHorizontalListView) findViewById(R.id.search_data_result_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object tag = view.getTag();
                if (tag != null) {
                    SearchResultContentHorizontalListModule.this.viewModel.NavigateToSearchResultDetails((EDSV2SearchResultItem) tag);
                }
            }
        });
        this.listView.setLoadMoreListener(new LoadMoreListener() {
            public void loadMore() {
                SearchResultContentHorizontalListModule.this.viewModel.loadMore();
            }

            public boolean isNeedLoadMore() {
                return SearchResultContentHorizontalListModule.this.viewModel.isNeedLoadMore();
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getSearchResult() == null) {
            return;
        }
        if (this.listAdapter == null || this.searchResults != this.viewModel.getSearchResult()) {
            this.searchResults = this.viewModel.getSearchResult();
            this.listAdapter = new MediaItemListAdapter(XLEApplication.getMainActivity(), R.layout.search_results_list_row, this.searchResults);
            this.listView.setAdapter(this.listAdapter);
            restoreListPosition();
            return;
        }
        if (this.viewModel.isLoadMoreFinished()) {
            this.listView.onLoadMoreFinished();
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void onStop() {
        super.onStop();
        this.viewModel.setListPosition(this.listView.getFirstVisiblePosition(), this.listView.getCurrentX());
    }

    protected void restoreListPosition() {
        ViewModelBase vm = getViewModel();
        if (vm != null && this.listView != null) {
            this.listView.scrollTo(vm.getAndResetListOffset());
        }
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (SearchResultsActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }
}
