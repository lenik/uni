package net.bodz.uni.jnigen;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OverloadedCtors
        extends ArrayList<Constructor<?>> {

    private static final long serialVersionUID = 1L;

    public Map<String, Constructor<?>> distinguishablization() {
        return distinguishablization(DistinguishableNaming.defaultNamingCandidates);
    }

    /**
     * @return <code>null</code> if all naming strategy failed.
     */
    public Map<String, Constructor<?>> distinguishablization(DistinguishableNaming... namingCandidates) {
        Map<String, Constructor<?>> map = new LinkedHashMap<>();
        L: for (DistinguishableNaming candidate : namingCandidates) {
            for (Constructor<?> m : this) {
                String name = candidate.getName(m);
                Constructor<?> prev = map.put(name, m);
                if (prev != null) {
                    map.clear();
                    continue L;
                }
            }
            return map;
        }
        return null;
    }

}
