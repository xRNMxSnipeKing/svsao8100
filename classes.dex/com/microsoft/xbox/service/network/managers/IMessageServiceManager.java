package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.MessageDetails;
import com.microsoft.xbox.service.model.serialization.MessageSummariesResponse;
import com.microsoft.xbox.service.model.serialization.SendMessageRequest;
import com.microsoft.xbox.toolkit.XLEException;

public interface IMessageServiceManager extends IServiceManager<MessageSummariesResponse> {
    boolean deleteMessage(long j, boolean z) throws XLEException;

    MessageDetails getMessageDetail(long j) throws XLEException;

    boolean sendMessage(SendMessageRequest sendMessageRequest) throws XLEException;
}
