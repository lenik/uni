package net.bodz.uni.jnigen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OverloadedMethods
        extends ArrayList<Method> {

    private static final long serialVersionUID = 1L;

    public Map<String, Method> distinguishablization() {
        return distinguishablization(DistinguishableNaming.defaultNamingCandidates);
    }

    /**
     * @return <code>null</code> if all naming strategy failed.
     */
    public Map<String, Method> distinguishablization(DistinguishableNaming... namingCandidates) {
        Map<String, Method> map = new LinkedHashMap<>();
        L: for (DistinguishableNaming candidate : namingCandidates) {
            for (Method m : this) {
                String name = candidate.getName(m);
                Method prev = map.put(name, m);
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
