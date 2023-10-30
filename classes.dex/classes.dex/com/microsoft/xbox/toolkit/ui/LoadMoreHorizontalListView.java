package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.ui.HorizontalListView.OnScrollListener;

public class LoadMoreHorizontalListView extends HorizontalListView {
    private static final int LOAD_MORE_MAX_DISTANCE = 30;
    private boolean isLoadingMore;
    private LoadMoreListener loadMoreListener;

    public interface LoadMoreListener {
        boolean isNeedLoadMore();

        void loadMore();
    }

    public LoadMoreHorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnScrollListener(new OnScrollListener() {
            public void onScroll(int leftViewIndex, int rightViewIndex, float distanceX) {
                if (LoadMoreHorizontalListView.this.mAdapter != null && rightViewIndex >= LoadMoreHorizontalListView.this.mAdapter.getCount() && distanceX <= 30.0f) {
                    LoadMoreHorizontalListView.this.onLoadMoreStart();
                }
            }
        });
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void onLoadMoreStart() {
        if (!this.isLoadingMore) {
            if (this.loadMoreListener == null || this.loadMoreListener.isNeedLoadMore()) {
                this.isLoadingMore = true;
                if (this.loadMoreListener != null) {
                    this.loadMoreListener.loadMore();
                }
            }
        }
    }

    public void onLoadMoreFinished() {
        this.isLoadingMore = false;
    }
}
