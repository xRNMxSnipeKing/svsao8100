package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2PartnerApplicationLaunchInfo;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.app.XLEApplication;
import java.util.ArrayList;

public class DetailsProviderView2 extends TableLayout {
    private static final int DEFAULT_NUM_COLUMNS = XLEApplication.Resources.getInteger(R.integer.detailProvidersGridColCount);
    private OnProviderClickListener listener = null;
    private int mediaType = 0;
    private ArrayList<EDSV2Provider> providers = null;

    public interface OnProviderClickListener {
        void onProviderClick(EDSV2Provider eDSV2Provider);
    }

    public DetailsProviderView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProviders(ArrayList<EDSV2Provider> providers, int mediaType) {
        if (this.providers != providers) {
            this.providers = providers;
            this.mediaType = mediaType;
            buildProvidersUI();
        }
    }

    public void setOnProviderClickListener(OnProviderClickListener listener) {
        this.listener = listener;
    }

    private void buildProvidersUI() {
        removeAllViews();
        TableRow headerRow = new TableRow(getContext());
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.provider_header_row, headerRow, true);
        addView(headerRow, new LayoutParams(-1, -2));
        setStretchAllColumns(true);
        int numRows = (this.providers.size() / DEFAULT_NUM_COLUMNS) + (this.providers.size() % DEFAULT_NUM_COLUMNS == 0 ? 0 : 1);
        for (int row = 0; row < numRows; row++) {
            TableRow currentTableRow = new TableRow(getContext());
            for (int col = 0; col < DEFAULT_NUM_COLUMNS; col++) {
                int providerIndex = (DEFAULT_NUM_COLUMNS * row) + col;
                if (providerIndex < this.providers.size()) {
                    currentTableRow.addView(buildProviderButton2(providerIndex), new TableRow.LayoutParams(0, -2, 1.0f));
                } else {
                    currentTableRow.addView(new View(getContext()), new TableRow.LayoutParams(0, 0, 1.0f));
                }
            }
            addView(currentTableRow, new TableRow.LayoutParams(-1, -2));
        }
    }

    private View buildProviderButton2(int index) {
        final EDSV2Provider providerData = (EDSV2Provider) this.providers.get(index);
        View providerButton = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.details_providers_grid_row, null);
        XLEUniformImageView tileView = (XLEUniformImageView) providerButton.findViewById(R.id.details_provider_grid_item_image);
        if (!(tileView == null || providerData.getLaunchInfos() == null || providerData.getLaunchInfos().size() <= 0)) {
            EDSV2PartnerApplicationLaunchInfo info = (EDSV2PartnerApplicationLaunchInfo) providerData.getLaunchInfos().get(0);
            if (info != null) {
                if (info.getTitleId() == XLEConstants.ZUNE_TITLE_ID && this.mediaType != 0) {
                    int imageResourceId;
                    switch (this.mediaType) {
                        case EDSV2MediaType.MEDIATYPE_XBOXAPP /*61*/:
                            if (!providerData.getIsXboxMusic()) {
                                imageResourceId = R.drawable.launch_xbox_video;
                                break;
                            }
                            imageResourceId = R.drawable.launch_xbox_music;
                            break;
                        case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                        case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
                        case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                        case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                            imageResourceId = R.drawable.launch_xbox_music;
                            break;
                        default:
                            imageResourceId = R.drawable.launch_xbox_video;
                            break;
                    }
                    tileView.setImageURI2(null, imageResourceId);
                } else if (providerData.getImageUrl() != null) {
                    tileView.setImageURI2(providerData.getImageUrl(), -1);
                } else {
                    tileView.setImageURI2(null, R.drawable.send2xbox_2x);
                }
            }
        }
        providerButton.setSoundEffectsEnabled(false);
        providerButton.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (DetailsProviderView2.this.listener != null) {
                    DetailsProviderView2.this.listener.onProviderClick(providerData);
                }
            }
        }));
        return providerButton;
    }
}
