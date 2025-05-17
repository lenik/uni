package net.bodz.bas.stat.distrib;

import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;

import net.bodz.bas.proxy.java.util.DecoratedMap;
import net.bodz.bas.repr.form.SortOrder;

public class BigDatasetStats<K>
        extends DecoratedMap<K, Long>
        implements
            ICountDistribMap<K, Long> {

    private static final long serialVersionUID = 1L;

    long total;

    public BigDatasetStats() {
        this(SortOrder.NONE);
    }

    public BigDatasetStats(SortOrder sortOrder) {
        this(sortOrder.newMapDefault());
    }

    public BigDatasetStats(int lruSize) {
        this(new LRUMap<>(lruSize));
    }

    @SuppressWarnings("serial")
    public BigDatasetStats(int lruSize, int maxCountToDrop) {
        this(new LRUMap<K, Long>(lruSize) {
            @Override
            protected boolean removeLRU(LinkEntry<K, Long> entry) {
                long count = entry.getValue();
                return count <= maxCountToDrop;
            }
        });
    }

    public BigDatasetStats(Map<K, Long> _orig) {
        super(_orig);
    }

    @Override
    public long getTotalCount() {
        return total;
    }

    public synchronized long add(K key, long delta) {
        total += delta;

        Long prev = putIfAbsent(key, delta);
        if (prev == null)
            return delta;
        long now = prev.longValue() + delta;
        if (now == 0)
            remove(key);
        else
            put(key, now);
        return now;
    }

    public long add(K key) {
        return add(key);
    }

    public long del(K key) {
        return add(key, -1L);
    }

    @Override
    public long count(K key) {
        Long val = get(key);
        return val == null ? 0L : val.longValue();
    }

    @Override
    public final void merge(ICountDistribMap<K, Long> other) {
        merge((BigDatasetStats<K>) other);
    }

    protected void merge(BigDatasetStats<K> o) {
        for (K k : o.keySet()) {
            Long n = o.get(k);
            add(k, n.longValue());
        }
    }

}
