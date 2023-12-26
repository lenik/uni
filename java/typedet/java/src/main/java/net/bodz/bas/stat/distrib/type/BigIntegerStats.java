package net.bodz.bas.stat.distrib.type;

import java.math.BigInteger;

import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class BigIntegerStats
        extends SmallDatasetStats<BigInteger> {

    private static final long serialVersionUID = 1L;

    BigInteger min;
    BigInteger max;

    @Override
    public Integer put(BigInteger key, Integer value) {
        if (min == null || key.compareTo(min) < 0)
            min = key;
        if (max == null || key.compareTo(max) > 0)
            max = key;
        return super.put(key, value);
    }

    @Override
    protected final void merge(SmallDatasetStats<BigInteger> o) {
        super.merge(o);
        merge((BigIntegerStats) o);
    }

    protected void merge(BigIntegerStats o) {
        if (min == null || o.min.compareTo(min) < 0)
            min = o.min;
        if (max == null || o.max.compareTo(max) > 0)
            max = o.max;
    }

}
