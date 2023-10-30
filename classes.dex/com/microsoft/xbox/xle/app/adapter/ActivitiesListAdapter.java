package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.ui.SmartGlassPlayButton;
import com.microsoft.xbox.xle.viewmodel.ActivitySummaryActivityViewModel;

public class ActivitiesListAdapter extends ArrayAdapter<EDSV2ActivityItem> {
    private static final int ACTIVITY_TYPE_FEATURED = 0;
    private static final int ACTIVITY_TYPE_MAX = 2;
    private static final int ACTIVITY_TYPE_NORMAL = 1;
    private EDSV2ActivityItem featuredActivity = this.viewModel.getFeaturedActivity();
    private ActivitySummaryActivityViewModel viewModel;

    public ActivitiesListAdapter(Context context, int rowViewResourceId, ActivitySummaryActivityViewModel vm) {
        super(context, rowViewResourceId, vm.getActivitiesList());
        this.viewModel = vm;
        notifyDataSetChanged();
    }

    public EDSV2ActivityItem getItem(int position) {
        if (this.featuredActivity == null) {
            return (EDSV2ActivityItem) super.getItem(position);
        }
        if (position == 0) {
            return this.featuredActivity;
        }
        return (EDSV2ActivityItem) super.getItem(position - 1);
    }

    public int getItemViewType(int position) {
        if (this.featuredActivity == null || position != 0) {
            return 1;
        }
        return 0;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getCount() {
        if (this.featuredActivity != null) {
            return super.getCount() + 1;
        }
        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isFeatured;
        int i = 0;
        View v = convertView;
        if (getItemViewType(position) == 0) {
            isFeatured = true;
        } else {
            isFeatured = false;
        }
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService("layout_inflater");
            if (isFeatured) {
                v = vi.inflate(R.layout.activity_summary_hero, null);
            } else {
                v = vi.inflate(R.layout.activity_summary_list_row, null);
            }
        }
        final EDSV2ActivityItem activity = getItem(position);
        if (activity != null) {
            v.setTag(activity);
            TextView titleView = (TextView) v.findViewById(R.id.activity_title);
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
            if (providerPriceView != null) {
                providerPriceView.setText(this.viewModel.getProviderPriceStringForActivity(activity));
            }
            if (playView != null) {
                if (!activity.isPurchased()) {
                    i = 8;
                }
                playView.setVisibility(i);
                playView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        ActivitiesListAdapter.this.viewModel.checkRelevantAndLaunchActivity(activity);
                    }
                });
            }
        }
        return v;
    }
}
