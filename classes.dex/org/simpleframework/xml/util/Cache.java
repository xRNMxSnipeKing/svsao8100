package org.simpleframework.xml.util;

public interface Cache<K, V> {
    void cache(K k, V v);

    boolean contains(K k);

    V fetch(K k);

    V take(K k);
}
