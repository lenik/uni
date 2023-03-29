package net.bodz.bas.type.overloaded;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class OverloadedConstructors<T>
        extends ArrayList<Constructor<T>> {

    private static final long serialVersionUID = 1L;

    public ConstructorMap<T> distinguishablization() {
        return distinguishablization(DistinguishableNaming.defaultNamingCandidates);
    }

    /**
     * @return <code>null</code> if all naming strategy failed.
     */
    public ConstructorMap<T> distinguishablization(DistinguishableNaming... namingCandidates) {
        ConstructorMap<T> map = new ConstructorMap<T>();
        L: for (DistinguishableNaming candidate : namingCandidates) {
            for (Constructor<T> m : this) {
                String name = candidate.getName(m);
                Constructor<T> prev = map.put(name, m);
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
