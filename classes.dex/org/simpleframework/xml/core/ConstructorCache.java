package org.simpleframework.xml.core;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;

class ConstructorCache extends ConcurrentHashMap<Class, Constructor> {
}
