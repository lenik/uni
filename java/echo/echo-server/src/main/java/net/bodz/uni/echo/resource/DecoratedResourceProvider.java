package net.bodz.uni.echo.resource;

import java.net.URL;
import java.util.List;

import net.bodz.bas.meta.decl.NotNull;
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

    public URL getResource(String path) {
        return getWrapped().getResource(path);
    }

    @NotNull
    public List<URL> getResources(@NotNull String path) {
        return getWrapped().getResources(path);
    }

    public void findResources(List<URL> resources, String path) {
        getWrapped().findResources(resources, path);
    }

}
