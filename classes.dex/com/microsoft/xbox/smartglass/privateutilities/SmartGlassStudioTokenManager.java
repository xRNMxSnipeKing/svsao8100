package com.microsoft.xbox.smartglass.privateutilities;

import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.smartglass.canvas.CanvasTokenManager;
import java.io.IOException;
import java.util.Hashtable;

public class SmartGlassStudioTokenManager implements CanvasTokenManager {
    private static final String TOKEN_END_TAG = "</EncryptedAssertion>";
    private static final String TOKEN_START_TAG = "<EncryptedAssertion";
    private static SmartGlassStudioTokenManager _instance = null;
    private String _password;
    private String _rpsTicket;
    private final Hashtable<String, String> _tokenMap;
    private String _userName;

    private SmartGlassStudioTokenManager() {
        throw new UnsupportedOperationException();
    }

    private SmartGlassStudioTokenManager(String userName, String password) {
        this._tokenMap = new Hashtable();
        this._userName = userName;
        this._password = password;
        this._rpsTicket = null;
    }

    public static void createInstance(String userName, String password) {
        _instance = new SmartGlassStudioTokenManager(userName, password);
    }

    public static SmartGlassStudioTokenManager getInstance() {
        return _instance;
    }

    private void authenticateUser() throws IOException {
        this._rpsTicket = new RpsClient(CompanionSession.getInstance().environment, this._userName, this._password).getTicket(null);
    }

    public String getXstsToken(String audienceUri, boolean forceRefresh) throws IOException {
        if (this._tokenMap.containsKey(audienceUri) && !forceRefresh) {
            return (String) this._tokenMap.get(audienceUri);
        }
        if (this._rpsTicket == null) {
            authenticateUser();
        }
        String xstsToken = new XstsClient(CompanionSession.getInstance().environment, this._rpsTicket, audienceUri).getToken(null);
        int start = xstsToken.indexOf(TOKEN_START_TAG);
        int end = xstsToken.indexOf(TOKEN_END_TAG);
        if (start == -1 || end == -1) {
            throw new IndexOutOfBoundsException("Unable to extract <EncryptedAssertionBlock> from XSTS token.");
        }
        xstsToken = xstsToken.substring(start, TOKEN_END_TAG.length() + end);
        if (xstsToken == null && this._tokenMap.containsKey(audienceUri)) {
            this._tokenMap.remove(audienceUri);
        } else if (xstsToken != null) {
            this._tokenMap.put(audienceUri, xstsToken);
        }
        return xstsToken;
    }
}
