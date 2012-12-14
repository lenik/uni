package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.bodz.bas.t.model.AbstractDecorator;

public abstract class DecoratedResourceProvider
        extends AbstractDecorator<IResourceProvider> {

    private static final long serialVersionUID = 1L;

    public DecoratedResourceProvider(IResourceProvider _orig) {
        super(_orig);
    }

    public int getPriority() {
        return getWrapped().getPriority();
    }

    public URL getResource(String path)
            throws IOException {
        return getWrapped().getResource(path);
    }

    public List<URL> getResources(String path)
            throws IOException {
        return getWrapped().getResources(path);
    }

    public void findResources(List<URL> resources, String path)
            throws IOException {
        getWrapped().findResources(resources, path);
    }

}
