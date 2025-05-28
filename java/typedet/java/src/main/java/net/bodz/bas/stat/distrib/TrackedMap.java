package net.bodz.bas.stat.distrib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.bodz.bas.meta.decl.NotNull;
import net.bodz.bas.proxy.java.util.DecoratedMap;
import net.bodz.bas.repr.form.SortOrder;

public class TrackedMap<K, V>
        extends DecoratedMap<K, V>
        implements
            IUpdateTimeTracker<K> {

    private static final long serialVersionUID = 1L;

    Map<K, Long> updateTimeMap = new HashMap<>();
    long time;

    public TrackedMap() {
        this(SortOrder.NONE);
    }

    public TrackedMap(SortOrder sortOrder) {
        super(sortOrder.newMapDefault());
    }

    public TrackedMap(Map<K, V> _orig) {
        super(_orig);
    }

    @Override
    public long getUpdateTime(K o) {
        Long t = updateTimeMap.get(o);
        if (t == null)
            return 0;
        else
            return t.longValue();
    }

    void updateTime(K key) {
        updateTimeMap.put(key, ++time);
    }

    @Override
    public V put(K key, V value) {
        updateTime(key);
        return super.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        updateTime(key);
        return super.putIfAbsent(key, value);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (K k : m.keySet())
            updateTime(k);
        super.putAll(m);
    }

    @Override
    public void clear() {
        updateTimeMap.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        updateTimeMap.remove(key);
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (super.remove(key, value)) {
            updateTimeMap.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (super.replace(key, oldValue, newValue)) {
            updateTime(key);
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        V prev = super.replace(key, value);
        if (prev != null)
            updateTime(key);
        return prev;

    }

    @Override
    public int hashCode() {
        return getWrapped().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return getWrapped().equals(o);
    }

    @Override
    public String toString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : key);
            long time = getUpdateTime(key);
            sb.append("<");
            sb.append(time);
            sb.append(">");
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

}
