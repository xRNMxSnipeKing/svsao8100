package com.microsoft.xbox.smartglass.privateutilities;

import com.microsoft.xbox.service.network.managers.xblshared.Environment;
import com.microsoft.xbox.smartglass.canvas.InputStreamUtils;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Observable;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class RpsClient extends Observable implements Runnable {
    private static String loginBodyTemplate = "<LoginRequest><ClientInfo name=\"TestTeam Client\" version=\"1.35\" /><User><SignInName>%s</SignInName><Password>%s</Password><SavePassword /></User><DAOption /><TargetOption /></LoginRequest>";
    private String _loginBody;
    private RpsClientResult _result = new RpsClientResult();
    private String _windowsLiveAuthenticationEndpoint;

    public RpsClient(Environment environment, String username, String password) {
        this._windowsLiveAuthenticationEndpoint = environment.getWindowsLiveEndpointUri();
        this._loginBody = String.format(loginBodyTemplate, new Object[]{username, password});
    }

    public String getTicket(HttpClient client) throws IOException {
        if (client == null) {
            client = HttpClient.create();
        }
        HttpPost post = new HttpPost(this._windowsLiveAuthenticationEndpoint);
        post.setEntity(new StringEntity(this._loginBody, "UTF-8"));
        String body = new String(InputStreamUtils.readToEnd(client.execute(post).getStream()), "UTF-8");
        int index = body.indexOf("t=");
        if (index < 0) {
            return null;
        }
        body = body.substring(index);
        index = body.indexOf(60);
        if (index >= 0) {
            return URLDecoder.decode(body.substring(0, index), "UTF-8");
        }
        return null;
    }

    public void run() {
        HttpClient client = null;
        try {
            this._result = new RpsClientResult();
            client = HttpClient.create();
            this._result.ticket = getTicket(client);
        } catch (IOException e) {
            this._result.exception = e;
            if (!(client == null || client.getLastResponse() == null)) {
                this._result.errorMessage = String.format("%s (%d)", new Object[]{response.getStatusString(), Integer.valueOf(response.getStatusCode())});
            }
        }
        setChanged();
    }

    public void notifyObservers() {
        notifyObservers(this._result);
    }

    public void notifyObservers(Object object) {
        super.notifyObservers(this._result);
    }
}
