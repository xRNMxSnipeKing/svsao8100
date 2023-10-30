package org.simpleframework.xml.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.filter.Filter;
import org.simpleframework.xml.filter.PlatformFilter;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeBuilder;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;
import org.simpleframework.xml.transform.Matcher;

public class Persister implements Serializer {
    private final Format format;
    private final SessionManager manager;
    private final Strategy strategy;
    private final Style style;
    private final Support support;

    public Persister() {
        this(new HashMap());
    }

    public Persister(Format format) {
        this(new TreeStrategy(), format);
    }

    public Persister(Map filter) {
        this(new PlatformFilter(filter));
    }

    public Persister(Map filter, Format format) {
        this(new PlatformFilter(filter));
    }

    public Persister(Filter filter) {
        this(new TreeStrategy(), filter);
    }

    public Persister(Filter filter, Format format) {
        this(new TreeStrategy(), filter, format);
    }

    public Persister(Matcher matcher) {
        this(new TreeStrategy(), matcher);
    }

    public Persister(Matcher matcher, Format format) {
        this(new TreeStrategy(), matcher, format);
    }

    public Persister(Strategy strategy) {
        this(strategy, new HashMap());
    }

    public Persister(Strategy strategy, Format format) {
        this(strategy, new HashMap(), format);
    }

    public Persister(Filter filter, Matcher matcher) {
        this(new TreeStrategy(), filter, matcher);
    }

    public Persister(Filter filter, Matcher matcher, Format format) {
        this(new TreeStrategy(), filter, matcher, format);
    }

    public Persister(Strategy strategy, Map data) {
        this(strategy, new PlatformFilter(data));
    }

    public Persister(Strategy strategy, Map data, Format format) {
        this(strategy, new PlatformFilter(data), format);
    }

    public Persister(Strategy strategy, Filter filter) {
        this(strategy, filter, new Format());
    }

    public Persister(Strategy strategy, Filter filter, Format format) {
        this(strategy, filter, new EmptyMatcher(), format);
    }

    public Persister(Strategy strategy, Matcher matcher) {
        this(strategy, new PlatformFilter(), matcher);
    }

    public Persister(Strategy strategy, Matcher matcher, Format format) {
        this(strategy, new PlatformFilter(), matcher, format);
    }

    public Persister(Strategy strategy, Filter filter, Matcher matcher) {
        this(strategy, filter, matcher, new Format());
    }

    public Persister(Strategy strategy, Filter filter, Matcher matcher, Format format) {
        this.support = new Support(filter, matcher);
        this.manager = new SessionManager();
        this.style = format.getStyle();
        this.strategy = strategy;
        this.format = format;
    }

    public <T> T read(Class<? extends T> type, String source) throws Exception {
        return read((Class) type, source, true);
    }

    public <T> T read(Class<? extends T> type, File source) throws Exception {
        return read((Class) type, source, true);
    }

    public <T> T read(Class<? extends T> type, InputStream source) throws Exception {
        return read((Class) type, source, true);
    }

    public <T> T read(Class<? extends T> type, Reader source) throws Exception {
        return read((Class) type, source, true);
    }

    public <T> T read(Class<? extends T> type, InputNode source) throws Exception {
        return read((Class) type, source, true);
    }

    public <T> T read(Class<? extends T> type, String source, boolean strict) throws Exception {
        return read((Class) type, new StringReader(source), strict);
    }

    public <T> T read(Class<? extends T> type, File source, boolean strict) throws Exception {
        InputStream file = new FileInputStream(source);
        try {
            T read = read((Class) type, file, strict);
            return read;
        } finally {
            file.close();
        }
    }

    public <T> T read(Class<? extends T> type, InputStream source, boolean strict) throws Exception {
        return read((Class) type, NodeBuilder.read(source), strict);
    }

    public <T> T read(Class<? extends T> type, Reader source, boolean strict) throws Exception {
        return read((Class) type, NodeBuilder.read(source), strict);
    }

    public <T> T read(Class<? extends T> type, InputNode node, boolean strict) throws Exception {
        try {
            T read = read((Class) type, node, this.manager.open(strict));
            return read;
        } finally {
            this.manager.close();
        }
    }

