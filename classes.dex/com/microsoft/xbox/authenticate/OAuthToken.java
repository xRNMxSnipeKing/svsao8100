package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.service.model.serialization.RefreshTokenRaw;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.Date;

public class OAuthToken extends Token {
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String EXPIRES_KEY = "expires_in";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final int REFRESH_TOKEN_LIFETIME_MS = 1384828928;
    private static final String SCOPE_KEY = "scope";

    public OAuthToken(TokenType type) {
        super(type);
    }

    protected Date getDefaultExpirationDateTime(TokenType type) {
        if (type == TokenType.Refresh) {
            return new Date(new Date().getTime() + 1384828928);
        }
        return null;
    }

    public static OAuthToken[] parseTokensFromOAuthFragment(String fragment) {
        String scope = null;
        String expires = null;
        OAuthToken accessToken = null;
        OAuthToken refreshToken = null;
        for (String entry : fragment.split("&")) {
            int index = entry.indexOf("=");
            String key = entry.substring(0, index);
            String value = entry.substring(index + 1);
            if (key.equalsIgnoreCase(ACCESS_TOKEN_KEY)) {
                accessToken = new OAuthToken(TokenType.Access);
                accessToken.setToken(value);
            } else if (key.equalsIgnoreCase(REFRESH_TOKEN_KEY)) {
                refreshToken = new OAuthToken(TokenType.Refresh);
                refreshToken.setToken(value);
            } else if (key.equalsIgnoreCase(SCOPE_KEY)) {
                scope = value;
            } else if (key.equalsIgnoreCase(EXPIRES_KEY)) {
                expires = value;
            }
        }
        if (accessToken == null || refreshToken == null || expires == null || scope == null) {
            XLELog.Diagnostic("XboxToken", "Failed to parse the fragments.");
            return null;
        }
        accessToken.setTarget(scope);
        refreshToken.setTarget(scope);
        try {
            accessToken.setExpires(Integer.parseInt(expires));
        } catch (NumberFormatException e) {
            XLELog.Error("XboxToken", "Failed to parse access token expiration time: " + e.toString());
        }
        return new OAuthToken[]{accessToken, refreshToken};
    }

    public static OAuthToken[] parseTokensFromOAuthResponseBodyJSON(String response) {
        String scope = null;
        String expires = null;
        OAuthToken accessToken = null;
        OAuthToken refreshToken = null;
        for (String entry : response.split(",")) {
            String[] keyValue = entry.split("\":");
            XLEAssert.assertTrue(keyValue.length == 2);
            String key = keyValue[0];
            String value = keyValue[1].trim().replace("\"", "");
            if (key.contains(ACCESS_TOKEN_KEY)) {
                accessToken = new OAuthToken(TokenType.Access);
                accessToken.setToken(value);
            } else if (key.contains(REFRESH_TOKEN_KEY)) {
                refreshToken = new OAuthToken(TokenType.Refresh);
                refreshToken.setToken(value);
            } else if (key.contains(SCOPE_KEY)) {
                scope = value;
            } else if (key.contains(EXPIRES_KEY)) {
                expires = value;
            }
        }
        if (accessToken == null || refreshToken == null || expires == null || scope == null) {
            XLELog.Diagnostic("XboxToken", "Failed to parse the response body.");
            return null;
        }
        accessToken.setTarget(scope);
        refreshToken.setTarget(scope);
        try {
            accessToken.setExpires(Integer.parseInt(expires));
        } catch (NumberFormatException e) {
            XLELog.Error("XboxToken", "Failed to parse access token expiration time: " + e.toString());
        }
        return new OAuthToken[]{accessToken, refreshToken};
    }

    public static OAuthToken parseRefreshTokenFromRaw(RefreshTokenRaw rawToken) {
        OAuthToken refreshToken = new OAuthToken(TokenType.Refresh);
        refreshToken.setToken(rawToken.Token);
        refreshToken.setTarget(rawToken.Scope);
        refreshToken.setExpires(rawToken.Expires);
        return refreshToken;
    }

    public RefreshTokenRaw getRefreshTokenRaw() {
        XLEAssert.assertTrue(getType() == TokenType.Refresh);
        RefreshTokenRaw rawToken = new RefreshTokenRaw();
        rawToken.Expires = getExpires();
        rawToken.Scope = getTarget();
        rawToken.Token = getToken();
        return rawToken;
    }
}
