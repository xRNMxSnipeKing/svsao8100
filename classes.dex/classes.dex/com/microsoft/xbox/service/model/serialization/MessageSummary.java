package com.microsoft.xbox.service.model.serialization;

import com.microsoft.xbox.toolkit.UrlUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.core.Commit;

@Root
public class MessageSummary {
    @Element(required = false)
    public boolean CanDelete;
    @Element(required = false)
    public boolean CanSetReadFlag;
    @Element(required = false)
    public boolean HasBeenRead;
    @Element(required = false)
    public boolean HasImage;
    @Element(required = false)
    public boolean HasText;
    @Element(required = false)
    public boolean HasVoice;
    @Element(required = false)
    public boolean IsFromFriend;
    @Element
    public long MessageId;
    @Element(required = false)
    public long MessageType;
    public URI SenderGamerPicUri;
    @Element(required = false)
    public String SenderGamertag;
    @Element(required = false)
    public long SenderTitleId;
    @Element(required = false)
    @Convert(UTCDateConverter.class)
    public Date SentTime;
    @Element(required = false)
    public String Subject;
    private String displaySubject;
    @Element(name = "SenderGamerPicUrl", required = false)
    private String senderGamerPicUrl;

    @Commit
    public void postprocess() {
        this.SenderGamerPicUri = UrlUtil.getEncodedUri(this.senderGamerPicUrl);
        if (this.Subject != null && this.Subject.length() > 0) {
            this.displaySubject = this.Subject;
        } else if (this.MessageType == 12) {
            this.displaySubject = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("message_type_party_invite"));
        } else if (this.MessageType == 11) {
            this.displaySubject = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("message_type_video_chat_invite"));
        } else if (this.MessageType == 10) {
            this.displaySubject = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("message_type_voice_chat_invite"));
        } else if (this.MessageType == 9) {
            this.displaySubject = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("message_type_video_message"));
        } else {
            this.displaySubject = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("message_type_personal_message"));
        }
    }

    public String getDisplaySubject() {
        return this.displaySubject;
    }
}
