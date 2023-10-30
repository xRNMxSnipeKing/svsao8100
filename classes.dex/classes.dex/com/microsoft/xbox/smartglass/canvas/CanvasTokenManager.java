package com.microsoft.xbox.smartglass.canvas;

import java.io.IOException;

public interface CanvasTokenManager {
    String getXstsToken(String str, boolean z) throws IOException;
}
