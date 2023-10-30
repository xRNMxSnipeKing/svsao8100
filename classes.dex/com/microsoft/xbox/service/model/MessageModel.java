package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.MessageDetails;
import com.microsoft.xbox.service.model.serialization.MessageSummariesResponse;
import com.microsoft.xbox.service.model.serialization.MessageSummary;
import com.microsoft.xbox.service.model.serialization.SendMessageRequest;
import com.microsoft.xbox.service.network.managers.IMessageServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MessageModel extends ModelBase<MessageSummariesResponse> {
    private static final int MAX_MESSAGE_DETAIL_MODELS = 10;
    private FixedSizeHashtable<Long, MessageDetails> detailsCache;
    private GetMessageListRunner getListRunner;
    private boolean isDeleting;
    private boolean isLoadingMessageDetails;
    private boolean isSending;
    private long lastRequestedMessageIdForDelete;
    private long lastRequestedMessageIdForDetail;
    private ArrayList<MessageSummary> messageList;
    private final int notFound;
    private IMessageServiceManager serviceManager;
    private int unreadMessageCount;

    private static class MessageModelHolder {
        private static MessageModel instance = new MessageModel();

        private MessageModelHolder() {
        }

        private static void reset() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            instance = new MessageModel();
        }
    }

    private class DeleteMessageRunner extends IDataLoaderRunnable<Boolean> {
        private MessageModel caller;
        private long messageId;
        private boolean shouldBlockSender;

        public DeleteMessageRunner(MessageModel caller, long messageId, boolean shouldBlockSender) {
            this.caller = caller;
            this.messageId = messageId;
            this.shouldBlockSender = shouldBlockSender;
        }

        public Boolean buildData() throws XLEException {
            return new Boolean(this.caller.serviceManager.deleteMessage(this.messageId, this.shouldBlockSender));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onDeleteMessageCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_DELETE_MESSAGE;
        }
    }

    private class GetMessageListRunner extends IDataLoaderRunnable<MessageSummariesResponse> {
        private MessageModel caller;

        public GetMessageListRunner(MessageModel caller) {
            this.caller = caller;
        }

        public MessageSummariesResponse buildData() throws XLEException {
            return (MessageSummariesResponse) this.caller.serviceManager.getData();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<MessageSummariesResponse> result) {
            this.caller.onGetMessageListCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MESSAGE_SUMMARY;
        }
    }

    private class GetOneMessageRunner extends IDataLoaderRunnable<MessageDetails> {
        private MessageModel caller;
        private long messageId;

        public GetOneMessageRunner(MessageModel caller, long id) {
            this.caller = caller;
            this.messageId = id;
        }

        public MessageDetails buildData() throws XLEException {
            return this.caller.serviceManager.getMessageDetail(this.messageId);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<MessageDetails> result) {
            this.caller.onGetMessageDetailsCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MESSAGE_DETAIL;
        }

        public Object getUserObject() {
            return Long.valueOf(this.messageId);
        }
    }

    private class SendMessageRunner extends IDataLoaderRunnable<Boolean> {
        private MessageModel caller;
        private SendMessageRequest request;

        public SendMessageRunner(MessageModel caller, SendMessageRequest request) {
            this.caller = caller;
            this.request = request;
        }

        public Boolean buildData() throws XLEException {
            return new Boolean(this.caller.serviceManager.sendMessage(this.request));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onSendMessageCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SEND_MESSAGE;
        }
    }

    private MessageModel() {
        this.notFound = -1;
        this.detailsCache = new FixedSizeHashtable(10);
        this.messageList = null;
        this.unreadMessageCount = 0;
        this.isLoadingMessageDetails = false;
        this.isLoading = false;
        this.lifetime = 3600000;
        this.serviceManager = ServiceManagerFactory.getInstance().getMessageServiceManager();
        this.getListRunner = new GetMessageListRunner(this);
    }

    public static MessageModel getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return MessageModelHolder.instance;
    }

    public static void reset() {
        getInstance().clearObserver();
        MessageModelHolder.reset();
    }

    public ArrayList<MessageSummary> getMessageList() {
        return this.messageList;
    }

    public int getUnReadMessageCount() {
        return this.unreadMessageCount;
    }

    public boolean getIsLoadingMessageList() {
        return this.isLoading;
    }

    public boolean getIsLoadingMessageDetails() {
        return this.isLoadingMessageDetails;
    }

    public boolean getIsDeleting() {
        return this.isDeleting;
    }

    public boolean getIsSending() {
        return this.isSending;
    }

    public MessageDetails getMessageDetails(long messageId) {
        return (MessageDetails) this.detailsCache.get(Long.valueOf(messageId));
    }

    public void loadMessageList(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        loadInternal(forceRefresh, UpdateType.MessageData, this.getListRunner);
    }

    public void loadMessageDetails(long messageId) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (this.messageList != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getMessageDetails(messageId) == null) {
            if (this.isLoadingMessageDetails) {
                if (this.lastRequestedMessageIdForDetail != messageId) {
                    XLELog.Warning("MessageModel", "loading a new message id " + Long.toString(messageId));
                } else {
                    return;
                }
            }
            this.lastRequestedMessageIdForDetail = messageId;
            this.isLoadingMessageDetails = true;
            new DataLoaderTask(new GetOneMessageRunner(this, messageId)).execute();
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MessageDetailsData, false), this, null));
            return;
        }
        this.isLoadingMessageDetails = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MessageDetailsData, true), this, null));
    }

    public void deleteMessage(long messageId, boolean blockingSender) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (this.messageList != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!this.isDeleting) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.isDeleting = true;
        this.lastRequestedMessageIdForDelete = messageId;
        new DataLoaderTask(new DeleteMessageRunner(this, messageId, blockingSender)).execute();
    }

    public void sendMessage(SendMessageRequest sendRequest) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!this.isSending) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.isSending = true;
        new DataLoaderTask(new SendMessageRunner(this, sendRequest)).execute();
    }

    private int findSummaryById(long messageId) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        int response = 0;
        if (this.messageList != null) {
            Iterator i$ = this.messageList.iterator();
            while (i$.hasNext()) {
                if (((MessageSummary) i$.next()).MessageId == messageId) {
                    return response;
                }
                response++;
            }
        }
        return -1;
    }

    private void onGetMessageListCompleted(AsyncResult<MessageSummariesResponse> asyncResult) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (asyncResult.getException() == null && asyncResult.getResult() != null) {
            int unreadCount = 0;
            ArrayList<MessageSummary> newList = new ArrayList();
            for (MessageSummary summary : ((MessageSummariesResponse) asyncResult.getResult()).Summaries) {
                if (summary.MessageType != 2) {
                    newList.add(summary);
                    if (!summary.HasBeenRead) {
                        unreadCount++;
                    }
                }
            }
            this.messageList = newList;
            this.detailsCache = new FixedSizeHashtable(10);
            this.unreadMessageCount = unreadCount;
            this.lastRefreshTime = new Date();
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MessageData, true), this, asyncResult.getException()));
    }

    private void onGetMessageDetailsCompleted(AsyncResult<MessageDetails> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null && result.getResult() != null) {
            MessageDetails details = (MessageDetails) result.getResult();
            int summaryIndex = findSummaryById(details.MessageId);
            this.detailsCache.put(Long.valueOf(details.MessageId), details);
            if (this.lastRequestedMessageIdForDetail != details.MessageId) {
                XLELog.Warning("MessageModel", "The busy indicator is reset for incorrect id");
            }
            if (summaryIndex <= -1) {
                XLELog.Error("MessageModel", "message doesn't exist in summary list ! " + details.MessageId);
            } else {
                MessageSummary summary = (MessageSummary) this.messageList.get(summaryIndex);
                if (summary.CanSetReadFlag && !summary.HasBeenRead) {
                    boolean z;
                    summary.HasBeenRead = true;
                    if (this.unreadMessageCount > 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    XLEAssert.assertTrue(z);
                    this.unreadMessageCount--;
                }
            }
        }
        this.isLoadingMessageDetails = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MessageDetailsData, true), this, result.getException()));
    }

    private void onDeleteMessageCompleted(AsyncResult<Boolean> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            int summaryIndex = findSummaryById(this.lastRequestedMessageIdForDelete);
            if (summaryIndex <= -1) {
                XLELog.Error("MessageModel", "the message id doesn't exist in the summary list " + this.lastRequestedMessageIdForDelete);
            } else {
                this.messageList.remove(summaryIndex);
            }
            if (((MessageDetails) this.detailsCache.get(Long.valueOf(this.lastRequestedMessageIdForDelete))) != null) {
                this.detailsCache.remove(Long.valueOf(this.lastRequestedMessageIdForDelete));
            } else {
                XLELog.Error("MessageModel", "The requested id doesn't exist in detail cache");
            }
        }
        this.isDeleting = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MessageDelete, true), this, result.getException()));
    }

    private void onSendMessageCompleted(AsyncResult<Boolean> result) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.isSending = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MessageSend, true), this, result.getException()));
    }
}
