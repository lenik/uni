package net.bodz.bas.stat.distrib.type;

import java.util.Map;
import java.util.TreeMap;

import net.bodz.bas.repr.form.SortOrder;
import net.bodz.bas.stat.distrib.SmallDatasetStats;

public abstract class AbstractStructStats
        extends SmallDatasetStats<String> {

    private static final long serialVersionUID = 1L;

    IntStats lenStats = new IntStats();

    Map<Integer, StringStats> keyLevels = new TreeMap<>();
    StructConfig config = StructConfig.DEFAULT;

    public AbstractStructStats() {
        super();
    }

    public AbstractStructStats(SortOrder sortOrder) {
        super(sortOrder);
    }

    public AbstractStructStats(int lruSize) {
        super(lruSize);
    }

    public AbstractStructStats(int lruSize, int maxCountToDrop) {
        super(lruSize, maxCountToDrop);
    }

    public AbstractStructStats(Map<String, Integer> _orig) {
        super(_orig);
    }

    protected StringStats getKeyStats(int level) {
        StringStats keyStats = keyLevels.get(level);
        if (keyStats == null && level <= config.maxLevel) {
            keyStats = new StringStats(SortOrder.KEEP);
            keyLevels.put(level, keyStats);
        }
        return keyStats;
    }

    @Override
    public Integer put(String key, Integer value) {
        lenStats.add(key.length());

        structAnalyze(key);

        return super.put(key, value);
    }

    protected abstract void structAnalyze(String s);

    @Override
    protected final void merge(SmallDatasetStats<String> o) {
        super.merge(o);
        merge((AbstractStructStats) o);
    }

    protected void merge(AbstractStructStats o) {
        lenStats.merge(o.lenStats);

        for (Integer _level : o.keyLevels.keySet()) {
            int level = _level.intValue();
            StringStats keyStats = getKeyStats(level);
            StringStats oKeyStats = o.getKeyStats(level);
            if (keyStats != null && oKeyStats != null)
                keyStats.merge(oKeyStats);
        }
    }

}
