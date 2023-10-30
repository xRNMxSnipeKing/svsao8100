package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.ui.SmartGlassPlayButton;
import com.microsoft.xbox.xle.viewmodel.ActivitySummaryActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import java.util.ArrayList;
import java.util.List;

public class ActivitySummaryGridModule extends ScreenModuleLayout {
    private static final int GRID_COLUMN_COUNT = XLEApplication.Resources.getInteger(R.integer.ActivitiesGridColumnCount_tablet);
    private ArrayList<EDSV2ActivityItem> activityList;
    private ScrollView activityScrollView;
    private TableLayout gridView;
    private ActivitySummaryActivityViewModel viewModel;

    public ActivitySummaryGridModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(R.layout.activity_summary_activity_content);
    }

    protected void onFinishInflate() {
        this.gridView = (TableLayout) findViewById(R.id.activity_summary_grid);
        this.activityScrollView = (ScrollView) findViewById(R.id.activity_summary_grid_scroll);
    }

    public void updateView() {
        if ((this.viewModel.getFeaturedActivity() != null || (this.viewModel.getActivitiesList() != null && this.viewModel.getActivitiesList().size() > 0)) && this.activityList != this.viewModel.getActivitiesList()) {
            this.activityList = this.viewModel.getActivitiesList();
            buildActivityItems(this.gridView, this.activityList, this.viewModel.getFeaturedActivity());
            restoreScrollPosition();
        }
    }

    public void onStop() {
        super.onStop();
        saveScrollPosition();
    }

    public void setViewModel(ViewModelBase vm) {
        this.viewModel = (ActivitySummaryActivityViewModel) vm;
    }

    public ViewModelBase getViewModel() {
        return this.viewModel;
    }

    private void restoreScrollPosition() {
        if (this.viewModel != null && this.activityScrollView != null) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    ActivitySummaryGridModule.this.activityScrollView.scrollTo(0, ActivitySummaryGridModule.this.viewModel.getAndResetListOffset());
                }
            });
        }
    }

    private void saveScrollPosition() {
        if (this.viewModel != null && this.activityScrollView != null) {
            this.viewModel.setListPosition(0, this.activityScrollView.getScrollY());
        }
    }

    private void buildActivityItems(TableLayout table, List<EDSV2ActivityItem> items, EDSV2ActivityItem featuredItem) {
        table.removeAllViews();
        table.setStretchAllColumns(true);
        if (featuredItem != null) {
            TableRow featuredTableRow = new TableRow(XLEApplication.Instance.getApplicationContext());
            featuredTableRow.addView(buildActivityItem(featuredItem, true), new LayoutParams(0, -2, 1.0f));
            table.addView(featuredTableRow, new TableLayout.LayoutParams(-1, -2));
        }
        if (items != null && items.size() > 0) {
            int numRows = (items.size() / GRID_COLUMN_COUNT) + (items.size() % GRID_COLUMN_COUNT == 0 ? 0 : 1);
            for (int row = 0; row < numRows; row++) {
                TableRow currentTableRow = new TableRow(XLEApplication.Instance.getApplicationContext());
                for (int col = 0; col < GRID_COLUMN_COUNT; col++) {
                    View cell;
                    int tableRowIndex = (GRID_COLUMN_COUNT * row) + col;
                    if (tableRowIndex < items.size()) {
                        cell = buildActivityItem((EDSV2ActivityItem) items.get(tableRowIndex), false);
                    } else {
                        cell = new View(XLEApplication.Instance.getApplicationContext());
                    }
                    currentTableRow.addView(cell, new LayoutParams(0, -2, 1.0f));
                }
                table.addView(currentTableRow, new TableLayout.LayoutParams(-1, -2));
            }
        }
    }

    private View buildActivityItem(EDSV2ActivityItem item, boolean isFeatured) {
        View v;
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService("layout_inflater");
        if (isFeatured) {
            v = vi.inflate(R.layout.activity_summary_hero, null);
        } else {
            v = vi.inflate(R.layout.activity_summary_list_row, null);
        }
        final EDSV2ActivityItem activity = item;
        if (activity != null) {
            v.setTag(activity);
            TextView titleView = (TextView) v.findViewById(R.id.activity_title);
            TextView descriptionView = (TextView) v.findViewById(R.id.activity_description);
            TextView providerPriceView = (TextView) v.findViewById(R.id.activity_price);
            SmartGlassPlayButton playView = (SmartGlassPlayButton) v.findViewById(R.id.activity_play);
            XLEUniformImageView tileView = (XLEUniformImageView) v.findViewById(R.id.activity_image);
            if (tileView != null) {
                if (isFeatured) {
                    tileView.setImageURI2(activity.getIcon2x1Url(), R.drawable.activity_2x1_missing);
                } else {
                    tileView.setImageURI2(activity.getIconUrl(), R.drawable.activity_1x1_missing);
                }
            }
            if (titleView != null) {
                titleView.setText(activity.getTitle());
            }
            if (descriptionView != null) {
                descriptionView.setText(activity.getDescription());
            }
            if (providerPriceView != null) {
                providerPriceView.setText(this.viewModel.getProviderPriceStringForActivity(activity));
            }
            if (playView != null) {
                playView.setVisibility(activity.isPurchased() ? 0 : 8);
                playView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        ActivitySummaryGridModule.this.viewModel.checkRelevantAndLaunchActivity(activity);
                    }
                });
            }
        }
        v.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ActivitySummaryGridModule.this.viewModel.navigateToDetails(activity);
            }
        });
        return v;
    }
}
