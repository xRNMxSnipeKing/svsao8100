package com.microsoft.xbox.service.model.serialization;

import com.microsoft.xbox.toolkit.UrlUtil;
import java.net.URI;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.core.Commit;

@Root
public class GameInfo {
    public static final int GAMETYPE_ALL = 15;
    public static final int GAMETYPE_NONE = 0;
    public static final int GAMETYPE_WEB = 2;
    public static final int GAMETYPE_WINDOSWPC = 8;
    public static final int GAMETYPE_WINDOWSPHONE = 1;
    public static final int GAMETYPE_XBOXCONSOLE = 4;
    @Element
    public int AchievementsEarned;
    @Element
    public String GameUrl;
    @Element(required = false)
    public int Gamerscore;
    @Element
    public long Id;
    public URI ImageUri;
    @Element
    private String ImageUrl;
    @Element
    @Convert(UTCDateConverter.class)
    public Date LastPlayed;
    @Element
    public String Name;
    @Element
    public int TotalAchievements;
    @Element(required = false)
    public int TotalPossibleGamerscore;
    @Element
    public int Type;

    @Commit
    public void postprocess() {
        this.ImageUri = UrlUtil.getEncodedUri(this.ImageUrl);
    }
}
