package net.bodz.lapiota.util;

import groovy.lang.Binding;

import java.util.Map;

import net.bodz.bas.lang.ref.Ref;
import net.bodz.bas.lang.script.ScriptClass;
import net.bodz.bas.lang.script.ScriptException;
import net.bodz.bas.lang.script.ScriptField;
import net.bodz.bas.lang.script.Scripts;
import net.bodz.bas.types.HashTextMap;
import net.bodz.bas.types.TextMap;

/**
 * Groovy variable binding with reflect fields
 */
public class RefBinding extends Binding {

    private Map<?, ?>            orig;
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
            throw new IllegalArgumentException("accessor " + name
                    + " is already existed, please remove it first");
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

    @SuppressWarnings("unchecked")
    public void bindScriptFields(final Object o, boolean forceAccess)
            throws ScriptException {
        assert o != null;
        Class<?> clazz = o.getClass();
        ScriptClass<Object> sclass = Scripts.convertClass(clazz, forceAccess);
        ScriptField<Object>[] fields = (ScriptField<Object>[]) sclass
                .getFields();
        for (final ScriptField<Object> field : fields) {
            Ref<Object> accessor = new Ref<Object>() {
                @Override
                public Object get() {
                    try {
                        return field.get(o);
                    } catch (ScriptException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

                @Override
                public void set(Object val) {
                    try {
                        field.set(o, val);
                    } catch (ScriptException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            };
            addAccessor(field.getName(), accessor);
        }
    }

    public void bindScriptFields(final Object o) throws ScriptException {
        bindScriptFields(o, false);
    }

}
