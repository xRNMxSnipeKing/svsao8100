package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.EditTextContainer;
import com.microsoft.xbox.toolkit.ui.EditViewFixedLength;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.ComposeMessageActivityViewModel;

public class ComposeMessageActivityAdapter extends AdapterBaseNormal {
    private boolean keyboardShown;
    private EditViewFixedLength messageEditText;
    private XLEButton recipients;
    private ComposeMessageActivityViewModel viewModel;

    public ComposeMessageActivityAdapter(ComposeMessageActivityViewModel vm) {
        this.screenBody = findViewById(R.id.composemessage_container);
        this.viewModel = vm;
        this.recipients = (XLEButton) findViewById(R.id.composemessage_recipients);
        this.messageEditText = (EditViewFixedLength) findViewById(R.id.composemessage_message);
        this.messageEditText.setContainer((EditTextContainer) findViewById(R.id.composemessage_container));
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.message_send, new OnClickListener() {
            public void onClick(View v) {
                ComposeMessageActivityAdapter.this.dismissKeyboard();
                ComposeMessageActivityAdapter.this.viewModel.sendMessage();
            }
        });
        setAppBarButtonClickListener(R.id.message_cancel, new OnClickListener() {
            public void onClick(View v) {
                ComposeMessageActivityAdapter.this.dismissKeyboard();
                ComposeMessageActivityAdapter.this.viewModel.cancelSendClick();
            }
        });
        this.messageEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (ComposeMessageActivityAdapter.this.messageEditText.getText() == null || ComposeMessageActivityAdapter.this.messageEditText.getText().length() <= 0) {
                    ComposeMessageActivityAdapter.this.setAppBarButtonEnabled(R.id.message_send, false);
                } else {
                    ComposeMessageActivityAdapter.this.setAppBarButtonEnabled(R.id.message_send, ComposeMessageActivityAdapter.this.viewModel.getIsRecipientNonEmpty());
                }
            }

            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }
        });
        this.recipients.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ComposeMessageActivityAdapter.this.dismissKeyboard();
                ComposeMessageActivityAdapter.this.viewModel.navigateToFriendsPicker();
            }
        });
        if (this.viewModel.getShouldAutoShowKeyboard()) {
            this.messageEditText.getEditTextView().requestFocus();
            showKeyboard(this.messageEditText.getEditTextView());
        }
    }

    public void onPause() {
        super.onPause();
        dismissKeyboard();
    }

    public void updateViewOverride() {
        this.recipients.setText(this.viewModel.getRecipients());
        boolean z = this.messageEditText.getText() != null && this.messageEditText.getText().length() > 0 && this.viewModel.getIsRecipientNonEmpty();
        setAppBarButtonEnabled(R.id.message_send, z);
        setBlocking(this.viewModel.isBlockingBusy(), this.viewModel.getBlockingStatusText());
    }

    public void setMessageEditText(String bodyText) {
        this.messageEditText.setText(bodyText);
    }

    public void updateMessageBody() {
        this.viewModel.setMessageBody(this.messageEditText.getText());
    }
}
