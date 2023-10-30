package com.microsoft.xbox.authenticate;

import java.util.Date;

public class XstsToken extends Token {
    private static final int XSTS_TOKEN_LIFETIME_MS = 14400000;

    public XstsToken(String audience) {
        super(TokenType.Xsts);
        setTarget(audience);
    }

    protected Date getDefaultExpirationDateTime(TokenType type) {
        return new Date(new Date().getTime() + 14400000);
    }
}
