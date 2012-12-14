package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.meta.decl.Priority;

public abstract class AbstractResourceProvider
        implements IResourceProvider {

    String name;
    int priority;

    public AbstractResourceProvider(String name) {
        if (name == null)
            throw new NullPointerException("name");
        this.name = name;

        Priority _priority = getClass().getAnnotation(Priority.class);
        if (_priority != null)
            priority = _priority.value();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public final List<URL> getResources(String path)
            throws IOException {
        List<URL> resources = new ArrayList<URL>();
        findResources(resources, path);
        return resources;
    }

    @Override
    public void findResources(List<URL> resources, String path)
            throws IOException {
        URL resource = getResource(path);
        if (resource != null)
            resources.add(resource);
    }

    @Override
    public String toString() {
        return name;
    }

}
