package com.microsoft.xbox.smartglass.privateutilities;

import com.microsoft.xbox.service.network.managers.xblshared.Environment;
import com.microsoft.xbox.smartglass.canvas.InputStreamUtils;
import java.io.IOException;
import java.util.Observable;
import java.util.UUID;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class XstsClient extends Observable implements Runnable {
    private static String _xstsBodyTemplate = "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\"><s:Header><a:Action s:mustUnderstand=\"1\">http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue</a:Action><a:MessageID>urn:uuid:%s</a:MessageID><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo><a:To s:mustUnderstand=\"1\">http://activeauth.xboxlive.com/XSts/xsts.svc/IWSTrust13</a:To></s:Header><s:Body><trust:RequestSecurityToken xmlns:trust=\"http://docs.oasis-open.org/ws-sx/ws-trust/200512\"><wsp:AppliesTo xmlns:wsp=\"http://schemas.xmlsoap.org/ws/2004/09/policy\"><EndpointReference xmlns=\"http://www.w3.org/2005/08/addressing\"><Address>%s</Address></EndpointReference></wsp:AppliesTo><trust:KeyType>http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer</trust:KeyType><trust:RequestType>http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue</trust:RequestType><trust:TokenType>http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0</trust:TokenType></trust:RequestSecurityToken></s:Body></s:Envelope>";
    private Environment _environment;
    private XstsClientResult _result;
    private String _rpsTicket;
    private String _xstsBody;
    private String _xstsEndpointUri = this._environment.getXstsEndpointUri();

    public XstsClient(Environment environment, String rpsTicket, String audienceUri) {
        this._environment = environment;
        this._rpsTicket = rpsTicket;
        this._xstsBody = String.format(_xstsBodyTemplate, new Object[]{UUID.randomUUID().toString(), audienceUri});
        this._result = new XstsClientResult();
        this._result.audienceUri = audienceUri;
    }

    public String getToken(HttpClient client) throws IOException {
        if (client == null) {
            client = HttpClient.create();
        }
        HttpPost post = new HttpPost(this._xstsEndpointUri);
        post.addHeader("Content-Type", "application/soap+xml");
        post.addHeader("Authorization", "WLID1.0 " + this._rpsTicket);
        post.setEntity(new StringEntity(this._xstsBody, "UTF-8"));
        return new String(InputStreamUtils.readToEnd(client.execute(post).getStream()), "UTF-8");
    }

    public void run() {
        HttpClient client = null;
        try {
            this._result = new XstsClientResult();
            client = HttpClient.create();
            this._result.token = getToken(client);
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

    public void notifyObservers(Object data) {
        super.notifyObservers(this._result);
    }
}
