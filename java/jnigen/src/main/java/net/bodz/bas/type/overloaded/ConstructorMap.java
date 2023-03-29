package net.bodz.bas.type.overloaded;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;

public class ConstructorMap<T>
        extends LinkedHashMap<String, Constructor<T>> {

    private static final long serialVersionUID = 1L;

    DistinguishableNaming naming;

    public ConstructorMap() {
        this(SortOrder.KEEP);
    }

    public ConstructorMap(SortOrder order) {
        this(order.newMap());
    }

    public ConstructorMap(Map<String, Constructor<T>> map) {
        super(map);
    }

    public DistinguishableNaming getNaming() {
        return naming;
    }

    public void setNaming(DistinguishableNaming naming) {
        this.naming = naming;
    }

}
