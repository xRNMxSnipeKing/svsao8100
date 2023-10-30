package com.microsoft.xbox.toolkit;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

public class XMLHelper {
    private static final int XML_WAIT_TIMEOUT_MS = 1000;
    private static XMLHelper instance = new XMLHelper();
    private Serializer serializer;

    public static XMLHelper instance() {
        return instance;
    }

    private XMLHelper() {
        this.serializer = null;
        this.serializer = new Persister(new AnnotationStrategy());
    }

    public <T> T load(InputStream input, Class<T> type) throws XLEException {
        if (ThreadManager.UIThread != Thread.currentThread()) {
            BackgroundThreadWaitor.getInstance().waitForReady(1000);
        } else {
            XLELog.Error("XMLHelper", "Parsing xml is not recommended on the UI thread. Make sure you have a clear justification for doing this.");
        }
        TimeMonitor stopwatch = new TimeMonitor();
        ClassLoader clsLoader;
        try {
            clsLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(type.getClassLoader());
            T rv = this.serializer.read((Class) type, input, false);
            Thread.currentThread().setContextClassLoader(clsLoader);
            return rv;
        } catch (Exception e) {
            XLELog.Error("Deserialization", "Error deserializing " + e.toString());
            throw new XLEException(8, e.toString());
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(clsLoader);
        }
    }

    public <T> String save(T output) throws XLEException {
        TimeMonitor stopwatch = new TimeMonitor();
        Writer writer = new StringWriter();
        try {
            this.serializer.write((Object) output, writer);
            return writer.toString();
        } catch (Exception e) {
            XLELog.Error("Serialization", "Error serializing " + e.toString());
            throw new XLEException(8, e.toString());
        }
    }

    public <T> void save(T output, OutputStream outStream) throws XLEException {
        TimeMonitor stopwatch = new TimeMonitor();
        try {
            this.serializer.write((Object) output, outStream);
        } catch (Exception e) {
            XLELog.Warning("Serialization", "Error serializing " + e.toString());
            throw new XLEException(8, e.toString());
        }
    }
}
