package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StreamUtil {
    public static byte[] CreateByteArray(InputStream stream) {
        ByteArrayOutputStream rv = new ByteArrayOutputStream();
        try {
            CopyStream(rv, stream);
            return rv.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static void CopyStream(OutputStream output, InputStream input) throws IOException {
        byte[] buffer = new byte[AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS];
        while (true) {
            int readlen = input.read(buffer);
            if (readlen > 0) {
                output.write(buffer, 0, readlen);
            } else {
                output.flush();
                return;
            }
        }
    }

    public static void CopyStreamWithLimit(OutputStream output, InputStream input, int maxLen) throws IOException {
        byte[] buffer = new byte[AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS];
        int totalLen = 0;
        do {
            int readlen = input.read(buffer);
            if (readlen <= 0) {
                break;
            }
            output.write(buffer, 0, readlen);
            totalLen += readlen;
        } while (totalLen <= maxLen);
        output.flush();
        XLELog.Info("CopyStreamWithLimit", String.format("length of stream copied : %d", new Object[]{Integer.valueOf(totalLen)}));
    }

    public static String ReadAsString(InputStream stream) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    return builder.toString();
                }
                builder.append(line);
                builder.append('\n');
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static byte[] HexStringToByteArray(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException("hexString invalid");
        }
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        XLEAssert.assertTrue(hexString.length() % 2 == 0);
        byte[] rv = new byte[(hexString.length() / 2)];
        for (int i = 0; i < hexString.length(); i += 2) {
            rv[i / 2] = Byte.parseByte(hexString.substring(i, i + 2), 16);
        }
        return rv;
    }
}
