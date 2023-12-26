package net.bodz.bas.stat.distrib.type;

import java.util.Map;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public class CharStats
        extends SmallDatasetStats<Character> {

    private static final long serialVersionUID = 1L;

    char min = Character.MAX_VALUE;
    char max = Character.MIN_VALUE;

    public CharStats() {
        super();
    }

    public CharStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public CharStats(Map<Character, Integer> _orig) {
        super(_orig);
    }

    public CharStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    @Override
    public Integer put(Character key, Integer value) {
        // int nOccurs = value.intValue();
        char val = key.charValue();
        if (val < min)
            min = val;
        if (val > max)
            max = val;
        return super.put(key, value);
    }

    @Override
    protected final void merge(SmallDatasetStats<Character> o) {
        super.merge(o);
        merge((CharStats) o);
    }

    protected void merge(CharStats o) {
        if (o.min < min)
            min = o.min;
        if (max < o.max)
            max = o.max;
    }

}
