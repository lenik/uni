package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.c.java.net.ContentURL;
import net.bodz.bas.c.java.net.URLScanner;

public class MappedResourceProvider
        extends AbstractResourceProvider {

    Map<String, URL> resourceMap;

    public MappedResourceProvider(Map<String, URL> resourceMap) {
        this.resourceMap = resourceMap;
    }

    public Map<String, URL> getResourceMap() {
        return resourceMap;
    }

    @Override
    public URL getResource(String path) {
        return resourceMap.get(path);
    }

    public static MappedResourceProvider scan(URL start, boolean recursive)
            throws IOException {
        URLScanner scanner = new URLScanner(recursive);
        Map<String, URL> resourceMap = scanner.scan(start);
        return new MappedResourceProvider(resourceMap);
    }

    public static MappedResourceProvider createFromMap(Map<String, String> textMap) {
        Map<String, URL> map = new HashMap<>();
        for (Entry<String, String> entry : textMap.entrySet()) {
            String name = entry.getKey();
            String contents = entry.getValue();
            URL resource = ContentURL.create(name, contents);
            map.put(name, resource);
        }
        return new MappedResourceProvider(map);
    }

}
