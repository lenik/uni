package net.bodz.uni.autocommit.classifier;

import java.util.TreeSet;

public class EditElements
        extends TreeSet<EditElement> {

    private static final long serialVersionUID = 1L;

    public EditElements() {
        super(EditElementComparator.INSTANCE);
    }

}
