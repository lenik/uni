package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public class ClassLoaderResourceProvider
        extends AbstractResourceProvider {

    private ClassLoader classLoader;

    public ClassLoaderResourceProvider(ClassLoader classLoader) {
        if (classLoader == null)
            throw new NullPointerException("classLoader");
        this.classLoader = classLoader;
    }

    @Override
    public URL getResource(String path)
            throws IOException {
        URL resource = classLoader.getResource(path);
        return resource;
    }

    @Override
    public void findResources(List<URL> resources, String path)
            throws IOException {
        Enumeration<URL> enm = classLoader.getResources(path);
        while (enm.hasMoreElements()) {
            URL resource = enm.nextElement();
            resources.add(resource);
        }
    }

}
