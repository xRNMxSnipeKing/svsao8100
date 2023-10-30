package com.microsoft.xbox.service.network.managers;

import android.text.TextUtils;
import com.microsoft.xbox.service.model.serialization.MessageDetails;
import com.microsoft.xbox.service.model.serialization.MessageSummariesResponse;
import com.microsoft.xbox.service.model.serialization.SendMessageRequest;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class MessageServiceManager implements IMessageServiceManager {
    private static final String RecipientsTemplate = "<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">%s</string>";
    private static final String SendRequestTemplate = "<SendMessageRequest xmlns=\"http://schemas.datacontract.org/2004/07/GDS.Contracts\"><Recipients>%s</Recipients><MessageText>%s</MessageText></SendMessageRequest>";

    private String getUrlForMessageDetails(long messageId) {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.MESSAGE_DETAIL_API_PATH + String.format(XboxLiveEnvironment.MESSAGE_QUERY_PARAMS, new Object[]{Long.valueOf(messageId)});
    }

    private String getUrlForMessageSummary() {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.MESSAGE_SUMMARY_API_PATH;
    }

    private String getUrlForSendMessage() {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.MESSAGE_SEND_API_PATH;
    }

    private String getUrlForDeleteMessage(long messageId) {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.MESSAGE_DELETE_API_PATH + String.format(XboxLiveEnvironment.MESSAGE_QUERY_PARAMS, new Object[]{Long.valueOf(messageId)});
    }

    private String getUrlForBlockSender(long messageId) {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.MESSAGE_BLOCK_API_PATH + String.format(XboxLiveEnvironment.MESSAGE_QUERY_PARAMS, new Object[]{Long.valueOf(messageId)});
    }

    public MessageSummariesResponse getData() throws XLEException {
        String url = getUrlForMessageSummary();
        XLELog.Info("MessageServiceManager", "getting message summary for " + url);
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url);
        if (stream != null) {
            return (MessageSummariesResponse) XMLHelper.instance().load(stream, MessageSummariesResponse.class);
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_MESSAGE_SUMMARY);
    }

    public MessageDetails getMessageDetail(long messageId) throws XLEException {
        String url = getUrlForMessageDetails(messageId);
        XLELog.Info("MessageServiceManager", "getting message details for " + url);
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url);
        if (stream != null) {
            return (MessageDetails) XMLHelper.instance().load(stream, MessageDetails.class);
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_MESSAGE_DETAIL, null, null, new Long(messageId));
    }

    public boolean deleteMessage(long messageId, boolean blockUser) throws XLEException {
        String url;
        if (blockUser) {
            url = getUrlForBlockSender(messageId);
        } else {
            url = getUrlForDeleteMessage(messageId);
        }
        XLELog.Info("MessageServiceManaer", "deleting/blocking using url " + url);
        if (ServiceCommon.deleteLiven(XboxLiveEnvironment.UDS_AUDIENCE_URI, url)) {
            return true;
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_DELETE_MESSAGE);
    }

    public boolean sendMessage(SendMessageRequest sendRequest) throws XLEException {
        String url = getUrlForSendMessage();
        StringBuilder builder = new StringBuilder();
        for (String recipient : sendRequest.Recipients) {
            builder.append(String.format(RecipientsTemplate, new Object[]{recipient}));
        }
        sendRequest.MessageText = TextUtils.htmlEncode(sendRequest.MessageText);
        String payload = String.format(SendRequestTemplate, new Object[]{builder.toString(), sendRequest.MessageText});
        XLELog.Info("MessageServiceManager", "sending message using url " + url);
        if (ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url, payload)) {
            return true;
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_SEND_MESSAGE);
    }
}
