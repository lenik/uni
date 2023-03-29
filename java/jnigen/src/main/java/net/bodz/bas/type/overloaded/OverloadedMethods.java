package net.bodz.bas.type.overloaded;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class OverloadedMethods
        extends ArrayList<Method> {

    private static final long serialVersionUID = 1L;

    public MethodMap distinguishablization() {
        return distinguishablization(DistinguishableNaming.defaultNamingCandidates);
    }

    /**
     * @return <code>null</code> if all naming strategy failed.
     */
    public MethodMap distinguishablization(DistinguishableNaming... namingCandidates) {
        MethodMap map = new MethodMap();
        L: for (DistinguishableNaming candidate : namingCandidates) {
            for (Method m : this) {
                String name = candidate.getName(m);
                Method prev = map.put(name, m);
                if (prev != null) {
                    map.clear();
                    continue L;
                }
            }
            map.setNaming(candidate);
            return map;
        }
        return map;
    }

}
