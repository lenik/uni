package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.stat.distrib.BigDatasetStats;

public class BigStringStats
        extends BigDatasetStats<String> {

    private static final long serialVersionUID = 1L;

    String min;
    String max;

    IntStats lenStats;
    CharStats firstCharStats;
    CharStats lastCharStats;

    public BigStringStats() {
        super();
    }

    public BigStringStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public BigStringStats(Map<String, Long> _orig) {
        super(_orig);
    }

    public BigStringStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    @Override
    public Long put(String key, Long value) {
        if (min == null || key.compareTo(min) < 0)
            min = key;
        if (max == null || key.compareTo(max) > 0)
            max = key;
        return super.put(key, value);
    }

    @Override
    protected final void merge(BigDatasetStats<String> o) {
        super.merge(o);
        merge((BigStringStats) o);
    }

    protected void merge(BigStringStats o) {
        if (min == null || o.min.compareTo(min) < 0)
            min = o.min;
        if (max == null || o.max.compareTo(max) > 0)
            max = o.max;
    }

}
