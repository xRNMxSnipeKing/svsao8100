package com.microsoft.xbox.toolkit;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlUtil {
    public static URI getEncodedUri(String oldUrl) {
        if (oldUrl == null || oldUrl.length() == 0) {
            return null;
        }
        return getEncodedUriNonNull(oldUrl);
    }

    public static URI getEncodedUriNonNull(String oldUrl) {
        try {
            URL url = new URL(oldUrl);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException e) {
            XLELog.Error("UrlUtil", "failed to encode " + oldUrl);
            return null;
        } catch (MalformedURLException e2) {
            XLELog.Error("UrlUtil", "failed to encode " + oldUrl);
            return null;
        }
    }

    public static URI getUri(String encodedUrl) {
        if (JavaUtil.isNullOrEmpty(encodedUrl)) {
            return null;
        }
        try {
            return new URI(encodedUrl);
        } catch (Exception e) {
            XLELog.Error("UrlUtil", "Failed to create uri object:" + encodedUrl);
            return null;
        }
    }

    public static String encodeUrl(String oldUrl) {
        if (oldUrl == null || oldUrl.length() == 0) {
            return null;
        }
        URI uri = getEncodedUri(oldUrl);
        if (uri != null) {
            return uri.toString();
        }
        return null;
    }

    public static boolean UrisEqualCaseInsensitive(URI lhs, URI rhs) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        return JavaUtil.stringsEqualCaseInsensitive(lhs.toString(), rhs.toString());
    }
}
