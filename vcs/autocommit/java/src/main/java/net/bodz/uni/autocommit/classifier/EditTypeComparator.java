package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.t.order.AbstractNonNullComparator;

public class EditTypeComparator
        extends AbstractNonNullComparator<EditType> {

    @Override
    public int compareNonNull(EditType o1, EditType o2) {
        int cmp = o1.getPriority() - o2.getPriority();
        if (cmp != 0)
            return cmp;
        cmp = o1.ordinal() - o2.ordinal();
        return cmp;
    }

    public static final EditTypeComparator INSTANCE = new EditTypeComparator();

}
