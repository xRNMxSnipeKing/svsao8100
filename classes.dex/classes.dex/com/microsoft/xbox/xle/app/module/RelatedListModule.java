package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.MediaItemListAdapter;
import com.microsoft.xbox.xle.viewmodel.AbstractRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class RelatedListModule extends ScreenModuleWithList {
    private ArrayAdapter listAdapter;
    private XLEListView listView;
    private ArrayList<EDSV2MediaItem> mediaItemRelated;
    private AbstractRelatedActivityViewModel viewModel;

    public RelatedListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.details_related_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.details_related_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RelatedListModule.this.viewModel.NavigateToRelatedItemDetails((EDSV2MediaItem) view.getTag());
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getViewModelState() == ListState.ValidContentState && this.mediaItemRelated != this.viewModel.getRelated()) {
            this.mediaItemRelated = (ArrayList) this.viewModel.getRelated();
            if (this.listAdapter == null) {
                this.listAdapter = new MediaItemListAdapter(XLEApplication.getMainActivity(), R.layout.search_results_list_row, this.mediaItemRelated);
                this.listView.setAdapter(this.listAdapter);
                restoreListPosition();
                this.listView.onDataUpdated();
                return;
            }
            this.listView.notifyDataSetChanged();
        }
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (AbstractRelatedActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
