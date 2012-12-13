package net.bodz.uni.echo.resource;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.bodz.bas.t.preorder.PrefixMap;

public class UnionResourceProvider
        implements IResourceProvider {

    PrefixMap<List<IResourceProvider>> prefixMap;

    public UnionResourceProvider() {
        prefixMap = new PrefixMap<>();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<String> getPrefixes() {
        return Arrays.asList("");
    }

    @Override
    public URL getResource(String path) {
        Entry<String, List<IResourceProvider>> entry = prefixMap.meetEntry(path);
        while (entry != null) {
            for (IResourceProvider provider : entry.getValue()) {

                URL resource = provider.getResource(path);
                if (resource != null)
                    return resource;

                entry = prefixMap.higherEntry(entry.getKey());
                if (entry == null)
                    break;
                String prefix = entry.getKey();
                if (!path.startsWith(prefix))
                    break;
            }
        }
        return null;
    }

}
