package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.service.model.serialization.TokensRaw;
import java.util.Date;

public class PartnerToken extends Token {
    private static final int PARTNER_TOKEN_LIFETIME_MS = 14400000;

    public PartnerToken(String audience) {
        super(TokenType.Partner);
        setTarget(audience);
    }

    protected Date getDefaultExpirationDateTime(TokenType type) {
        return new Date(new Date().getTime() + 14400000);
    }

    public static PartnerToken parseTokenFromRaw(String audience, TokensRaw tokenRaw) {
        PartnerToken token = new PartnerToken(audience);
        if (!(tokenRaw.Partner == null || tokenRaw.Partner.length() == 0)) {
            token.setToken(tokenRaw.Partner);
        }
        return token;
    }
}
