package net.bodz.uni.echo.resource;

import java.net.URL;

public class ClassResourceProvider
        extends AbstractResourceProvider {

    private final Class<?> clazz;

    public ClassResourceProvider(Class<?> clazz) {
        super("class:" + clazz.getName());
        this.clazz = clazz;
    }

    @Override
    public URL getResource(String path) {
        URL resource = clazz.getResource(path);
        return resource;
    }

}
