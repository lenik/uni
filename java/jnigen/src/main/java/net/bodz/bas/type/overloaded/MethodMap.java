package net.bodz.bas.type.overloaded;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;

public class MethodMap
        extends LinkedHashMap<String, Method> {

    private static final long serialVersionUID = 1L;

    DistinguishableNaming naming;

    public MethodMap() {
        this(SortOrder.KEEP);
    }

    public MethodMap(SortOrder order) {
        this(order.newMap());
    }

    public MethodMap(Map<String, Method> map) {
        super(map);
    }

    public DistinguishableNaming getNaming() {
        return naming;
    }

    public void setNaming(DistinguishableNaming naming) {
        this.naming = naming;
    }

}
