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
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.MediaItemListAdapter;
import com.microsoft.xbox.xle.ui.XLERelatedView;
import com.microsoft.xbox.xle.viewmodel.AbstractRelatedActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class RelatedGridModule extends ScreenModuleWithGrid {
    private XLERelatedView gridView;
    private ArrayAdapter listAdapter;
    private ArrayList<EDSV2MediaItem> meidaItemRelated;
    private int numColumns;
    private AbstractRelatedActivityViewModel viewModel;

    public RelatedGridModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.details_related_activity_content);
    }

    protected void onFinishInflate() {
        this.gridView = (XLERelatedView) findViewById(R.id.details_related_grid);
        if (XLEApplication.Instance.isAspectRatioLong()) {
            this.numColumns = XLEApplication.MainActivity.getResources().getInteger(R.integer.movieRelatedGridColumnCount);
        } else {
            this.numColumns = XLEApplication.MainActivity.getResources().getInteger(R.integer.movieRelatedGridColumnCountNotLong);
        }
        this.gridView.setNumColumns(this.numColumns);
        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RelatedGridModule.this.viewModel.NavigateToRelatedItemDetails((EDSV2MediaItem) view.getTag());
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getViewModelState() == ListState.ValidContentState && this.meidaItemRelated != this.viewModel.getRelated()) {
            this.meidaItemRelated = (ArrayList) this.viewModel.getRelated();
            if (this.listAdapter == null) {
                this.listAdapter = new MediaItemListAdapter(XLEApplication.getMainActivity(), R.layout.search_results_grid_row, this.meidaItemRelated);
                this.gridView.setAdapter(this.listAdapter);
                restorePosition();
                return;
            }
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (AbstractRelatedActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLERelatedView getGridView() {
        return this.gridView;
    }
}
