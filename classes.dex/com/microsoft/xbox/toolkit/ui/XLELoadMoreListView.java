package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.microsoft.xbox.toolkit.XboxApplication;

public class XLELoadMoreListView extends XLEListView implements OnScrollListener {
    private View footer;
    private View footerLayout;
    private boolean isLoadingMore;
    private LoadMoreListener loadMoreListener;

    public interface LoadMoreListener {
        boolean isNeedLoadMore();

        void loadMore();
    }

    public XLELoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.isLoadingMore = false;
        this.footerLayout = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(XboxApplication.Instance.getLayoutRValue("load_more_listview_footer"), null);
        this.footer = this.footerLayout.findViewById(XboxApplication.Instance.getIdRValue("load_more_listview_footer"));
        this.footer.setVisibility(8);
        addFooterView(this.footerLayout, null, false);
        setOnScrollListener(this);
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (getLastVisiblePosition() + 1 >= getAdapter().getCount() && scrollState == 0 && !this.isLoadingMore) {
            if (this.loadMoreListener == null || this.loadMoreListener.isNeedLoadMore()) {
                this.footer.setVisibility(0);
                this.isLoadingMore = true;
                if (this.loadMoreListener != null) {
                    this.loadMoreListener.loadMore();
                }
            }
        }
    }

    public void onLoadMoreFinished() {
        this.isLoadingMore = false;
        this.footer.setVisibility(8);
    }
}
