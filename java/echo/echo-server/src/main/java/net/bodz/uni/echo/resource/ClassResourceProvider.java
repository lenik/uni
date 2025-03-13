package net.bodz.uni.echo.resource;

import java.net.URL;

import net.bodz.bas.meta.decl.NotNull;
import net.bodz.bas.meta.decl.Nullable;

public class ClassResourceProvider
        extends AbstractResourceProvider {

    private final Class<?> clazz;

    public ClassResourceProvider(Class<?> clazz) {
        super("class:" + clazz.getName());
        this.clazz = clazz;
    }

    @Nullable
    @Override
    public URL getResource(@NotNull String path) {
        URL resource = clazz.getResource(path);
        return resource;
    }

}
