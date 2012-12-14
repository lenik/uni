package net.bodz.uni.echo.resource;

import java.net.URL;
import java.util.Map;

public class MappedResourceProvider
        extends AbstractResourceProvider {

    Map<String, URL> resourceMap;

    public MappedResourceProvider(String name, Map<String, URL> resourceMap) {
        super("map:" + name);
        this.resourceMap = resourceMap;
    }

    public Map<String, URL> getResourceMap() {
        return resourceMap;
    }

    public boolean isEmpty() {
        return resourceMap.isEmpty();
    }

    @Override
    public URL getResource(String path) {
        return resourceMap.get(path);
    }

}
