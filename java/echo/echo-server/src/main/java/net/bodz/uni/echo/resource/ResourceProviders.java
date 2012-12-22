package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.bodz.bas.c.java.net.ContentURL;
import net.bodz.bas.c.loader.ClassResource;
import net.bodz.bas.c.loader.scan.URLResourceScanner;
import net.bodz.bas.c.object.ObjectInfo;

public class ResourceProviders {

    public static MappedResourceProvider createFromMap(Map<String, String> textMap) {
        Map<String, URL> map = new HashMap<>();
        for (Entry<String, String> entry : textMap.entrySet()) {
            String name = entry.getKey();
            String contents = entry.getValue();
            URL resource = ContentURL.create("/" + name, contents);
            map.put(name, resource);
        }
        return new MappedResourceProvider(ObjectInfo.getSimpleId(map), map);
    }

    public static MappedResourceProvider scanResources(String name, URL start, boolean recursive)
            throws IOException {
        URLResourceScanner scanner = new URLResourceScanner(recursive);
        scanner.setIncludeDirectories(false);
        Map<String, URL> resourceMap = scanner.scan(start);
        return new MappedResourceProvider(name, resourceMap);
    }

    public static MappedResourceProvider scanPackageResources(Class<?> clazz, boolean recursive)
            throws IOException {
        URL packageURL = ClassResource.getPackageURL(clazz);
        return scanResources("package:" + clazz.getPackage().getName(), packageURL, recursive);
    }

    public static MappedResourceProvider scanClassResources(Class<?> clazz, boolean recursive)
            throws IOException {
        URL classDirURL = ClassResource.getClassDirURL(clazz);
        return scanResources("class:" + clazz.getName(), classDirURL, recursive);
    }

    public static UnionResourceProvider scanInheritedClassResources(Class<?> clazz, boolean recursive)
            throws IOException {
        UnionResourceProvider union = new UnionResourceProvider("class-tree:" + clazz.getName(), false);
        while (clazz != null) {
            MappedResourceProvider classDirResources = scanClassResources(clazz, recursive);
            if (!classDirResources.isEmpty())
                union.add(classDirResources);
            clazz = clazz.getSuperclass();
        }
        return union;
    }

}
