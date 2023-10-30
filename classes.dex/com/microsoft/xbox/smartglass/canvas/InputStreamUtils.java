package com.microsoft.xbox.smartglass.canvas;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class InputStreamUtils {
    private static final int blockSize = 8192;

    public static byte[] readToEnd(InputStream is) throws IOException {
        ReadableByteChannel bc = Channels.newChannel(is);
        ByteBuffer bb = ByteBuffer.allocate(8192);
        while (bc.read(bb) != -1) {
            bb = resizeBuffer(bb);
        }
        byte[] result = new byte[bb.position()];
        bb.position(0);
        bb.get(result);
        return result;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer in) {
        ByteBuffer result = in;
        if (in.remaining() >= 8192) {
            return result;
        }
        result = ByteBuffer.allocate(in.capacity() * 2);
        in.flip();
        result.put(in);
        return result;
    }
}
