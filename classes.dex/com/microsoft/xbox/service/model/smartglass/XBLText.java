package com.microsoft.xbox.service.model.smartglass;

public class XBLText {
    public long selectionIndex;
    public long selectionLength;
    public String text;

    public XBLText() {
        this("", 0, 0);
    }

    public XBLText(String text, long selectionIndex, long selectionLength) {
        this.text = text;
        this.selectionIndex = selectionIndex;
        this.selectionLength = selectionLength;
    }
}
