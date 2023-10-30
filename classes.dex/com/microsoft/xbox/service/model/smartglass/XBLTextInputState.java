package com.microsoft.xbox.service.model.smartglass;

public class XBLTextInputState {
    public int inputType;
    public boolean isKeyboardRunestrip;
    public int keyboardType;
    public long maxLength;
    public boolean showPassCode;
    public String supportedCharacters;

    public XBLKeyboardType getKeyboardType() {
        return XBLKeyboardType.forValue(this.keyboardType);
    }

    public XBLKeyboardInput getInputType() {
        return XBLKeyboardInput.forValue(this.inputType);
    }
}
