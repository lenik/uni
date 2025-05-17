package net.bodz.bas.stat.distrib;

import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;

import net.bodz.bas.proxy.java.util.DecoratedMap;
import net.bodz.bas.repr.form.SortOrder;

public class SmallDatasetStats<K>
        extends DecoratedMap<K, Integer>
        implements ICountDistribMap<K, Integer> {

    private static final long serialVersionUID = 1L;

    private long total;

    public SmallDatasetStats() {
        this(SortOrder.NONE);
    }

    public SmallDatasetStats(SortOrder sortOrder) {
        this(sortOrder.<K, Integer>newMapDefault());
    }

    public SmallDatasetStats(int lruSize) {
        this(new LRUMap<K, Integer>(lruSize));
    }

    @SuppressWarnings("serial")
    public SmallDatasetStats(int lruSize, int maxCountToDrop) {
        this(new LRUMap<K, Integer>(lruSize) {
            @Override
            protected boolean removeLRU(LinkEntry<K, Integer> entry) {
                int count = entry.getValue();
                return count <= maxCountToDrop;
            }
        });
    }

    public SmallDatasetStats(Map<K, Integer> _orig) {
        super(_orig);
    }

    @Override
    public long getTotalCount() {
        return total;
    }

    public synchronized int add(K key, int delta) {
        total += delta;

        Integer prev = putIfAbsent(key, delta);
        if (prev == null)
            return delta;

        long now = prev.longValue() + delta;
        if (now > Integer.MAX_VALUE)
            now = Integer.MAX_VALUE;
        if (now < Integer.MIN_VALUE)
            now = Integer.MIN_VALUE;

        if (now == 0)
            remove(key);
        else
            put(key, (int) now);
        return (int) now;
    }

    public final int add(K key) {
        return add(key, 1);
    }

    public final int del(K key) {
        return add(key, -1);
    }

    @Override
    public final long count(K key) {
        Integer val = get(key);
        return val == null ? 0L : val.longValue();
    }

    @Override
    public final void merge(ICountDistribMap<K, Integer> other) {
        merge((SmallDatasetStats<K>) other);
    }

    protected void merge(SmallDatasetStats<K> o) {
        for (K k : o.keySet()) {
            Integer n = o.get(k);
            add(k, n);
        }
    }

}
