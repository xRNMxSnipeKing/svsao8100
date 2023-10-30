package org.simpleframework.xml.core;

interface Criteria extends Iterable<String> {
    void commit(Object obj) throws Exception;

    Variable get(String str);

    Variable remove(String str) throws Exception;

    Variable resolve(String str);

    void set(Label label, Object obj) throws Exception;
}
