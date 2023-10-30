package com.microsoft.xbox.service.model.sls;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {
    public String gamerTag;
    public String xuid;
}
