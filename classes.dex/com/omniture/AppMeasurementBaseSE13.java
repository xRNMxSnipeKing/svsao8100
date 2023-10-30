package com.omniture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AppMeasurementBaseSE13 extends AppMeasurementBase {
    protected void finalize() throws Throwable {
        if (this.requestList != null) {
            synchronized (s.requestList) {
                s.requestList.notifyAll();
            }
        }
        super.finalize();
    }

    protected Calendar getCalendar() {
        return new GregorianCalendar();
    }

    protected void offlineRequestListRead() {
        if (this.offlineFilename != null) {
            File offlineFile;
            try {
                offlineFile = new File(s.offlineFilename);
            } catch (Exception e) {
                offlineFile = null;
            }
            if (offlineFile != null && offlineFile.exists()) {
                try {
                    BufferedReader offlineIn = new BufferedReader(new FileReader(offlineFile));
                    while (true) {
                        String request = offlineIn.readLine();
                        if (request != null) {
                            synchronized (s.requestList) {
                                s.requestList.add(request);
                            }
                        } else {
                            offlineIn.close();
                            offlineFile.delete();
                            return;
                        }
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    protected void offlineRequestListWrite() {
        if (this.offlineFilename != null) {
            File offlineFile;
            try {
                offlineFile = new File(s.offlineFilename);
            } catch (Exception e) {
                offlineFile = null;
            }
            if (offlineFile != null) {
                try {
                    BufferedWriter offlineOut = new BufferedWriter(new FileWriter(offlineFile));
                    for (int requestNum = 0; requestNum < s.requestList.size(); requestNum++) {
                        String request = (String) s.requestList.get(requestNum);
                        offlineOut.write(request, 0, request.length());
                        offlineOut.newLine();
                    }
                    offlineOut.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    protected void offlineRequestListDelete() {
        if (this.offlineFilename != null) {
            File offlineFile;
            try {
                offlineFile = new File(s.offlineFilename);
            } catch (Exception e) {
                offlineFile = null;
            }
            if (offlineFile != null && offlineFile.exists()) {
                try {
                    offlineFile.delete();
                } catch (Exception e2) {
                }
            }
        }
    }

    protected RequestHandler getRequestHandler() {
        if (this.requestHandler == null) {
            this.requestHandler = new RequestHandlerSe13();
        }
        return this.requestHandler;
    }
}
