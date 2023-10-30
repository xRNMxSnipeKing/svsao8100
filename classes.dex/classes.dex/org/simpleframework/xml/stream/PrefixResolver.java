package org.simpleframework.xml.stream;

import java.util.Iterator;
import java.util.LinkedHashMap;

class PrefixResolver extends LinkedHashMap<String, String> implements NamespaceMap {
    private final OutputNode source;

    public PrefixResolver(OutputNode source) {
        this.source = source;
    }

    public String getPrefix() {
        return this.source.getPrefix();
    }

    public String setReference(String reference) {
        return setReference(reference, "");
    }

    public String setReference(String reference, String prefix) {
        if (resolvePrefix(reference) != null) {
            return null;
        }
        return (String) put(reference, prefix);
    }

    public String getPrefix(String reference) {
        if (size() > 0) {
            String prefix = (String) get(reference);
            if (prefix != null) {
                return prefix;
            }
        }
        return resolvePrefix(reference);
    }

    public String getReference(String prefix) {
        if (containsValue(prefix)) {
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                String reference = (String) i$.next();
                String value = (String) get(reference);
                if (value != null && value.equals(prefix)) {
                    return reference;
                }
            }
        }
        return resolveReference(prefix);
    }

    private String resolveReference(String prefix) {
        NamespaceMap parent = this.source.getNamespaces();
        if (parent != null) {
            return parent.getReference(prefix);
        }
        return null;
    }

    private String resolvePrefix(String reference) {
        NamespaceMap parent = this.source.getNamespaces();
        if (parent != null) {
            String prefix = parent.getPrefix(reference);
            if (!containsValue(prefix)) {
                return prefix;
            }
        }
        return null;
    }

    public Iterator<String> iterator() {
        return keySet().iterator();
    }
}
