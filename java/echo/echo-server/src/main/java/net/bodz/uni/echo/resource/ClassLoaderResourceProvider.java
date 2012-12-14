package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import net.bodz.bas.c.object.ObjectInfo;

public class ClassLoaderResourceProvider
        extends AbstractResourceProvider {

    private ClassLoader classLoader;
    private String prefix;

    public ClassLoaderResourceProvider(ClassLoader classLoader) {
        this(classLoader, "");
    }

    public ClassLoaderResourceProvider(ClassLoader classLoader, String prefix) {
        super("loader:" + ObjectInfo.getSimpleId(classLoader));

        if (prefix == null)
            throw new NullPointerException("prefix");

        this.classLoader = classLoader;
        this.prefix = prefix;
    }

    @Override
    public URL getResource(String path)
            throws IOException {
        if (!prefix.isEmpty())
            path = prefix + path;
        URL resource = classLoader.getResource(path);
        return resource;
    }

    @Override
    public void findResources(List<URL> resources, String path)
            throws IOException {
        if (!prefix.isEmpty())
            path = prefix + path;
        Enumeration<URL> enm = classLoader.getResources(path);
        while (enm.hasMoreElements()) {
            URL resource = enm.nextElement();
            resources.add(resource);
        }
    }

}
