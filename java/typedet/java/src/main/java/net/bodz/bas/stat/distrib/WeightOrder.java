package net.bodz.bas.stat.distrib;

import net.bodz.bas.t.order.AbstractNonNullComparator;

public abstract class WeightOrder<K extends Comparable<K>>
        extends AbstractNonNullComparator<K> {

    protected abstract long getHisto(K k);

    protected abstract long getUpdateTime(K k);

    @Override
    public int compareNonNull(K o1, K o2) {
        long n1 = getHisto(o1);
        long n2 = getHisto(o2);
        int cmp = Long.compare(n1, n2);
        if (cmp != 0)
            return cmp; // from light to heavy

        long t1 = getUpdateTime(o1);
        long t2 = getUpdateTime(o2);
        if (t1 == 0)
            return -1;
        if (t2 == 0)
            return 1;

        cmp = Long.compare(t1, t2);
//        if (cmp != 0)
        return cmp; // from old to new
    }

}
