package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;

class ModelList extends ArrayList<Model> {
    public ModelList build() {
        ModelList list = new ModelList();
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            list.register((Model) i$.next());
        }
        return list;
    }

    public boolean isEmpty() {
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Model model = (Model) i$.next();
            if (model != null && !model.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Model lookup(int index) {
        if (index <= size()) {
            return (Model) get(index - 1);
        }
        return null;
    }

    public void register(Model model) {
        int index = model.getIndex();
        int size = size();
        for (int i = 0; i < index; i++) {
            if (i >= size) {
                add(null);
            }
            if (i == index - 1) {
                set(index - 1, model);
            }
        }
    }

    public Model take() {
        while (!isEmpty()) {
            Model model = (Model) remove(0);
            if (!model.isEmpty()) {
                return model;
            }
        }
        return null;
    }
}
