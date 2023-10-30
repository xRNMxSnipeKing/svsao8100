package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class AnyType {
    @Attribute(required = false)
    @Namespace(prefix = "xsi", reference = "http://www.w3.org/2001/XMLSchema-instance")
    public String type;
    @Text(required = false)
    public String value;

    public AnyType(String value, String type) {
        this.value = value;
        this.type = type;
    }
}
