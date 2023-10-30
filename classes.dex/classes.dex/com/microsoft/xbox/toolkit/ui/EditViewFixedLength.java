package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;

public class EditViewFixedLength extends RelativeLayout {
    private TextView characterCountView = null;
    private int characterCountViewRid;
    private EditTextContainer container;
    private EditText editTextView = null;
    private int editTextViewRid;
    private int inputType;
    private int maxCharacterCount;
    private boolean singleLine;
    private String title;
    private TextView titleView = null;
    private int titleViewRid;

    public EditViewFixedLength(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public EditViewFixedLength(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("EditViewFixedLength"));
        this.title = a.getString(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_title"));
        this.maxCharacterCount = a.getInteger(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_maxCharacterCount"), 0);
        this.singleLine = a.getBoolean(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_singleLine"), false);
        this.inputType = a.getInt(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_inputType"), 0);
        this.titleViewRid = a.getResourceId(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_titleId"), -1);
        this.editTextViewRid = a.getResourceId(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_editTextId"), -1);
        this.characterCountViewRid = a.getResourceId(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_textCountId"), -1);
        int layout = a.getResourceId(XboxApplication.Instance.getStyleableRValue("EditViewFixedLength_layoutId"), -1);
        LayoutInflater vi = (LayoutInflater) context.getSystemService("layout_inflater");
        if (layout == -1) {
            layout = XboxApplication.Instance.getLayoutRValue("edit_view_fixed_length");
        }
        vi.inflate(layout, this, true);
        setLayoutParams(new LayoutParams(-1, -2));
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.titleView = (TextView) findViewById(this.titleViewRid == -1 ? XboxApplication.Instance.getIdRValue("edit_view_fixed_length_title") : this.titleViewRid);
        this.editTextView = (EditText) findViewById(this.editTextViewRid == -1 ? XboxApplication.Instance.getIdRValue("edit_view_fixed_length_edit") : this.editTextViewRid);
        this.characterCountView = (TextView) findViewById(this.characterCountViewRid == -1 ? XboxApplication.Instance.getIdRValue("edit_view_fixed_length_character_count") : this.characterCountViewRid);
        this.editTextView.setFilters(new InputFilter[]{new LengthFilter(this.maxCharacterCount)});
        this.editTextView.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                EditViewFixedLength.this.updateCharacterCountView(s.length());
            }
        });
        this.editTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    EditViewFixedLength.this.container.setKeyboardShown();
                }
            }
        });
        this.titleView.setText(this.title);
        updateCharacterCountView(this.editTextView.length());
        this.editTextView.setSingleLine(this.singleLine);
        setFocusable(false);
        setFocusableInTouchMode(false);
        this.editTextView.setFocusable(true);
        this.editTextView.setFocusableInTouchMode(true);
        this.editTextView.setInputType(this.inputType | this.editTextView.getInputType());
    }

    public void setText(String text) {
        if (text != null) {
            this.editTextView.setText(text);
        }
    }

    public void setContainer(EditTextContainer parent) {
        if (this.container != null) {
            XLELog.Error("EditViewFixedLength", "container is set multiple times");
            return;
        }
        this.container = parent;
        this.container.addChild(this.editTextView);
    }

    public String getText() {
        if (this.editTextView == null || this.editTextView.getText() == null) {
            return null;
        }
        return this.editTextView.getText().toString();
    }

    public EditText getEditTextView() {
        return this.editTextView;
    }

    public void addTextChangedListener(TextWatcher watcher) {
        this.editTextView.addTextChangedListener(watcher);
    }

    private void updateCharacterCountView(int newLength) {
        this.characterCountView.setText(newLength + "/" + this.maxCharacterCount);
    }
}
