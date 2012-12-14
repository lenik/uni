package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.bodz.bas.t.order.PriorityComparator;

public class UnionResourceProvider
        extends AbstractResourceProvider
        implements Iterable<IResourceProvider> {

    private Set<IResourceProvider> resourceProviders;

    public UnionResourceProvider(String name, boolean sorted) {
        super("union:" + name);
        if (sorted)
            resourceProviders = new TreeSet<>(PriorityComparator.INSTANCE);
        else
            resourceProviders = new LinkedHashSet<>();
    }

    public boolean isEmpty() {
        return resourceProviders.isEmpty();
    }

    @Override
    public Iterator<IResourceProvider> iterator() {
        return resourceProviders.iterator();
    }

    public void add(IResourceProvider resourceProvider) {
        if (resourceProvider == null)
            throw new NullPointerException("resourceProvider");
        resourceProviders.add(resourceProvider);
    }

    public void remove(IResourceProvider resourceProvider) {
        if (resourceProvider == null)
            throw new NullPointerException("resourceProvider");
        resourceProviders.remove(resourceProvider);
    }

    @Override
    public URL getResource(String path)
            throws IOException {
        for (IResourceProvider resourceProvider : resourceProviders) {
            URL resource = resourceProvider.getResource(path);
            if (resource != null)
                return resource;
        }
        return null;
    }

    @Override
    public void findResources(List<URL> resources, String path)
            throws IOException {
        for (IResourceProvider resourceProvider : resourceProviders)
            resourceProvider.findResources(resources, path);
    }

}
