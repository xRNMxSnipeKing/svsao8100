package com.microsoft.xbox.service.model.activities;

import com.microsoft.xbox.toolkit.JavaUtil.HexLongJSONDeserializer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class ActivityProviderInfo {
    private String name;
    private boolean requiresParentPurchase;
    private long titleId;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @JsonDeserialize(using = HexLongJSONDeserializer.class)
    @JsonProperty("hexTitleId")
    public void setTitleId(long value) {
        this.titleId = value;
    }

    public long getTitleId() {
        return this.titleId;
    }

    public void setRequiresParentPurchase(boolean requires) {
        this.requiresParentPurchase = requires;
    }

    public boolean getRequiresParentPurchase() {
        return this.requiresParentPurchase;
    }
}
