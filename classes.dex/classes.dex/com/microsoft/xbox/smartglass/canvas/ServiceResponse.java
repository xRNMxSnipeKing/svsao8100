package com.microsoft.xbox.smartglass.canvas;

import java.util.List;
import java.util.Map;

public class ServiceResponse {
    public final Map<String, List<String>> headers;
    public final int requestId;
    public final String response;

    public ServiceResponse(int requestId, String serviceResponse, Map<String, List<String>> responseHeaders) {
        this.requestId = requestId;
        this.response = serviceResponse;
        this.headers = responseHeaders;
    }
}
