package net.bodz.bas.stat.distrib;

import java.util.Map;

public interface ICountDistribMap<K, V>
        extends
            Map<K, V> {

    long getTotalCount();

    long count(K k);

    void merge(ICountDistribMap<K, V> other);

}
