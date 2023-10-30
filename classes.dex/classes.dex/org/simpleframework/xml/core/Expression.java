package org.simpleframework.xml.core;

interface Expression extends Iterable<String> {
    String getFirst();

    int getIndex();

    String getLast();

    Expression getPath(int i);

    Expression getPath(int i, int i2);

    String getPrefix();

    boolean isAttribute();

    boolean isPath();

    String toString();
}
