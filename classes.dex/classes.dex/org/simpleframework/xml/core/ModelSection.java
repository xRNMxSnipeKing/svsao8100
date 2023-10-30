package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.simpleframework.xml.stream.Style;

class ModelSection implements Section {
    private LabelMap attributes;
    private Context context;
    private LabelMap elements;
    private Model model;
    private ModelMap models;
    private Style style;

    public ModelSection(Context context, Model model) {
        this.style = context.getStyle();
        this.context = context;
        this.model = model;
    }

    public String getName() {
        return this.model.getName();
    }

    public String getPrefix() {
        return this.model.getPrefix();
    }

    public Iterator<String> iterator() {
        List<String> list = new ArrayList();
        for (String element : this.model) {
            String name = this.style.getElement(element);
            if (name != null) {
                list.add(name);
            }
        }
        return list.iterator();
    }

    public boolean isSection(String name) throws Exception {
        return getModels().get(name) != null;
    }

    public ModelMap getModels() throws Exception {
        if (this.models == null) {
            this.models = this.model.buildModels(this.context);
        }
        return this.models;
    }

    public LabelMap getAttributes() throws Exception {
        if (this.attributes == null) {
            this.attributes = this.model.buildAttributes(this.context);
        }
        return this.attributes;
    }

    public LabelMap getElements() throws Exception {
        if (this.elements == null) {
            this.elements = this.model.buildElements(this.context);
        }
        return this.elements;
    }

    public Label getElement(String name) throws Exception {
        return getElements().take(name);
    }

    public Section getSection(String name) throws Exception {
        ModelList list = (ModelList) getModels().get(name);
        if (list != null) {
            Model model = list.take();
            if (model != null) {
                return new ModelSection(this.context, model);
            }
        }
        return null;
    }
}
