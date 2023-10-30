package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.XboxConsoleHelpViewModel;

public class XboxConsoleHelpAdapter extends AdapterBaseNormal {
    private XLEButton cancel;
    private XLEButton tryagain;
    private XboxConsoleHelpViewModel viewmodel;
    private XLEButton xboxoneUpSell;

    public XboxConsoleHelpAdapter(XboxConsoleHelpViewModel consolehelpviewmodel) {
        this.screenBody = findViewById(R.id.console_help_body);
        this.tryagain = (XLEButton) findViewById(R.id.console_help_try_again);
        this.cancel = (XLEButton) findViewById(R.id.console_help_cancel);
        this.xboxoneUpSell = (XLEButton) findViewById(R.id.console_help_xboxone_upsell);
        this.viewmodel = consolehelpviewmodel;
        this.tryagain.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                XboxConsoleHelpAdapter.this.viewmodel.retryConnectXbox();
            }
        });
        this.cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                XboxConsoleHelpAdapter.this.viewmodel.cancelToConnectXbox();
            }
        });
    }

    public void onStart() {
        super.onStart();
        if (this.xboxoneUpSell != null) {
            this.xboxoneUpSell.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    XLEUtil.gotoXboxOneUpSell();
                }
            });
        }
    }

    public void onStop() {
        super.onStop();
        if (this.xboxoneUpSell != null) {
            this.xboxoneUpSell.setOnClickListener(null);
        }
    }

    public void updateViewOverride() {
        setBlocking(this.viewmodel.isBlockingBusy(), this.viewmodel.getBlockingStatusText());
    }
}
