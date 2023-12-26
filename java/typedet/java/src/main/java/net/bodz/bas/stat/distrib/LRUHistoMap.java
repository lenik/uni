package net.bodz.bas.stat.distrib;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.Stdio;

public class LRUHistoMap<K extends Comparable<K>>
        extends SmallDatasetStats<K>
        implements
            IUpdateTimeTracker<K> {

    private static final long serialVersionUID = 1L;

    int maxSize;
    TrackedMap<K, Integer> core;
    TreeSet<K> _boundKeys;

    public LRUHistoMap(int maxSize) {
        super((Map<K, Integer>) null);
        core = new TrackedMap<>();
        _orig = core;

        this.maxSize = maxSize;
        _boundKeys = new TreeSet<>(new Order());
    }

    @Override
    public long getUpdateTime(K o) {
        return core.getUpdateTime(o);
    }

    class Order
            extends WeightOrder<K> {

        @Override
        protected long getHisto(K k) {
            return count(k);
        }

        @Override
        protected long getUpdateTime(K k) {
            return core.getUpdateTime(k);
        }

    }

    protected boolean isFull() {
        return size() >= maxSize;
    }

    @Override
    public synchronized Integer put(K key, Integer value) {
        if (containsKey(key))
            _boundKeys.remove(key);
        else
            while (isFull()) {
                K head = _boundKeys.iterator().next();
                remove(head);
            }
        Integer oldValue = super.put(key, value);
        _boundKeys.add(key);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Integer> m) {
        _boundKeys.removeAll(m.keySet());
        super.putAll(m);
        _boundKeys.addAll(m.keySet());
    }

    @Override
    public void clear() {
        _boundKeys.clear();
        super.clear();
    }

    @Override
    public Integer remove(Object key) {
        _boundKeys.remove(key);
        return super.remove(key);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super Integer, ? extends Integer> function) {
        Set<K> keys = keySet();
        _boundKeys.removeAll(keys);
        super.replaceAll(function);
        _boundKeys.addAll(keys);
    }

    @Override
    public int hashCode() {
        return getWrapped().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getWrapped().equals(obj);
    }

    @Override
    public String toString() {
        return getWrapped().toString();
    }

    public void dump() {
        dump(Stdio.cout);
    }

    public void dump(IPrintOut out) {
        int i = 0;
        out.printf("[%d]", size());
        out.print("{");

        // List<K> bOnly = new ArrayList<>(_boundKeys);
        Set<K> cOnly = new LinkedHashSet<>(keySet());
        cOnly.removeAll(_boundKeys);
        // bOnly.removeAll(cOnly);
        List<K> all = new ArrayList<>(_boundKeys);
        all.addAll(cOnly);

        for (K k : all) {
            if (i++ != 0)
                out.print(", ");
            if (cOnly.contains(k))
                out.print("cOnly+");
            if (!containsKey(k)) // b-only
                out.print("bOnly-");
            _dumpEntry(out, k);
        }
        out.println("}");
    }

    void _dumpEntry(IPrintOut out, K k) {
        Integer val = get(k);
        long time = core.getUpdateTime(k);
        out.printf("%s%s_%d", k, //
                Strings.repeat(val - 1, '+'), //
                time);
    }

}
