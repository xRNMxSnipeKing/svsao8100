package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceEditText;
import com.microsoft.xbox.toolkit.ui.TextHintView;
import com.microsoft.xbox.toolkit.ui.XLEButton;

public class SearchBarView extends RelativeLayout {
    private XLEButton clearButton;
    private OnSearchBarListener onSearchBarListener;
    private OnShowOrDismissKeyboardListener onShowOrDismissKeyboardListener;
    private View searchBarLayout;
    private TextHintView searchHintView;
    private CustomTypefaceEditText searchTagInputEdit;

    public interface OnSearchBarListener {
        void onClear();

        void onSearch();
    }

    public interface OnShowOrDismissKeyboardListener {
        void dismissKeyboard();

        void showKeyboard(View view);
    }

    public SearchBarView(Context context) {
        super(context);
        init(context);
    }

    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.search_bar_layout, this, true);
        this.searchBarLayout = findViewById(R.id.search_bar_layout);
        this.searchTagInputEdit = (CustomTypefaceEditText) findViewById(R.id.search_tag_input);
        this.searchTagInputEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (SearchBarView.this.searchTagInputEdit.getText() == null || SearchBarView.this.searchTagInputEdit.getText().length() <= 0) {
                    SearchBarView.this.clearButton.setEnabled(false);
                } else {
                    SearchBarView.this.clearButton.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }
        });
        this.clearButton = (XLEButton) findViewById(R.id.searchbar_clear_button);
        this.searchHintView = (TextHintView) findViewById(R.id.search_bar_hint);
        this.searchHintView.setVisibility(0);
        this.searchHintView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                SearchBarView.this.searchHintView.setVisibility(8);
                SearchBarView.this.searchBarLayout.setBackgroundResource(R.color.searchBarFocus);
                SearchBarView.this.searchTagInputEdit.requestFocus();
                if (SearchBarView.this.onShowOrDismissKeyboardListener != null) {
                    SearchBarView.this.onShowOrDismissKeyboardListener.showKeyboard(SearchBarView.this.searchTagInputEdit);
                }
                return true;
            }
        });
        this.searchTagInputEdit.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!(JavaUtil.isNullOrEmpty(v.getText().toString()) || SearchBarView.this.onSearchBarListener == null || actionId != 3)) {
                    SearchBarView.this.onSearchBarListener.onSearch();
                    if (SearchBarView.this.onShowOrDismissKeyboardListener != null) {
                        SearchBarView.this.onShowOrDismissKeyboardListener.dismissKeyboard();
                    }
                }
                return true;
            }
        });
        this.clearButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SearchBarView.this.searchTagInputEdit.setText("");
                SearchBarView.this.searchTagInputEdit.requestFocus();
                if (SearchBarView.this.onSearchBarListener != null) {
                    SearchBarView.this.onSearchBarListener.onClear();
                }
            }
        });
    }

    public void setOnSearchBarListener(OnSearchBarListener onSearchBarListener) {
        this.onSearchBarListener = onSearchBarListener;
    }

    public void setOnShowOrDismissKeyboardListener(OnShowOrDismissKeyboardListener onShowOrDismissKeyboardListener) {
        this.onShowOrDismissKeyboardListener = onShowOrDismissKeyboardListener;
    }

    public void setSearchTag(String searchTag) {
        if (!JavaUtil.isNullOrEmpty(searchTag)) {
            this.searchHintView.setVisibility(8);
            this.searchTagInputEdit.setText(searchTag);
            this.searchTagInputEdit.requestFocus();
            this.searchTagInputEdit.setSelection(searchTag.length());
            this.searchTagInputEdit.clearFocus();
        }
    }

    public String getSearchTag() {
        return this.searchTagInputEdit.getText().toString();
    }

    public void disableSearch() {
        this.searchTagInputEdit.setEnabled(false);
        this.clearButton.setEnabled(false);
    }

    public void enableSearch() {
        this.searchTagInputEdit.setEnabled(true);
        this.clearButton.setEnabled(true);
    }

    public void onSetInactive() {
        this.searchHintView.dimissCountDownTimer();
        this.searchTagInputEdit.clearFocus();
        this.clearButton.requestFocus();
    }

    public void onSetActive() {
        if (JavaUtil.isNullOrEmpty(this.searchTagInputEdit.getText().toString())) {
            this.searchHintView.setVisibility(0);
        }
    }
}
