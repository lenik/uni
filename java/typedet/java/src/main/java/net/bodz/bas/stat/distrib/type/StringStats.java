package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class StringStats
        extends SmallDatasetStats<String> {

    private static final long serialVersionUID = 1L;

    String min;
    String max;

    IntStats lenStats;
    CharStats firstCharStats;
    CharStats lastCharStats;

    public StringStats() {
        super();
    }

    public StringStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public StringStats(Map<String, Integer> _orig) {
        super(_orig);
    }

    public StringStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    @Override
    public Integer put(String key, Integer value) {
        if (min == null || key.compareTo(min) < 0)
            min = key;
        if (max == null || key.compareTo(max) > 0)
            max = key;
        return super.put(key, value);
    }

    @Override
    protected final void merge(SmallDatasetStats<String> o) {
        super.merge(o);
        merge((StringStats) o);
    }

    protected void merge(StringStats o) {
        if (min == null || o.min.compareTo(min) < 0)
            min = o.min;
        if (max == null || o.max.compareTo(max) > 0)
            max = o.max;
    }

}
