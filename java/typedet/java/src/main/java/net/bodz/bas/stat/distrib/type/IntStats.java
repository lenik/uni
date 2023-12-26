package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class IntStats
        extends SmallDatasetStats<Integer> {

    private static final long serialVersionUID = 1L;

    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;

    public IntStats() {
        super();
    }

    public IntStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public IntStats(Map<Integer, Integer> _orig) {
        super(_orig);
    }

    public IntStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    @Override
    public Integer put(Integer key, Integer value) {
        // int nOccurs = value.intValue();
        int val = key.intValue();
        if (val < min)
            min = val;
        if (val > max)
            max = val;
        return super.put(key, value);
    }

    @Override
    protected final void merge(SmallDatasetStats<Integer> o) {
        super.merge(o);
        merge((IntStats) o);
    }

    protected void merge(IntStats o) {
        if (o.min < min)
            min = o.min;
        if (max < o.max)
            max = o.max;
    }

}
