package com.microsoft.xbox.authenticate;

public class LoginServiceManagerFactory {
    private static LoginServiceManagerFactory instance = new LoginServiceManagerFactory();
    private IAuthServiceManager authServiceManager;
    private ITokenStorageManager tokenStorageManager;

    private LoginServiceManagerFactory() {
    }

    public static LoginServiceManagerFactory getInstance() {
        return instance;
    }

    public ITokenStorageManager getTokenStorageManager() {
        if (this.tokenStorageManager != null) {
            return this.tokenStorageManager;
        }
        return new TokenStorageManager();
    }

    public IAuthServiceManager getAuthServiceManager() {
        if (this.authServiceManager != null) {
            return this.authServiceManager;
        }
        return new AuthServiceManager();
    }
}
