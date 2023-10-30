package com.microsoft.xbox.service.model.serialization;

import com.microsoft.xbox.toolkit.UrlUtil;
import java.net.URI;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.core.Commit;

@Root
public class Achievement {
    @Element
    public String Description;
    @Element
    public boolean DisplayBeforeEarned;
    @Element(required = false)
    @Convert(UTCDateConverter.class)
    public Date EarnedDateTime;
    @Element
    public boolean EarnedOnline;
    @Element
    public long GameId;
    @Element
    public int Gamerscore;
    @Element(required = false)
    public String HowToEarn;
    @Element
    public boolean IsEarned;
    @Element
    public String Key;
    @Element
    public String Name;
    public URI PictureUri;
    @Element
    private String PictureUrl;

    @Commit
    public void postprocess() {
        this.PictureUri = UrlUtil.getEncodedUri(this.PictureUrl);
    }
}