    private <T> T read(Class<? extends T> type, InputNode node, Session session) throws Exception {
        return read((Class) type, node, new Source(this.strategy, this.support, this.style, session));
    }

    private <T> T read(Class<? extends T> type, InputNode node, Context context) throws Exception {
        return new Traverser(context).read(node, (Class) type);
    }

    public <T> T read(T value, String source) throws Exception {
        return read((Object) value, source, true);
    }

    public <T> T read(T value, File source) throws Exception {
        return read((Object) value, source, true);
    }

    public <T> T read(T value, InputStream source) throws Exception {
        return read((Object) value, source, true);
    }

    public <T> T read(T value, Reader source) throws Exception {
        return read((Object) value, source, true);
    }

    public <T> T read(T value, InputNode source) throws Exception {
        return read((Object) value, source, true);
    }

    public <T> T read(T value, String source, boolean strict) throws Exception {
        return read((Object) value, new StringReader(source), strict);
    }

    public <T> T read(T value, File source, boolean strict) throws Exception {
        InputStream file = new FileInputStream(source);
        try {
            T read = read((Object) value, file, strict);
            return read;
        } finally {
            file.close();
        }
    }

    public <T> T read(T value, InputStream source, boolean strict) throws Exception {
        return read((Object) value, NodeBuilder.read(source), strict);
    }

    public <T> T read(T value, Reader source, boolean strict) throws Exception {
        return read((Object) value, NodeBuilder.read(source), strict);
    }

    public <T> T read(T value, InputNode node, boolean strict) throws Exception {
        try {
            T read = read((Object) value, node, this.manager.open(strict));
            return read;
        } finally {
            this.manager.close();
        }
    }

    private <T> T read(T value, InputNode node, Session session) throws Exception {
        return read((Object) value, node, new Source(this.strategy, this.support, this.style, session));
    }

    private <T> T read(T value, InputNode node, Context context) throws Exception {
        return new Traverser(context).read(node, (Object) value);
    }

    public boolean validate(Class type, String source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, File source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, InputStream source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, Reader source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, InputNode source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, String source, boolean strict) throws Exception {
        return validate(type, new StringReader(source), strict);
    }

    public boolean validate(Class type, File source, boolean strict) throws Exception {
        InputStream file = new FileInputStream(source);
        try {
            boolean validate = validate(type, file, strict);
            return validate;
        } finally {
            file.close();
        }
    }

    public boolean validate(Class type, InputStream source, boolean strict) throws Exception {
        return validate(type, NodeBuilder.read(source), strict);
    }

    public boolean validate(Class type, Reader source, boolean strict) throws Exception {
        return validate(type, NodeBuilder.read(source), strict);
    }

    public boolean validate(Class type, InputNode node, boolean strict) throws Exception {
        try {
            boolean validate = validate(type, node, this.manager.open(strict));
            return validate;
        } finally {
            this.manager.close();
        }
    }

    private boolean validate(Class type, InputNode node, Session session) throws Exception {
        return validate(type, node, new Source(this.strategy, this.support, this.style, session));
    }

    private boolean validate(Class type, InputNode node, Context context) throws Exception {
        return new Traverser(context).validate(node, type);
    }

    public void write(Object source, OutputNode root) throws Exception {
        try {
            write(source, root, this.manager.open());
        } finally {
            this.manager.close();
        }
    }

    private void write(Object source, OutputNode root, Session session) throws Exception {
        write(source, root, new Source(this.strategy, this.support, this.style, session));
    }

    private void write(Object source, OutputNode node, Context context) throws Exception {
        new Traverser(context).write(node, source);
    }

    public void write(Object source, File out) throws Exception {
        OutputStream file = new FileOutputStream(out);
        try {
            write(source, file);
        } finally {
            file.close();
        }
    }

    public void write(Object source, OutputStream out) throws Exception {
        write(source, out, "utf-8");
    }

    public void write(Object source, OutputStream out, String charset) throws Exception {
        write(source, new OutputStreamWriter(out, charset));
    }

    public void write(Object source, Writer out) throws Exception {
        write(source, NodeBuilder.write(out, this.format));
    }
}
