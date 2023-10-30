package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.TouchUtil;
import com.microsoft.xbox.xle.ui.TitleBarView;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.TitleBarViewModel;

public class TitleBarAdapter extends AdapterBase {
    private View messageButton = findViewById(R.id.title_bar_message_button);
    private TextView messageCountText = ((TextView) findViewById(R.id.title_bar_messagecount));
    private TitleBarView titleBar = ((TitleBarView) findViewById(R.id.title_bar));
    private TitleBarViewModel viewModel;

    public TitleBarAdapter(final TitleBarViewModel viewModel) {
        this.viewModel = viewModel;
        View notConnectedView = findViewById(R.id.now_playing_not_connected);
        if (notConnectedView != null) {
            notConnectedView.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    viewModel.connectToConsole();
                }
            });
        }
    }

    public void updateViewOverride() {
        this.titleBar.updateHasUnreadMessages(this.viewModel.getHasUnreadMessages());
        this.titleBar.updateConnectState(this.viewModel.getConnectionState().ordinal());
        this.messageCountText.setText(this.viewModel.getMessageCountText());
    }

    public void onPause() {
        super.onPause();
        XLEAssert.assertNotNull(this.messageButton);
        if (this.messageButton != null) {
            this.messageButton.setOnClickListener(null);
        }
    }

    public void onResume() {
        XLEAssert.assertNotNull(this.messageButton);
        XLEAssert.assertNotNull(this.viewModel);
        if (this.messageButton != null) {
            this.messageButton.setOnClickListener(TouchUtil.createOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    TitleBarAdapter.this.viewModel.navigateToMessagesList();
                }
            }));
        }
    }

    public void onSetActive() {
    }

    public void onSetInactive() {
    }
}
