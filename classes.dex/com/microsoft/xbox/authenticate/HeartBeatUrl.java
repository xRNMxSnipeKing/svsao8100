package com.microsoft.xbox.authenticate;

public class HeartBeatUrl {
    public long timeStamp = System.currentTimeMillis();
    public String url;

    public HeartBeatUrl(String url) {
        this.url = url;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HeartBeatUrl)) {
            return false;
        }
        HeartBeatUrl heartbeatUrl = (HeartBeatUrl) obj;
        if (this.url.equals(heartbeatUrl.url) && this.timeStamp == heartbeatUrl.timeStamp) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.url == null ? 0 : this.url.hashCode();
    }
}
