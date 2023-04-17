package net.bodz.lily.tool.javagen;

import net.bodz.bas.t.order.AbstractNonNullComparator;

public class SeqByPassComparator
        extends AbstractNonNullComparator<SeqByPass> {

    @Override
    public int compareNonNull(SeqByPass o1, SeqByPass o2) {
        int cmp = o1.pass - o2.pass;
        if (cmp != 0)
            return cmp;
        cmp = o1.seq - o2.seq;
        return cmp;
    }

    public static final SeqByPassComparator INSTANCE = new SeqByPassComparator();

}
