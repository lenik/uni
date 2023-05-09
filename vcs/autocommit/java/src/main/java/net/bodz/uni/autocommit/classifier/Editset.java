package net.bodz.uni.autocommit.classifier;

import java.util.TreeMap;

public class Editset
        extends TreeMap<EditType, EditElements> {

    private static final long serialVersionUID = 1L;

    public Editset() {
        super(EditTypeComparator.INSTANCE);
    }

    public EditElements lazyCreate(EditType type) {
        if (type == null)
            throw new NullPointerException("type");
        EditElements elements = get(type);
        if (elements == null)
            put(type, elements = new EditElements());
        return elements;
    }

}