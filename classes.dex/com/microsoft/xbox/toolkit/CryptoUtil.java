package com.microsoft.xbox.toolkit;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {
    public static byte[] hmacSha1(byte[] input, byte[] key) {
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return mac.doFinal(input);
        } catch (Exception e) {
            XLELog.Error("CryptoUtil", "failed to generate hmacSHA1 with exception " + e.toString());
            return null;
        }
    }

    public static byte[] SHA1(byte[] inputBytes) {
        try {
            byte[] rv = MessageDigest.getInstance("SHA-1").digest(inputBytes);
            XLEAssert.assertTrue(rv.length == 20);
            return rv;
        } catch (Exception e) {
            XLELog.Error("CryptoUtil", "failed to generate SHA1 with exception " + e.toString());
            return null;
        }
    }

    public static byte[] BEUTF16(String input) {
        try {
            return input.getBytes("UTF-16BE");
        } catch (Exception e) {
            XLELog.Error("CryptoUtil", "failed to generate BEUTF16 with exception " + e.toString());
            return null;
        }
    }

    public static String BEUTF16DecodeString(byte[] input) {
        try {
            return new String(input, "UTF-16BE");
        } catch (Exception e) {
            XLELog.Error("CryptoUtil", "failed to decode BEUTF16 with exception " + e.toString());
            return null;
        }
    }

    public static boolean VerifySignatureSHA256withRSA(byte[] modulus, byte[] exponent, byte[] inputData, byte[] signatureBytes) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent))));
            signature.update(inputData);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            XLELog.Error("CryptoUtil", "failed to VerifySignatureSHA256withRSA with exception " + e.toString());
            return false;
        }
    }
}
