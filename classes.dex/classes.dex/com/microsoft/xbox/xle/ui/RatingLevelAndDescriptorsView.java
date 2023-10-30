package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2RatingDescriptor;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.UrlUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEImageViewFast;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.RatingStringsHelper;
import java.util.ArrayList;
import java.util.Iterator;

public class RatingLevelAndDescriptorsView extends LinearLayout {
    public static final String parentRatingFormat = "http://epix.xbox.com/consoleassets/vm_ems/DetailsPages/RatingIcons/%s.png";
    private LinearLayout ratingDescriptorsContainer;
    private CustomTypefaceTextView ratingDescriptorsTextView;
    private CustomTypefaceTextView ratingLevel;
    private XLEImageViewFast ratingTile;

    public RatingLevelAndDescriptorsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.rating_level_and_descriptors, this, true);
        this.ratingLevel = (CustomTypefaceTextView) findViewById(R.id.parent_rating_level_text);
        this.ratingTile = (XLEImageViewFast) findViewById(R.id.parent_rating_level_tile);
        this.ratingDescriptorsContainer = (LinearLayout) findViewById(R.id.parent_rating_descriptor_container);
        this.ratingDescriptorsTextView = (CustomTypefaceTextView) findViewById(R.id.parent_rating_descriptor_text);
    }

    public void setRatingLevelAndDescriptors(String ratingId, String defaultParentalRating, ArrayList<EDSV2RatingDescriptor> ratingdescriptors) {
        if (JavaUtil.isNullOrEmpty(ratingId)) {
            this.ratingLevel.setVisibility(8);
            this.ratingTile.setVisibility(8);
            if (this.ratingDescriptorsContainer != null) {
                this.ratingDescriptorsContainer.setVisibility(8);
            }
            if (this.ratingDescriptorsTextView != null) {
                this.ratingDescriptorsTextView.setVisibility(8);
                return;
            }
            return;
        }
        this.ratingLevel.setVisibility(0);
        this.ratingTile.setVisibility(0);
        if (this.ratingDescriptorsContainer != null) {
            this.ratingDescriptorsContainer.setVisibility(0);
            this.ratingDescriptorsContainer.removeAllViews();
        }
        if (this.ratingDescriptorsTextView != null) {
            this.ratingDescriptorsTextView.setVisibility(0);
        }
        this.ratingLevel.setText(RatingStringsHelper.getGameRatingString(ratingId, defaultParentalRating));
        this.ratingTile.setImageURI2(UrlUtil.getEncodedUri(String.format(parentRatingFormat, new Object[]{ratingId})));
        if (ratingdescriptors != null) {
            EDSV2RatingDescriptor ratingDescriptor;
            if (this.ratingDescriptorsTextView != null) {
                String[] descriptorsAsStringArray = new String[ratingdescriptors.size()];
                for (int i = 0; i < ratingdescriptors.size(); i++) {
                    ratingDescriptor = (EDSV2RatingDescriptor) ratingdescriptors.get(i);
                    descriptorsAsStringArray[i] = RatingStringsHelper.getGameRatingDescriptor(ratingDescriptor.getId(), ratingDescriptor.getNonLocalizedDescriptor());
                }
                this.ratingDescriptorsTextView.setText(JavaUtil.concatenateStringsWithDelimiter(XboxApplication.Resources.getString(R.string.comma_delimiter), false, descriptorsAsStringArray));
            }
            if (this.ratingDescriptorsContainer != null) {
                Iterator i$ = ratingdescriptors.iterator();
                while (i$.hasNext()) {
                    ratingDescriptor = (EDSV2RatingDescriptor) i$.next();
                    CustomTypefaceTextView textView = new CustomTypefaceTextView(XLEApplication.MainActivity, "fonts/SegoeWP-Semilight.ttf");
                    textView.setText(RatingStringsHelper.getGameRatingDescriptor(ratingDescriptor.getId(), ratingDescriptor.getNonLocalizedDescriptor()));
                    this.ratingDescriptorsContainer.addView(textView);
                }
            }
        }
    }
}
