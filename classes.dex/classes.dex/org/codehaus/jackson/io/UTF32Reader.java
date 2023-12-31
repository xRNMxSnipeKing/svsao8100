package org.codehaus.jackson.io;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

public final class UTF32Reader extends BaseReader {
    final boolean mBigEndian;
    int mByteCount = 0;
    int mCharCount = 0;
    char mSurrogate = '\u0000';

    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    public /* bridge */ /* synthetic */ int read() throws IOException {
        return super.read();
    }

    public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian) {
        super(ctxt, in, buf, ptr, len);
        this.mBigEndian = isBigEndian;
    }

    public int read(char[] cbuf, int start, int len) throws IOException {
        if (this._buffer == null) {
            return -1;
        }
        if (len < 1) {
            return len;
        }
        int outPtr;
        if (start < 0 || start + len > cbuf.length) {
            reportBounds(cbuf, start, len);
        }
        len += start;
        int outPtr2 = start;
        if (this.mSurrogate != '\u0000') {
            outPtr = outPtr2 + 1;
            cbuf[outPtr2] = this.mSurrogate;
            this.mSurrogate = '\u0000';
        } else {
            int left = this._length - this._ptr;
            if (left < 4 && !loadMore(left)) {
                return -1;
            }
            outPtr = outPtr2;
        }
        while (outPtr < len) {
            int ch;
            int ptr = this._ptr;
            if (this.mBigEndian) {
                ch = (((this._buffer[ptr] << 24) | ((this._buffer[ptr + 1] & 255) << 16)) | ((this._buffer[ptr + 2] & 255) << 8)) | (this._buffer[ptr + 3] & 255);
            } else {
                ch = (((this._buffer[ptr] & 255) | ((this._buffer[ptr + 1] & 255) << 8)) | ((this._buffer[ptr + 2] & 255) << 16)) | (this._buffer[ptr + 3] << 24);
            }
            this._ptr += 4;
            if (ch > 65535) {
                if (ch > 1114111) {
                    reportInvalid(ch, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
                }
                ch -= AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR;
                outPtr2 = outPtr + 1;
                cbuf[outPtr] = (char) (55296 + (ch >> 10));
                ch = 56320 | (ch & 1023);
                if (outPtr2 >= len) {
                    this.mSurrogate = (char) ch;
                    break;
                }
            }
            outPtr2 = outPtr;
            outPtr = outPtr2 + 1;
            cbuf[outPtr2] = (char) ch;
            if (this._ptr >= this._length) {
                outPtr2 = outPtr;
                break;
            }
        }
        outPtr2 = outPtr;
        len = outPtr2 - start;
        this.mCharCount += len;
        return len;
    }

    private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
        throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + this.mCharCount + ", byte #" + (this.mByteCount + gotBytes) + ")");
    }

    private void reportInvalid(int value, int offset, String msg) throws IOException {
        throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + (this.mCharCount + offset) + ", byte #" + ((this.mByteCount + this._ptr) - 1) + ")");
    }

    private boolean loadMore(int available) throws IOException {
        int count;
        this.mByteCount += this._length - available;
        if (available > 0) {
            if (this._ptr > 0) {
                for (int i = 0; i < available; i++) {
                    this._buffer[i] = this._buffer[this._ptr + i];
                }
                this._ptr = 0;
            }
            this._length = available;
        } else {
            this._ptr = 0;
            count = this._in.read(this._buffer);
            if (count < 1) {
                this._length = 0;
                if (count < 0) {
                    freeBuffers();
                    return false;
                }
                reportStrangeStream();
            }
            this._length = count;
        }
        while (this._length < 4) {
            count = this._in.read(this._buffer, this._length, this._buffer.length - this._length);
            if (count < 1) {
                if (count < 0) {
                    freeBuffers();
                    reportUnexpectedEOF(this._length, 4);
                }
                reportStrangeStream();
            }
            this._length += count;
        }
        return true;
    }
}
