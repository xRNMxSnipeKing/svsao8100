package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.authenticate.Token;
import com.microsoft.xbox.service.model.zest.SignInResponse;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class CommerceServiceManager implements ICommerceServiceManager {
    private static final String signInBody = "<SignInRequest xmlns:i='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://schemas.zune.net/commerce/2009/01'><TunerDrmType>PlayReady</TunerDrmType><TunerInfo><ID>S-1-5-21-3174142767-1358996543-3781426270</ID><Name>FGAO-X200</Name><Type>XBLWINClient</Type><Version>1.1.1</Version></TunerInfo></SignInRequest>";

    public SignInResponse signIn() throws XLEException {
        XLELog.Diagnostic("CommerceServiceManager", "sign in to Zest...");
        String url = XboxLiveEnvironment.Instance().getZestAccountSigninUrl();
        ArrayList<Header> headers = new ArrayList();
        Token musictoken = XstsTokenManager.getInstance().getXstsToken(XboxLiveEnvironment.MUSIC_AUDIENCE_URI);
        if (musictoken == null || JavaUtil.isNullOrEmpty(musictoken.getToken())) {
            throw new XLEException(XLEErrorCode.INVALID_TOKEN);
        }
        headers.add(new BasicHeader("Authorization", "XBL2.0 x=" + musictoken.getToken()));
        headers.add(new BasicHeader("Content-type", "application/soap+xml; charset=utf-8"));
        try {
            XLEHttpStatusAndStream statusAndStream = ServiceCommon.postStringWithStatus(url, headers, signInBody);
            if (200 == statusAndStream.statusCode) {
                XLELog.Diagnostic("CommerceServiceManager", "Successfully retrieved user sign in info.");
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
                return (SignInResponse) XMLHelper.instance().load(statusAndStream.stream, SignInResponse.class);
            }
            XLELog.Error("CommerceServiceManager", "service return error " + Integer.toString(statusAndStream.statusCode));
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
            throw new XLEException(XLEErrorCode.INVALID_ACCESS_TOKEN);
        } catch (Throwable e) {
            throw new XLEException((long) XLEErrorCode.FAILED_TO_PARSE_ZEST_RESPONSE, e);
        } catch (Exception e2) {
            if (e2 instanceof XLEException) {
                throw ((XLEException) e2);
            }
            XLELog.Error("CommerceServiceManager", "Failed to get xsts token " + e2.toString());
            throw new XLEException(XLEErrorCode.INVALID_ACCESS_TOKEN);
        }
    }
}
