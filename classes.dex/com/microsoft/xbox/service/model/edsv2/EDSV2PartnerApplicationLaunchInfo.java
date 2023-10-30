package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.toolkit.JavaUtil.HexLongJSONDeserializer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class EDSV2PartnerApplicationLaunchInfo {
    private String deepLinkInfo;
    private LaunchType launchType = LaunchType.AppLaunchType;
    private long titleId;
    private JTitleType titleType = JTitleType.Application;

    public String getDeepLinkInfo() {
        return this.deepLinkInfo;
    }

    public void setDeepLinkInfo(String link) {
        this.deepLinkInfo = link;
    }

    public long getTitleId() {
        return this.titleId;
    }

    @JsonDeserialize(using = HexLongJSONDeserializer.class)
    @JsonProperty("titleIdString")
    public void setTitleId(long id) {
        this.titleId = id;
    }

    public void setLaunchType(LaunchType type) {
        this.launchType = type;
    }

    public LaunchType getLaunchType() {
        return this.launchType;
    }

    public void setTitleType(JTitleType type) {
        this.titleType = type;
    }

    public JTitleType getTitleType() {
        return this.titleType;
    }
}
