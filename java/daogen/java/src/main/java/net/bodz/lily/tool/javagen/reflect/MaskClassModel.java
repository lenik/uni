package net.bodz.lily.tool.javagen.reflect;

import java.util.HashMap;
import java.util.Map;

public class MaskClassModel {

    final Class<?> declaringClass;
    final Map<String, MaskFieldModel> fields = new HashMap<>();

    public MaskClassModel(Class<?> declaringClass) {
        if (declaringClass == null)
            throw new NullPointerException("declaringClass");
        this.declaringClass = declaringClass;
    }

    public Map<String, MaskFieldModel> getFieldMap() {
        return fields;
    }

    public MaskFieldModel getField(String name) {
        return fields.get(name);
    }

    public void setField(String name, MaskFieldModel model) {
        fields.put(name, model);
    }

}
