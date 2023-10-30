package com.omniture;

import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class AppMeasurementBase extends AppMeasurement_Variables {
    private static Random randomNumGen = new Random();
    protected int _1_referrer;
    public String imageDimensions;
    public int maxRequestThreads;
    public int maxRequestsPerThread;
    public boolean offline;
    protected RequestHandler requestHandler;
    protected Vector requestList;
    protected int requestThreadID;
    protected Hashtable requestThreads;
    public boolean sendFromServer;
    protected String target;
    public boolean usePlugins;
    protected String version;

    protected boolean _hasDoPlugins() {
        return false;
    }

    protected void _doPlugins() {
    }

    protected boolean _hasDoRequest() {
        return false;
    }

    protected boolean _doRequest(String url, Hashtable headers) {
        return true;
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    protected RequestHandler getRequestHandler() {
        if (this.requestHandler == null) {
            this.requestHandler = new DefaultRequestHandler();
        }
        return this.requestHandler;
    }

    public void forceOffline() {
        this.offline = true;
        if (this.requestList != null) {
            synchronized (s.requestList) {
                s.requestList.notifyAll();
            }
        }
    }

    public void forceOnline() {
        this.offline = false;
        if (this.requestList != null) {
            synchronized (s.requestList) {
                s.requestList.notifyAll();
            }
        }
    }

    public AppMeasurementBase() {
        this.target = "";
        this.usePlugins = false;
        this.requestList = null;
        this.requestThreads = null;
        this.requestThreadID = 0;
        this.maxRequestThreads = 1;
        this.maxRequestsPerThread = 50;
        this.offline = false;
        this.sendFromServer = false;
        this._1_referrer = 0;
        this.requestHandler = null;
        this.version = "JAVA-1.2.4";
        this.ssl = false;
        this.linkLeaveQueryString = false;
        this.debugTracking = false;
        this.charSet = "UTF-8";
        this.sendFromServer = true;
        this.contextData = new Hashtable();
        this.retrieveLightData = new Hashtable();
    }

    protected void modulesUpdate() {
    }

    public void logDebug(String msg) {
        System.out.println(msg);
    }

    protected boolean isString(Object value) {
        try {
            return ((String) value) != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected String toString(Object value) {
        return (String) value;
    }

    protected boolean isInteger(Object value) {
        try {
            return ((Integer) value) != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected int toInteger(Object value) {
        return ((Integer) value).intValue();
    }

    protected boolean isBoolean(Object value) {
        try {
            return ((Boolean) value) != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean toBoolean(Object value) {
        return ((Boolean) value).booleanValue();
    }

    public boolean isSet(boolean val) {
        return val;
    }

    public boolean isSet(int val) {
        if (val == 0) {
            return false;
        }
        return true;
    }

    public boolean isSet(float val) {
        if (((double) val) == 0.0d) {
            return false;
        }
        return true;
    }

    public boolean isSet(double val) {
        if (val == 0.0d) {
            return false;
        }
        return true;
    }

    public boolean isSet(String val) {
        if (val == null || val.length() == 0) {
            return false;
        }
        return true;
    }

    public boolean isSet(Object val) {
        if (val == null) {
            return false;
        }
        if (isString(val)) {
            return isSet(toString(val));
        }
        if (isInteger(val)) {
            return isSet(toInteger(val));
        }
        if (isBoolean(val)) {
            return isSet(toBoolean(val));
        }
        return true;
    }

    public boolean isNumber(int num) {
        return true;
    }

    protected boolean isNumber(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String[] splitString(String delim, String str) {
        int delimPos = 0;
        int partNum = 1;
        while (true) {
            delimPos = str.indexOf(delim, delimPos);
            if (delimPos < 0) {
                break;
            }
            partNum++;
            delimPos += delim.length();
        }
        String[] partList = new String[partNum];
        if (partNum == 1) {
            partList[0] = str;
        } else {
            partNum = 0;
            while (true) {
                delimPos = str.indexOf(delim);
                if (delimPos < 0) {
                    break;
                }
                partList[partNum] = str.substring(0, delimPos);
                str = str.substring(delim.length() + delimPos);
                partNum++;
            }
            partList[partNum] = str;
        }
        return partList;
    }

    public String joinArray(String delim, String[] partList) {
        String str = "";
        boolean isFirstValue = true;
        for (String str2 : partList) {
            if (!isFirstValue) {
                str = str + delim;
            }
            str = str + str2;
            isFirstValue = false;
        }
        return str;
    }

    public String replace(String x, String o, String n) {
        String y = x;
        if (!isSet(y) || y.indexOf(o) < 0) {
            return y;
        }
        return joinArray(n, splitString(o, y));
    }

    public static String escape(String str) {
        if (str == null) {
            return null;
        }
        try {
            String str2 = new String(str.getBytes("UTF-8"), "ISO-8859-1");
            try {
                String newStr = "";
                for (int chNum = 0; chNum < str2.length(); chNum++) {
                    char ch = str2.charAt(chNum);
                    if ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ((ch < '0' || ch > '9') && ".-*_".indexOf(ch) <= -1))) {
                        String chHex = Integer.toString(ch, 16).toUpperCase();
                        if (chHex.length() == 1) {
                            chHex = '0' + chHex;
                        }
                        newStr = newStr + '%' + chHex;
                    } else {
                        newStr = newStr + ch;
                    }
                }
                return newStr;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e2) {
            return null;
        }
    }

    public static String unescape(String str) {
        if (str == null) {
            return null;
        }
        if (str.indexOf(37) < 0) {
            return str;
        }
        try {
            int chNum = 0;
            String newStr = "";
            while (chNum < str.length()) {
                char ch = str.charAt(chNum);
                if (ch == '%') {
                    String chHex = str.substring(chNum + 1, chNum + 3);
                    chNum += 3;
                    newStr = newStr + ((char) Integer.parseInt(chHex, 16));
                } else {
                    StringBuilder append = new StringBuilder().append(newStr);
                    if (ch == '+') {
                        ch = ' ';
                    }
                    chNum++;
                    newStr = append.append(ch).toString();
                }
            }
            return new String(newStr.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    protected Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public double getTime() {
        return (double) getCalendar().getTime().getTime();
    }

    protected boolean requestRequest(String request) {
        logDebug(request);
        return true;
    }

    protected void offlineRequestListRead() {
    }

    protected void offlineRequestListWrite() {
    }

    protected void offlineRequestListDelete() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void handleRequestList() {
        /*
        r19 = this;
        r13 = r19;
        r7 = "";
        r8 = 0;
    L_0x0006:
        r12 = 0;
        r0 = r13.requestList;
        r16 = r0;
        monitor-enter(r16);
    L_0x000c:
        r15 = r13.requestList;	 Catch:{ all -> 0x0035 }
        r15 = r15.isEmpty();	 Catch:{ all -> 0x0035 }
        if (r15 == 0) goto L_0x0038;
    L_0x0014:
        r15 = r13.requestThreads;	 Catch:{ Exception -> 0x0118 }
        r15 = r15.size();	 Catch:{ Exception -> 0x0118 }
        r17 = 1;
        r0 = r17;
        if (r15 <= r0) goto L_0x0022;
    L_0x0020:
        monitor-exit(r16);	 Catch:{ all -> 0x0035 }
    L_0x0021:
        return;
    L_0x0022:
        r15 = r13.requestList;	 Catch:{ Exception -> 0x0118 }
        r17 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r17;
        r15.wait(r0);	 Catch:{ Exception -> 0x0118 }
    L_0x002b:
        r15 = r13.requestList;	 Catch:{ all -> 0x0035 }
        r15 = r15.isEmpty();	 Catch:{ all -> 0x0035 }
        if (r15 == 0) goto L_0x000c;
    L_0x0033:
        monitor-exit(r16);	 Catch:{ all -> 0x0035 }
        goto L_0x0021;
    L_0x0035:
        r15 = move-exception;
        monitor-exit(r16);	 Catch:{ all -> 0x0035 }
        throw r15;
    L_0x0038:
        r15 = r13.requestList;	 Catch:{ all -> 0x0035 }
        r17 = 0;
        r0 = r17;
        r10 = r15.elementAt(r0);	 Catch:{ all -> 0x0035 }
        r10 = (java.lang.String) r10;	 Catch:{ all -> 0x0035 }
        r15 = r13.requestList;	 Catch:{ all -> 0x0035 }
        r17 = 0;
        r0 = r17;
        r15.removeElementAt(r0);	 Catch:{ all -> 0x0035 }
        monitor-exit(r16);	 Catch:{ all -> 0x0035 }
        r15 = r13.trackOffline;
        if (r15 == 0) goto L_0x0056;
    L_0x0052:
        r15 = r13.offline;
        if (r15 != 0) goto L_0x00a3;
    L_0x0056:
        r15 = r13.trackOffline;
        if (r15 == 0) goto L_0x007c;
    L_0x005a:
        r15 = 0;
        r15 = (r8 > r15 ? 1 : (r8 == r15 ? 0 : -1));
        if (r15 <= 0) goto L_0x007c;
    L_0x0060:
        r15 = r13.offlineThrottleDelay;
        if (r15 <= 0) goto L_0x007c;
    L_0x0064:
        r2 = r13.getTime();
        r4 = r2 - r8;
        r15 = r13.offlineThrottleDelay;
        r15 = (double) r15;
        r15 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1));
        if (r15 >= 0) goto L_0x007c;
    L_0x0071:
        java.lang.Thread.currentThread();	 Catch:{ Exception -> 0x0115 }
        r15 = r13.offlineThrottleDelay;	 Catch:{ Exception -> 0x0115 }
        r15 = (double) r15;	 Catch:{ Exception -> 0x0115 }
        r15 = r15 - r4;
        r15 = (long) r15;	 Catch:{ Exception -> 0x0115 }
        java.lang.Thread.sleep(r15);	 Catch:{ Exception -> 0x0115 }
    L_0x007c:
        r11 = com.omniture.RequestProperties.parseRequestProperties(r10);
        r14 = r11.getUrl();
        r6 = r11.getHeaders();
        r15 = r13._hasDoRequest();
        if (r15 == 0) goto L_0x00ff;
    L_0x008e:
        r15 = r13._doRequest(r14, r6);
        if (r15 == 0) goto L_0x00ff;
    L_0x0094:
        r12 = 1;
    L_0x0095:
        if (r12 == 0) goto L_0x00a3;
    L_0x0097:
        r15 = r13.requestList;
        r15 = r15.size();
        if (r15 <= 0) goto L_0x010b;
    L_0x009f:
        r8 = r13.getTime();
    L_0x00a3:
        if (r12 != 0) goto L_0x010e;
    L_0x00a5:
        r15 = r13.trackOffline;
        if (r15 == 0) goto L_0x0006;
    L_0x00a9:
        r0 = r13.requestList;
        r16 = r0;
        monitor-enter(r16);
        r15 = r13.requestList;	 Catch:{ all -> 0x00fc }
        r17 = 0;
        r0 = r17;
        r15.insertElementAt(r10, r0);	 Catch:{ all -> 0x00fc }
        r15 = r13.requestList;	 Catch:{ all -> 0x00fc }
        r15 = r15.isEmpty();	 Catch:{ all -> 0x00fc }
        if (r15 != 0) goto L_0x00f0;
    L_0x00bf:
        r15 = r13.requestList;	 Catch:{ all -> 0x00fc }
        r0 = r13.requestList;	 Catch:{ all -> 0x00fc }
        r17 = r0;
        r17 = r17.size();	 Catch:{ all -> 0x00fc }
        r17 = r17 + -1;
        r0 = r17;
        r15 = r15.elementAt(r0);	 Catch:{ all -> 0x00fc }
        r15 = r15.equals(r7);	 Catch:{ all -> 0x00fc }
        if (r15 != 0) goto L_0x00f0;
    L_0x00d7:
        r15 = r13.requestList;	 Catch:{ all -> 0x00fc }
        r0 = r13.requestList;	 Catch:{ all -> 0x00fc }
        r17 = r0;
        r17 = r17.size();	 Catch:{ all -> 0x00fc }
        r17 = r17 + -1;
        r0 = r17;
        r15 = r15.elementAt(r0);	 Catch:{ all -> 0x00fc }
        r0 = r15;
        r0 = (java.lang.String) r0;	 Catch:{ all -> 0x00fc }
        r7 = r0;
        r13.offlineRequestListWrite();	 Catch:{ all -> 0x00fc }
    L_0x00f0:
        r15 = r13.requestList;	 Catch:{ Exception -> 0x0113 }
        r17 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0 = r17;
        r15.wait(r0);	 Catch:{ Exception -> 0x0113 }
    L_0x00f9:
        monitor-exit(r16);	 Catch:{ all -> 0x00fc }
        goto L_0x0006;
    L_0x00fc:
        r15 = move-exception;
        monitor-exit(r16);	 Catch:{ all -> 0x00fc }
        throw r15;
    L_0x00ff:
        r15 = r19.getRequestHandler();
        r15 = r15.sendRequest(r14, r6);
        if (r15 == 0) goto L_0x0095;
    L_0x0109:
        r12 = 1;
        goto L_0x0095;
    L_0x010b:
        r8 = 0;
        goto L_0x00a3;
    L_0x010e:
        r13.offlineRequestListDelete();
        goto L_0x0006;
    L_0x0113:
        r15 = move-exception;
        goto L_0x00f9;
    L_0x0115:
        r15 = move-exception;
        goto L_0x007c;
    L_0x0118:
        r15 = move-exception;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.omniture.AppMeasurementBase.handleRequestList():void");
    }

    protected void requestThreadStart() {
        final AppMeasurementBase s = this;
        if (!isSet(s.maxRequestThreads)) {
            s.maxRequestThreads = 1;
        }
        if (s.requestThreads == null) {
            s.requestThreads = new Hashtable();
        }
        int threadsNeeded = ((int) Math.ceil((double) (s.requestList.size() / s.maxRequestsPerThread))) + 1;
        synchronized (s.requestThreads) {
            while (s.requestThreads.size() < threadsNeeded && s.requestThreads.size() < s.maxRequestThreads) {
                if (s.requestThreads.size() <= 0) {
                    s.requestThreadID = 0;
                }
                final int threadID = s.requestThreadID;
                Thread requestThread = new Thread() {
                    public AppMeasurementBase _s = s;
                    private int _threadID = threadID;

                    public void run() {
                        AppMeasurementBase s = this._s;
                        s.handleRequestList();
                        synchronized (s.requestThreads) {
                            s.requestThreads.remove(new Integer(this._threadID));
                        }
                    }
                };
                s.requestThreads.put(new Integer(s.requestThreadID), requestThread);
                requestThread.start();
                s.requestThreadID++;
            }
        }
    }

    protected void enqueueRequest(String request) {
        if (this.requestList == null) {
            s.requestList = new Vector();
            offlineRequestListRead();
        }
        requestThreadStart();
        synchronized (s.requestList) {
            if (isSet(s.trackOffline)) {
                if (!isSet(s.offlineLimit)) {
                    s.offlineLimit = 10;
                }
                if (s.requestList.size() >= s.offlineLimit) {
                    s.requestList.removeElementAt(0);
                }
            }
            s.requestList.addElement(request);
            s.requestList.notifyAll();
        }
        if (isSet(s.debugTracking)) {
            String debug = "AppMeasurement Debug: ";
            String[] requestParts = splitString("\t", request);
            if (requestParts.length > 0 && requestParts[0].length() > 0) {
                String url = requestParts[0];
                debug = debug + url;
                for (String unescape : splitString("&", splitString("\t", url)[0])) {
                    debug = debug + "\n\t" + unescape(unescape);
                }
                int requestPartNum = 1;
                while (requestPartNum < requestParts.length) {
                    String key = requestParts[requestPartNum];
                    if (!(key == null || key == "" || requestPartNum >= requestParts.length - 1)) {
                        String value = requestParts[requestPartNum + 1];
                        if (!(value == null || value == "")) {
                            debug = debug + "\n\t" + key + ": " + value;
                        }
                    }
                    requestPartNum += 2;
                }
                logDebug(debug);
            }
        }
    }

    protected String makeRequest(String cacheBusting, String queryString) {
        String request;
        String trackingServer = this.trackingServer;
        String dc = this.dc;
        String prefix = this.visitorNamespace;
        if (!isSet(trackingServer)) {
            if (!isSet(prefix)) {
                prefix = s.account;
                int firstComma = prefix.indexOf(",");
                if (firstComma != -1) {
                    prefix = prefix.substring(0, firstComma);
                }
                prefix = replace(replace(prefix, "_", "-"), ".", "-");
            }
            if (isSet(dc)) {
                dc = dc.toLowerCase();
                if (dc.equals("dc2") || dc.equals("122")) {
                    dc = "122";
                } else {
                    dc = "112";
                }
            } else {
                dc = "112";
            }
            trackingServer = prefix + "." + dc + ".2o7.net";
        } else if (isSet(s.ssl) && isSet(s.trackingServerSecure)) {
            trackingServer = s.trackingServerSecure;
        }
        if (isSet(s.ssl)) {
            request = "https://";
        } else {
            request = "http://";
        }
        request = request + trackingServer + "/b/ss/" + s.account + "/" + (s.mobile ? "5." : "") + (s.sendFromServer ? "0" : "1") + "/" + s.version + (isSet(s.target) ? "-" + s.target : "") + "/" + cacheBusting + "?AQB=1&ndh=1&" + queryString + "&AQE=1";
        if (!s.sendFromServer) {
            return "<img src=\"" + request + "\" width=\"1\" height=\"1\" border=\"0\" alt=\"\" />";
        }
        if (isSet(s.userAgent)) {
            request = request + "\tUser-Agent\t" + replace(replace(replace(s.userAgent, "\t", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR), "\n", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR), "\r", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
        }
        if (isSet(s.acceptLanguage)) {
            request = request + "\tAccept-Language\t" + replace(replace(replace(s.acceptLanguage, "\t", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR), "\n", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR), "\r", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
        }
        enqueueRequest(request);
        return "";
    }

    protected void handleLinkTracking() {
        String linkType = this.linkType;
        String linkURL = this.linkURL;
        String linkName = this.linkName;
        if (!isSet(linkType)) {
            return;
        }
        if (isSet(linkURL) || isSet(linkName)) {
            linkType = linkType.toLowerCase();
            if (!(linkType.equals("d") || linkType.equals("e"))) {
                linkType = "o";
            }
            if (isSet(linkURL) && !s.linkLeaveQueryString) {
                int queryStringStart = linkURL.indexOf("?");
                if (queryStringStart != -1) {
                    linkURL = linkURL.substring(0, queryStringStart);
                }
            }
            s.pe = "lnk_" + escape(linkType);
            s.pev1 = escape(linkURL);
            s.pev2 = escape(linkName);
        }
    }

    protected void handleTechnology() {
    }

    private String serializeToQueryString(String varKey, Hashtable varValue, String varFilter, String varFilterPrefix, String filter) {
        AppMeasurementBase s = this;
        String queryString = "";
        Vector nestedFilterList = null;
        if (varKey.equals("contextData")) {
            varKey = "c";
        }
        if (varValue == null) {
            return queryString;
        }
        Enumeration keys = varValue.keys();
        while (keys.hasMoreElements()) {
            String subVarKey = (String) keys.nextElement();
            Object subVarValue = varValue.get(subVarKey);
            if ((filter == null || (subVarKey.length() >= filter.length() && subVarKey.substring(0, filter.length()).equals(filter))) && isSet(subVarValue)) {
                if (isSet(varFilter)) {
                    if (varFilter.indexOf("," + (isSet(varFilterPrefix) ? varFilterPrefix + "." : "") + subVarKey + ",") < 0) {
                    }
                }
                boolean nestedFilterMatch = false;
                if (nestedFilterList != null) {
                    int nestedFilterNum = 0;
                    while (nestedFilterNum < nestedFilterList.size()) {
                        if (subVarKey.length() >= ((String) nestedFilterList.elementAt(nestedFilterNum)).length() && subVarKey.substring(0, ((String) nestedFilterList.elementAt(nestedFilterNum)).length()).equals((String) nestedFilterList.elementAt(nestedFilterNum))) {
                            nestedFilterMatch = true;
                        }
                        nestedFilterNum++;
                    }
                }
                if (!nestedFilterMatch) {
                    if (queryString.equals("")) {
                        queryString = queryString + "&" + varKey + ".";
                    }
                    if (filter != null) {
                        subVarKey = subVarKey.substring(filter.length());
                    }
                    if (subVarKey.length() > 0) {
                        int nestedKeyEnd = subVarKey.indexOf(".");
                        if (nestedKeyEnd > 0) {
                            String nestedKey = subVarKey.substring(0, nestedKeyEnd);
                            String nestedFilter = (filter != null ? filter : "") + nestedKey + ".";
                            if (nestedFilterList == null) {
                                nestedFilterList = new Vector();
                            }
                            nestedFilterList.addElement(nestedFilter);
                            queryString = queryString + serializeToQueryString(nestedKey, varValue, varFilter, varFilterPrefix, nestedFilter);
                        } else {
                            if (isBoolean(subVarValue)) {
                                if (toBoolean(subVarValue)) {
                                    subVarValue = "true";
                                } else {
                                    subVarValue = "false";
                                }
                            }
                            if (isSet(subVarValue)) {
                                if (varFilterPrefix.equals("retrieveLightData")) {
                                    if (filter.indexOf(".contextData.") < 0) {
                                        String subVarPrefix = subVarKey.substring(0, 4);
                                        String subVarSuffix = subVarKey.substring(4);
                                        if (subVarKey.equals("transactionID")) {
                                            subVarKey = "xact";
                                        } else if (subVarKey.equals("channel")) {
                                            subVarKey = "ch";
                                        } else if (subVarKey.equals("campaign")) {
                                            subVarKey = "v0";
                                        } else if (isNumber(subVarSuffix)) {
                                            if (subVarPrefix.equals("prop")) {
                                                subVarKey = "c" + subVarSuffix;
                                            } else if (subVarPrefix.equals("eVar")) {
                                                subVarKey = "v" + subVarSuffix;
                                            } else if (subVarPrefix.equals("list")) {
                                                subVarKey = "l" + subVarSuffix;
                                            } else if (subVarPrefix.equals("hier")) {
                                                subVarKey = "h" + subVarSuffix;
                                                subVarValue = ((String) subVarValue).substring(0, 255);
                                            }
                                        }
                                    }
                                }
                                queryString = queryString + "&" + escape(subVarKey) + "=" + escape("" + subVarValue);
                            }
                        }
                    }
                }
            }
        }
        if (queryString != "") {
            return queryString + "&." + varKey;
        }
        return queryString;
    }

    private String serializeToQueryString(String varKey, Hashtable varValue, String varFilter, String varFilterPrefix) {
        return serializeToQueryString(varKey, varValue, varFilter, varFilterPrefix, null);
    }

    protected String getQueryString() {
        String[] varList;
        String queryString = "";
        String varFilter = "";
        String eventFilter = "";
        String events = "";
        if (isSet(this.lightProfileID)) {
            varList = s.lightVarList;
            varFilter = s.lightTrackVars;
            if (isSet(varFilter)) {
                varFilter = "," + varFilter + "," + joinArray(",", s.lightRequiredVarList) + ",";
            }
        } else {
            varList = s.accountVarList;
            if (isSet(s.linkType)) {
                varFilter = s.linkTrackVars;
                eventFilter = s.linkTrackEvents;
            }
            if (isSet(varFilter)) {
                varFilter = "," + varFilter + "," + joinArray(",", s.requiredVarList) + ",";
            }
            if (isSet(eventFilter)) {
                eventFilter = "," + eventFilter + ",";
            }
            if (isSet(s.events2)) {
                events = events + (events != "" ? "," : "") + s.events2;
            }
        }
        for (String varKey : varList) {
            String varKey2;
            String varPrefix;
            String varSuffix;
            String varValue = getAccountVar(varKey2);
            if (varKey2.length() > 4) {
                varPrefix = varKey2.substring(0, 4);
                varSuffix = varKey2.substring(4);
            } else {
                varPrefix = null;
                varSuffix = null;
            }
            if (!isSet(varValue) && varKey2.equals("events") && isSet(events)) {
                varValue = events;
                events = "";
            }
            if (isSet(varValue) && (!isSet(varFilter) || varFilter.indexOf("," + varKey2 + ",") >= 0)) {
                StringBuilder append;
                String str;
                if (varKey2.equals("timestamp")) {
                    varKey2 = "ts";
                } else if (varKey2.equals("dynamicVariablePrefix")) {
                    varKey2 = "D";
                } else if (varKey2.equals("visitorID")) {
                    varKey2 = "vid";
                } else if (varKey2.equals("pageURL")) {
                    varKey2 = "g";
                } else if (varKey2.equals("referrer")) {
                    varKey2 = "r";
                } else if (varKey2.equals("vmk") || varKey2.equals("visitorMigrationKey")) {
                    varKey2 = "vmt";
                } else if (varKey2.equals("visitorMigrationServer")) {
                    varKey2 = "vmf";
                    if (isSet(s.ssl) && isSet(s.visitorMigrationServerSecure)) {
                        varValue = "";
                    }
                } else if (varKey2.equals("visitorMigrationServerSecure")) {
                    varKey2 = "vmf";
                    if (!isSet(s.ssl) && isSet(s.visitorMigrationServer)) {
                        varValue = "";
                    }
                } else if (varKey2.equals("charSet")) {
                    varKey2 = "ce";
                } else if (varKey2.equals("visitorNamespace")) {
                    varKey2 = "ns";
                } else if (varKey2.equals("cookieDomainPeriods")) {
                    varKey2 = "cdp";
                } else if (varKey2.equals("cookieLifetime")) {
                    varKey2 = "cl";
                } else if (varKey2.equals("variableProvider")) {
                    varKey2 = "vvp";
                } else if (varKey2.equals("currencyCode")) {
                    varKey2 = "cc";
                } else if (varKey2.equals("channel")) {
                    varKey2 = "ch";
                } else if (varKey2.equals("transactionID")) {
                    varKey2 = "xact";
                } else if (varKey2.equals("campaign")) {
                    varKey2 = "v0";
                } else if (varKey2.equals("resolution")) {
                    varKey2 = "s";
                } else if (varKey2.equals("events")) {
                    if (isSet(events)) {
                        varValue = varValue + (varValue != "" ? "," : "") + events;
                    }
                    if (isSet(eventFilter)) {
                        String[] varValueParts = splitString(",", varValue);
                        varValue = "";
                        for (int varSubNum = 0; varSubNum < varValueParts.length; varSubNum++) {
                            String varValuePart = varValueParts[varSubNum];
                            int varValuePartPos = varValuePart.indexOf("=");
                            if (varValuePartPos >= 0) {
                                varValuePart = varValuePart.substring(0, varValuePartPos);
                            }
                            varValuePartPos = varValuePart.indexOf(":");
                            if (varValuePartPos >= 0) {
                                varValuePart = varValuePart.substring(0, varValuePartPos);
                            }
                            if (eventFilter.indexOf("," + varValuePart + ",") >= 0) {
                                append = new StringBuilder().append(varValue);
                                if (isSet(varValue)) {
                                    str = ",";
                                } else {
                                    str = "";
                                }
                                varValue = append.append(str).append(varValueParts[varSubNum]).toString();
                            }
                        }
                    }
                } else if (varKey2.equals("events2")) {
                    varValue = "";
                } else if (varKey2.equals("contextData")) {
                    queryString = queryString + serializeToQueryString("c", s.contextData, varFilter, varKey2, null);
                    varValue = "";
                } else if (varKey2.equals("lightProfileID")) {
                    varKey2 = "mtp";
                } else if (varKey2.equals("lightStoreForSeconds")) {
                    varKey2 = "mtss";
                    if (!isSet(s.lightProfileID)) {
                        varValue = "";
                    }
                } else if (varKey2.equals("lightIncrementBy")) {
                    varKey2 = "mti";
                    if (!isSet(s.lightProfileID)) {
                        varValue = "";
                    }
                } else if (varKey2.equals("retrieveLightProfiles")) {
                    varKey2 = "mtsr";
                } else if (varKey2.equals("deleteLightProfiles")) {
                    varKey2 = "mtsd";
                } else if (varKey2.equals("retrieveLightData")) {
                    if (isSet(s.retrieveLightProfiles)) {
                        queryString = queryString + serializeToQueryString("mts", s.retrieveLightData, varFilter, varKey2, null);
                    }
                    varValue = "";
                } else if (isNumber(varSuffix)) {
                    if (varPrefix.equals("prop")) {
                        varKey2 = "c" + varSuffix;
                    } else if (varPrefix.equals("eVar")) {
                        varKey2 = "v" + varSuffix;
                    } else if (varPrefix.equals("list")) {
                        varKey2 = "l" + varSuffix;
                    } else if (varPrefix.equals("hier")) {
                        varKey2 = "h" + varSuffix;
                        if (varValue.length() > 255) {
                            varValue = varValue.substring(0, 255);
                        }
                    }
                }
                if (isSet(varValue)) {
                    append = new StringBuilder().append(queryString).append("&").append(escape(varKey2)).append("=");
                    if (varKey2.length() <= 3 || !varKey2.substring(0, 3).equals("pev")) {
                        str = escape(varValue);
                    } else {
                        str = varValue;
                    }
                    queryString = append.append(str).toString();
                }
            }
        }
        return queryString;
    }

    protected void variableOverridesApply(Hashtable variableOverrides) {
        variableOverridesApply(variableOverrides, false);
    }

    protected void variableOverridesApply(Hashtable variableOverrides, boolean restoring) {
        AppMeasurementBase s = this;
        for (String varKey : s.accountVarList) {
            if ((variableOverrides.containsKey(varKey) && ((isString(variableOverrides.get(varKey)) && isSet(toString(variableOverrides.get(varKey)))) || isSet(variableOverrides.get(varKey)))) || (variableOverrides.containsKey("!" + varKey) && isSet((String) variableOverrides.get("!" + varKey)))) {
                Hashtable varValue;
                if (variableOverrides.containsKey(varKey)) {
                    varValue = variableOverrides.get(varKey);
                } else {
                    varValue = null;
                }
                if (!restoring && (varKey.equals("contextData") || varKey.equals("retrieveLightData"))) {
                    Hashtable varValueHashTable = varValue;
                    Enumeration keys;
                    String subVarKey;
                    if (isSet(s.contextData)) {
                        keys = s.contextData.keys();
                        while (keys.hasMoreElements()) {
                            subVarKey = (String) keys.nextElement();
                            if (!varValueHashTable.containsKey(subVarKey) || !isSet(varValueHashTable.get(subVarKey))) {
                                varValueHashTable.put(subVarKey, s.contextData.get(subVarKey));
                            }
                        }
                    } else if (isSet(s.retrieveLightData)) {
                        keys = s.retrieveLightData.keys();
                        while (keys.hasMoreElements()) {
                            subVarKey = (String) keys.nextElement();
                            if (!varValueHashTable.containsKey(subVarKey) || !isSet(varValueHashTable.get(subVarKey))) {
                                varValueHashTable.put(subVarKey, s.retrieveLightData.get(subVarKey));
                            }
                        }
                    }
                }
                if (varKey.equals("contextData")) {
                    s.contextData = varValue;
                } else if (varKey.equals("retrieveLightData")) {
                    s.retrieveLightData = varValue;
                } else {
                    setAccountVar(varKey, (String) varValue);
                }
            }
        }
        for (String varKey2 : s.accountConfigList) {
            if ((variableOverrides.containsKey(varKey2) && isSet(variableOverrides.get(varKey2))) || (variableOverrides.containsKey("!" + varKey2) && isSet((String) variableOverrides.get("!" + varKey2)))) {
                Object varValue2;
                if (variableOverrides.containsKey(varKey2)) {
                    varValue2 = variableOverrides.get(varKey2);
                } else {
                    varValue2 = null;
                }
                setAccountVar(varKey2, (String) varValue2);
            }
        }
    }

    protected void variableOverridesBuild(Hashtable variableOverrides) {
        String varValue;
        AppMeasurementBase s = this;
        for (String varKey : s.accountVarList) {
            if (!variableOverrides.containsKey(varKey) || !isSet((String) variableOverrides.get(varKey))) {
                varValue = getAccountVar(varKey);
                if (varKey.equals("contextData") && isSet(s.contextData)) {
                    variableOverrides.put(varKey, s.contextData);
                } else if (varKey.equals("retrieveLightData") && isSet(s.retrieveLightData)) {
                    variableOverrides.put(varKey, s.retrieveLightData);
                } else {
                    varValue = getAccountVar(varKey);
                    if (isSet(varValue)) {
                        variableOverrides.put(varKey, varValue);
                    }
                }
                if (!((variableOverrides.containsKey(varKey) && isSet(variableOverrides.get(varKey))) || variableOverrides.containsKey("!" + varKey))) {
                    variableOverrides.put("!" + varKey, "1");
                }
            }
        }
        for (String varKey2 : s.accountConfigList) {
            if (!variableOverrides.containsKey(varKey2) || !isSet((String) variableOverrides.get(varKey2))) {
                varValue = getAccountVar(varKey2);
                if (isSet(varValue)) {
                    variableOverrides.put(varKey2, varValue);
                }
                if (!((variableOverrides.containsKey(varKey2) && isSet(variableOverrides.get(varKey2))) || variableOverrides.containsKey("!" + varKey2))) {
                    variableOverrides.put("!" + varKey2, "1");
                }
            }
        }
    }

    public void clearVars() {
        AppMeasurementBase s = this;
        for (String varKey : s.accountVarList) {
            String varPrefix;
            if (varKey.length() > 4) {
                varPrefix = varKey.substring(0, 4);
            } else {
                varPrefix = "";
            }
            if (varKey.equals("channel") || varKey.equals("events") || varKey.equals("purchaseID") || varKey.equals("transactionID") || varKey.equals("products") || varKey.equals("state") || varKey.equals("zip") || varKey.equals("campaign") || varPrefix.equals("prop") || varPrefix.equals("eVar") || varPrefix.equals("hier")) {
                setAccountVar(varKey, null);
            }
        }
    }

    public String track(Hashtable variableOverrides) {
        AppMeasurementBase s = this;
        String code = "";
        Hashtable variableOverridesBackup = null;
        Calendar tm = getCalendar();
        String cacheBusting = "s" + ((int) Math.floor(randomNumGen.nextDouble() * 1.0E8d));
        String queryString = "t=" + escape("" + tm.get(5) + "/" + tm.get(2) + "/" + tm.get(1) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + tm.get(11) + ":" + tm.get(12) + ":" + tm.get(13) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + (tm.get(7) - 1) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + ((tm.getTimeZone().getOffset(1, tm.get(1), tm.get(2), tm.get(5), tm.get(7), (((((tm.get(11) * 60) + tm.get(12)) * 60) + tm.get(13)) * EDSV2MediaType.MEDIATYPE_MOVIE) + tm.get(14)) / 60000) * -1));
        if (variableOverrides != null) {
            variableOverridesBackup = new Hashtable();
            variableOverridesBuild(variableOverridesBackup);
            variableOverridesApply(variableOverrides);
        }
        if (s.usePlugins && _hasDoPlugins()) {
            _doPlugins();
        }
        if (isSet(s.account)) {
            if (isSet(s.trackOffline) && !isSet(s.timestamp)) {
                s.timestamp = (int) Math.floor(getTime() / 1000.0d);
            }
            setDefaults();
            handleLinkTracking();
            handleTechnology();
            code = makeRequest(cacheBusting, queryString + getQueryString());
        }
        if (variableOverrides != null) {
            variableOverridesApply(variableOverridesBackup);
        }
        s.timestamp = 0;
        s.referrer = null;
        s.pe = null;
        s.pev1 = null;
        s.pev2 = null;
        s.pev3 = null;
        s.linkURL = null;
        s.linkName = null;
        s.linkType = null;
        s.lightProfileID = null;
        s.retrieveLightProfiles = null;
        s.deleteLightProfiles = null;
        return code;
    }

    public String track() {
        return track(null);
    }

    public String trackLink(String linkURL, String linkType, String linkName, Hashtable variableOverrides) {
        this.linkURL = linkURL;
        this.linkType = linkType;
        this.linkName = linkName;
        return track(variableOverrides);
    }

    public String trackLight(String profileID, int storeForSeconds, int incrementBy, Hashtable variableOverrides) {
        this.lightProfileID = profileID;
        this.lightStoreForSeconds = storeForSeconds;
        this.lightIncrementBy = incrementBy;
        return track(variableOverrides);
    }

    public String trackLink(String linkURL, String linkType, String linkName) {
        return trackLink(linkURL, linkType, linkName, null);
    }

    public String trackLight(String profileID, int storeForSeconds, int incrementBy) {
        return trackLight(profileID, storeForSeconds, incrementBy, null);
    }

    public String trackLight(String profileID, int storeForSeconds) {
        return trackLight(profileID, storeForSeconds, 0, null);
    }

    public String trackLight(String profileID) {
        return trackLight(profileID, 0, 0, null);
    }

    protected void setDefaults() {
        if (!isSet(this.userAgent)) {
            s.userAgent = getDefaultUserAgent();
        }
        if (!isSet(s.acceptLanguage)) {
            s.acceptLanguage = getDefaultAcceptLanguage();
        }
        if (!isSet(s.visitorID)) {
            s.visitorID = getDefaultVisitorID();
        }
        if (!isSet(s.pageURL)) {
            s.pageURL = getDefaultPageURL();
        }
        if (isSet(s.pageURL) && s.pageURL.toLowerCase().indexOf("https://") >= 0) {
            s.ssl = true;
        }
        if (!isSet(s.referrer) && !isSet(s._1_referrer)) {
            s.referrer = getDefaultReferrer();
            s._1_referrer = 1;
        }
    }

    protected String getDefaultUserAgent() {
        return "";
    }

    protected String getDefaultAcceptLanguage() {
        return "";
    }

    protected String getDefaultVisitorID() {
        return "";
    }

    protected String getDefaultPageURL() {
        return "";
    }

    protected String getDefaultReferrer() {
        return "";
    }
}
