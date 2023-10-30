package com.microsoft.xbox.toolkit;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;

public class FileStorageManager {
    private static FileStorageManager instance = new FileStorageManager();

    public static FileStorageManager getInstance() {
        return instance;
    }

    public <T> T readFromFile(Context context, String fileName, Class<T> type) throws XLEException {
        try {
            FileInputStream fileStream = context.openFileInput(fileName);
            T objRaw = null;
            if (fileStream != null) {
                objRaw = XMLHelper.instance().load(fileStream, type);
            }
            if (objRaw != null) {
                return objRaw;
            }
            context.deleteFile(fileName);
            return null;
        } catch (Throwable e) {
            throw new XLEException(16, e);
        }
    }

    public boolean deleteFile(Context context, String fileName) {
        File file = context.getFileStreamPath(fileName);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public <T> boolean saveToFile(Context context, String fileName, T rawObj) throws XLEException {
        if (rawObj == null) {
            return false;
        }
        try {
            XMLHelper.instance().save(rawObj, context.openFileOutput(fileName, 0));
            return true;
        } catch (Throwable e) {
            throw new XLEException(17, e);
        }
    }
}
