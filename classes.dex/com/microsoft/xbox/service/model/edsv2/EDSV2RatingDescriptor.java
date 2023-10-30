package com.microsoft.xbox.service.model.edsv2;

public class EDSV2RatingDescriptor {
    private String id;
    private String nonLocalizedDescriptor;

    public String getNonLocalizedDescriptor() {
        return this.nonLocalizedDescriptor;
    }

    public void setNonLocalizedDescriptor(String nonLocalizedDescriptor) {
        this.nonLocalizedDescriptor = nonLocalizedDescriptor;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
