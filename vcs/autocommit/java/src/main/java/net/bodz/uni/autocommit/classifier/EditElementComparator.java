package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.t.order.AbstractNonNullComparator;

public class EditElementComparator
        extends AbstractNonNullComparator<EditElement> {

    @Override
    public int compareNonNull(EditElement o1, EditElement o2) {
        int cmp = o1.getPriority() - o2.getPriority();
        if (cmp != 0)
            return cmp;
        cmp = o1.getName().compareTo(o2.getName());
        if (cmp != 0)
            return cmp;
        cmp = o1.getPath().compareTo(o2.getPath());
        return cmp;
    }

    public static final EditElementComparator INSTANCE = new EditElementComparator();

}
