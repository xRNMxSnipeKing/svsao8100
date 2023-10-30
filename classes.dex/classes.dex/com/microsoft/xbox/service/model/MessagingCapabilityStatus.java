package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.XLEAssert;

public class MessagingCapabilityStatus {
    private boolean canSendMessage;
    private int errorResourceId;

    public MessagingCapabilityStatus(boolean canSend, int errorId) {
        this.canSendMessage = canSend;
        this.errorResourceId = errorId;
    }

    public boolean getCanSendMessage() {
        return this.canSendMessage;
    }

    public int getErrorResourceId() {
        XLEAssert.assertTrue(!this.canSendMessage);
        return this.errorResourceId;
    }
}
