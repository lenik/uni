package net.bodz.bas.stat.distrib.type;

import java.math.BigDecimal;

import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class BigDecimalStats
        extends SmallDatasetStats<BigDecimal> {

    private static final long serialVersionUID = 1L;

    BigDecimal min;
    BigDecimal max;

//    IntStats lenStats;
//    IntStats intLenStats;
//    IntStats decLenStats;

    @Override
    public Integer put(BigDecimal key, Integer value) {
        if (min == null || key.compareTo(min) < 0)
            min = key;
        if (max == null || key.compareTo(max) > 0)
            max = key;
        return super.put(key, value);
    }

    @Override
    protected final void merge(SmallDatasetStats<BigDecimal> o) {
        super.merge(o);
        merge((BigDecimalStats) o);
    }

    protected void merge(BigDecimalStats o) {
        if (min == null || o.min.compareTo(min) < 0)
            min = o.min;
        if (max == null || o.max.compareTo(max) > 0)
            max = o.max;
    }

}
