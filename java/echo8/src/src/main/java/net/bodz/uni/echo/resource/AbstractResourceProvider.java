package net.bodz.uni.echo.resource;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResourceProvider
        implements IResourceProvider {

    List<String> prefixes;

    public AbstractResourceProvider(String... prefixes) {
        this.prefixes = new ArrayList<String>();

        for (String prefix : prefixes) {
            while (prefix.startsWith("/"))
                prefix = prefix.substring(1);
            while (prefix.endsWith("/"))
                prefix = prefix.substring(0, prefix.length() - 1);
            this.prefixes.add(prefix);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<String> getPrefixes() {
        return prefixes;
    }

}
