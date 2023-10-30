package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.ActivitiesListAdapter;
import com.microsoft.xbox.xle.viewmodel.ActivitySummaryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;

public class ActivitySummaryListModule extends ScreenModuleWithList {
    private ArrayList<EDSV2ActivityItem> activityList;
    private ActivitiesListAdapter listAdapter;
    private XLEListView listView;
    private ActivitySummaryActivityViewModel viewModel;

    public ActivitySummaryListModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.activity_summary_activity_content);
    }

    protected void onFinishInflate() {
        this.listView = (XLEListView) findViewById(R.id.activity_summary_list);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long arg3) {
                ActivitySummaryListModule.this.viewModel.navigateToDetails((EDSV2ActivityItem) view.getTag());
            }
        });
    }

    public void updateView() {
        if (this.viewModel.getActivitiesList() == null) {
            return;
        }
        if (this.activityList != this.viewModel.getActivitiesList()) {
            this.activityList = this.viewModel.getActivitiesList();
            this.listAdapter = new ActivitiesListAdapter(XLEApplication.getMainActivity(), R.layout.activity_summary_list_row, this.viewModel);
            this.listView.setAdapter(this.listAdapter);
            restoreListPosition();
            this.listView.onDataUpdated();
            return;
        }
        this.listView.notifyDataSetChanged();
    }

    public void onDestroy() {
        this.listView.setOnItemClickListener(null);
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (ActivitySummaryActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    public XLEListView getListView() {
        return this.listView;
    }
}
