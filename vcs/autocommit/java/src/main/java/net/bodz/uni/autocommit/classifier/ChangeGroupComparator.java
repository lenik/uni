package net.bodz.uni.autocommit.classifier;

import net.bodz.bas.t.order.AbstractNonNullComparator;

public class ChangeGroupComparator
        extends AbstractNonNullComparator<ChangeGroup> {

    @Override
    public int compareNonNull(ChangeGroup o1, ChangeGroup o2) {
        int cmp = o1.getPriority() - o2.getPriority();
        if (cmp != 0)
            return cmp;
        cmp = o1.getName().compareTo(o2.getName());
        return cmp;
    }

    public static final ChangeGroupComparator INSTANCE = new ChangeGroupComparator();

}
