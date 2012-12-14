package net.bodz.uni.echo.resource;

import java.net.URL;

public class ClassResourceProvider
        extends AbstractResourceProvider {

    private final Class<?> clazz;

    public ClassResourceProvider(Class<?> clazz) {
        if (clazz == null)
            throw new NullPointerException("clazz");
        this.clazz = clazz;
    }

    @Override
    public URL getResource(String path) {
        URL resource = clazz.getResource(path);
        return resource;
    }

}
