package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import java.util.Date;

public abstract class Token {
    private static final long MS_REMAINING_BEFORE_EXPIRATION = 1200000;
    private Date expires = getDefaultExpirationDateTime(this.type);
    private String target;
    private String token;
    private TokenType type;

    protected abstract Date getDefaultExpirationDateTime(TokenType tokenType);

    public Token(TokenType type) {
        this.type = type;
    }

    public String getTarget() {
        return this.target;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getToken() {
        return this.token;
    }

    public Date getExpires() {
        return this.expires;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setExpires(int expiresInSeconds) {
        this.expires = new Date(new Date().getTime() + (((long) (expiresInSeconds * EDSV2MediaType.MEDIATYPE_MOVIE)) - MS_REMAINING_BEFORE_EXPIRATION));
    }

    public void setExpires(Date expirationDate) {
        this.expires = expirationDate;
    }

    public boolean isExpired() {
        return this.expires.getTime() - new Date().getTime() < 0;
    }

    public boolean isValid() {
        return (this.token == null || this.target == null || this.expires == null || isExpired()) ? false : true;
    }
}
