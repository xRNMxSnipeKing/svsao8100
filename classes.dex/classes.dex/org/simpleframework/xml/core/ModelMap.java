package org.simpleframework.xml.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.simpleframework.xml.stream.Style;

class ModelMap extends LinkedHashMap<String, ModelList> implements Iterable<ModelList> {
    public Model lookup(String name, int index) {
        ModelList list = (ModelList) get(name);
        if (list != null) {
            return list.lookup(index);
        }
        return null;
    }

    public void register(String name, Model model) {
        ModelList list = (ModelList) get(name);
        if (list == null) {
            list = new ModelList();
            put(name, list);
        }
        list.register(model);
    }

    public Iterator<ModelList> iterator() {
        return values().iterator();
    }

    public ModelMap build(Context context) throws Exception {
        Style style = context.getStyle();
        if (style != null) {
            return build(style);
        }
        return this;
    }

    private ModelMap build(Style style) throws Exception {
        ModelMap map = new ModelMap();
        for (String element : keySet()) {
            ModelList list = (ModelList) get(element);
            String name = style.getElement(element);
            if (list != null) {
                list = list.build();
            }
            map.put(name, list);
        }
        return map;
    }
}
