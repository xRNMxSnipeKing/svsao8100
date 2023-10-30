package com.omniture;

public class AppMeasurementBaseSE extends AppMeasurementBaseSE13 {
    protected RequestHandler getRequestHandler() {
        if (this.requestHandler == null) {
            this.requestHandler = new RequestHandlerSe();
        }
        return this.requestHandler;
    }
}
