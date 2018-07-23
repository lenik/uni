package net.bodz.uni.util;

import groovy.lang.Binding;

import java.util.Map;

import javax.script.ScriptException;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.bas.potato.PotatoTypes;
import net.bodz.bas.potato.element.IProperty;
import net.bodz.bas.potato.element.IType;
import net.bodz.bas.potato.ref.PropertyRef;
import net.bodz.bas.t.ref.Ref;

/**
 * Groovy variable binding with reflect fields
 */
public class RefBinding
        extends Binding {

    private Map<?, ?> orig;
    private TextMap<Ref<Object>> accessors;

    public RefBinding() {
        super();
        init();
    }

    public RefBinding(Map<?, ?> variables) {
        super(variables);
        init();
    }

    public RefBinding(String[] args) {
        super(args);
        init();
    }

    private void init() {
        orig = getVariables();
        accessors = new HashTextMap<Ref<Object>>();
    }

    public void addAccessor(String name, Ref<Object> accessor) {
        if (accessors.containsKey(name))
            throw new IllegalArgumentException("accessor " + name + " is already existed, please remove it first");
        accessors.put(name, accessor);
    }

    public boolean removeAccessor(String name) {
        return accessors.remove(name) != null;
    }

    @Override
    public Object getVariable(String name) {
        if (!orig.containsKey(name)) {
            Ref<Object> ref = accessors.get(name);
            if (ref != null)
                return ref.get();
        }
        return super.getVariable(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        if (!orig.containsKey(name)) {
            Ref<Object> ref = accessors.get(name);
            if (ref != null) {
                ref.set(value);
                return;
            }
        }
        super.setVariable(name, value);
    }

    public void bindScriptFields(final Object o, boolean forceAccess)
            throws ScriptException {
        assert o != null;
        Class<?> clazz = o.getClass();

        IType type = PotatoTypes.getInstance().loadType(clazz);

        for (final IProperty property : type.getProperties()) {
            PropertyRef<Object> propertyRef = new PropertyRef<>(o, property);
            addAccessor(property.getName(), propertyRef);
        }
    }

    public void bindScriptFields(final Object o)
            throws ScriptException {
        bindScriptFields(o, false);
    }

}
