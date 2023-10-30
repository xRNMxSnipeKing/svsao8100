package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;

public class BioView extends LinearLayout {
    private TextView bioView;
    private TextView locationView;
    private TextView mottoView;
    private TextView nameView;

    public BioView(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public BioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.bio_layout, this, true);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.nameView = (TextView) findViewById(R.id.bio_name);
        this.mottoView = (TextView) findViewById(R.id.bio_motto);
        this.locationView = (TextView) findViewById(R.id.bio_location);
        this.bioView = (TextView) findViewById(R.id.bio_bio);
    }

    public void setName(String name) {
        setBioText(this.nameView, name);
    }

    public void setMotto(String motto) {
        setBioText(this.mottoView, motto);
    }

    public void setLocation(String location) {
        setBioText(this.locationView, location);
    }

    public void setBio(String bio) {
        setBioText(this.bioView, bio);
    }

    private void setBioText(TextView textView, String text) {
        if (text == null || text.length() == 0) {
            text = XLEApplication.Resources.getString(R.string.bio_not_set);
        }
        textView.setText(text);
    }
}
