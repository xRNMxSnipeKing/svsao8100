package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.XLEApplication;

public class CollectionContentTabletErrorAndEmptyInfoView extends LinearLayout {
    private String info;
    private CustomTypefaceTextView textView;

    public CollectionContentTabletErrorAndEmptyInfoView(Context context) {
        super(context);
        init(context, null);
    }

    public CollectionContentTabletErrorAndEmptyInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CollectionContentTabletErrorAndEmptyInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService("layout_inflater");
        this.info = context.obtainStyledAttributes(attrs, R.styleable.CollectionContentTabletErrorAndEmptyInfoView).getString(0);
        if (XLEApplication.Instance.isAspectRatioLong()) {
            this.textView = (CustomTypefaceTextView) vi.inflate(R.layout.collection_content_error_empty_info_long, this, true).findViewById(R.id.collection_error_empty_info);
        } else {
            this.textView = (CustomTypefaceTextView) vi.inflate(R.layout.collection_content_error_empty_info_not_long, this, true).findViewById(R.id.collection_error_empty_info);
        }
        this.textView.setText(this.info);
    }
}
