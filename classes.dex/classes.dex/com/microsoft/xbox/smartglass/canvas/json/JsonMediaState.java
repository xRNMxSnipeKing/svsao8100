package com.microsoft.xbox.smartglass.canvas.json;

import com.microsoft.xbox.service.model.MediaTitleState;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonMediaState extends JSONObject {
    public JsonMediaState() throws JSONException {
        this(new MediaTitleState());
    }

    public JsonMediaState(MediaTitleState mediaState) throws JSONException {
        put("duration", mediaState.getDuration());
        put("position", mediaState.getPosition());
        put("minSeekPos", mediaState.getMinSeek());
        put("maxSeekPos", mediaState.getMaxSeek());
        put("rate", (double) mediaState.getRate());
        put("state", mediaState.getTransportState());
        put("capabilities", mediaState.getTransportCapabilities());
        put("assetId", mediaState.getMediaAssetId());
    }
}
