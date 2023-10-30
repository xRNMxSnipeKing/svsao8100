package com.microsoft.xbox.service.model.discover;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class ImageUuidConverter implements Converter<String> {
    private String ConvertToImageID(String value) {
        String[] uuidData;
        if (value != null) {
            uuidData = value.split(":");
        } else {
            uuidData = null;
        }
        if (uuidData == null || uuidData.length <= 0) {
            return null;
        }
        return uuidData[uuidData.length - 1];
    }

    public String read(InputNode node) {
        try {
            return ConvertToImageID(node.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    public void write(OutputNode node, String result) {
    }
}
