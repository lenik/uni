package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class LongStats
        extends SmallDatasetStats<Long> {

    private static final long serialVersionUID = 1L;

    long min = Long.MAX_VALUE;
    long max = Long.MIN_VALUE;

    public LongStats() {
        super();
    }

    public LongStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public LongStats(Map<Long, Integer> _orig) {
        super(_orig);
    }

    public LongStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    @Override
    public Integer put(Long key, Integer value) {
        // int nOccurs = value.intValue();
        long val = key.longValue();
        if (val < min)
            min = val;
        if (val > max)
            max = val;
        return super.put(key, value);
    }

    @Override
    protected final void merge(SmallDatasetStats<Long> o) {
        super.merge(o);
        merge((LongStats) o);
    }

    protected void merge(LongStats o) {
        if (o.min < min)
            min = o.min;
        if (max < o.max)
            max = o.max;
    }

}
