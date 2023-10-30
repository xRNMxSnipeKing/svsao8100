package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class EDSServiceManagerGetJSONWorker {
    private String data = null;
    private Ready ready = new Ready();
    private int statusCode = -1;

    private int getStatusCode() {
        this.ready.waitForReady();
        return this.statusCode;
    }

    public String getJSONData() {
        this.ready.waitForReady();
        XLELog.Diagnostic("EDSServiceManager", "getProgrammingItems rawJSON: " + this.data);
        return this.data;
    }

    public <T> T deserialize(Class<T> cls, long errorCode) throws XLEException {
        if (getStatusCode() != 0) {
            throw new XLEException(errorCode);
        }
        try {
            T rv = new ObjectMapper().readValue(getJSONData(), (Class) cls);
            if (rv != null) {
                return rv;
            }
            throw new XLEException(errorCode);
        } catch (JsonMappingException e) {
            XLELog.Error("EDSServiceManagerGetJSONWorker", "JsonMappingException error " + e.toString());
            throw new XLEException(errorCode);
        } catch (IOException e2) {
            XLELog.Error("EDSServiceManagerGetJSONWorker", "IOException error " + e2.toString());
            throw new XLEException(errorCode);
        }
    }

    public void setJSONData(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
        this.ready.setReady();
    }
}
