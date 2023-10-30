package com.microsoft.xbox.service.model.edsv2;

public class EDSV2ActivityProviderPolicy {
    private boolean isDefault;
    private boolean requiresParentPurchase;
    private String title;
    private long titleId;

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public void setRequiresParentPurchase(boolean requiresParentPurchase) {
        this.requiresParentPurchase = requiresParentPurchase;
    }

    public boolean getRequiresParentPurchase() {
        return this.requiresParentPurchase;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitleId(long id) {
        this.titleId = id;
    }

    public long getTitleId() {
        return this.titleId;
    }
}
