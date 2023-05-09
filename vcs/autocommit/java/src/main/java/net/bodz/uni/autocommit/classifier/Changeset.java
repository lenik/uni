package net.bodz.uni.autocommit.classifier;

import java.util.TreeMap;

public class Changeset
        extends TreeMap<ChangeGroup, EditElements> {

    private static final long serialVersionUID = 1L;

    public Changeset() {
        super(ChangeGroupComparator.INSTANCE);
    }

    public EditElements lazyCreate(ChangeGroup group) {
        EditElements elements = get(group);
        if (elements == null) {
            elements = new EditElements();
            put(group, elements);
        }
        return elements;
    }

}
