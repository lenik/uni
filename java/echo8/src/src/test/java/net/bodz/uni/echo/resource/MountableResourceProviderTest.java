package net.bodz.uni.echo.resource;

import java.io.IOException;

import org.junit.Test;

import net.bodz.bas.c.java.util.TextMaps;

public class MountableResourceProviderTest
        extends AbstractResourceProviderTest {

    static MappedResourceProvider createMap(String... args) {
        return ResourceProviders.createFromMap(TextMaps.create(args));
    }

    static MountableResourceProvider create(boolean unionAuto, boolean unionSorted) {
        MountableResourceProvider result = new MountableResourceProvider("test");
        result.setUnionAuto(unionAuto);
        result.setUnionSorted(unionSorted);

        MappedResourceProvider map1 = createMap("a", "a-1", "b", "b-1", "a/a", "aa1");
        MappedResourceProvider map2 = createMap("a", "a-2", "c", "c-2", "a/a", "aa2");
        MappedResourceProvider map3 = createMap("a", "a-3", "a/a", "aa3");
        result.mount("u", map1);
        result.mount("u", map2);
        result.mount("", map3);
        result.mount("a", map1);

        return result;
    }

    @Test
    public void testGetResource()
            throws IOException {
        MountableResourceProvider mounts = create(false, false);
        GetResourceTester d = new GetResourceTester(mounts);
        d.o("u/a", "a-2");
        d.o("u/b", null);
        d.o("u/c", "c-2");
        d.o("a", "a-3");
        d.o("b", null);
        d.o("a/a", "aa3");
    }

    @Test
    public void testGetResources_Single()
            throws IOException {
        MountableResourceProvider mounts = create(false, false);
        GetResourcesTester d = new GetResourcesTester(mounts);
        d.o("u/a", "a-2");
        d.o("u/b");
        d.o("u/c", "c-2");
        d.o("a", "a-3");
        d.o("b");
        d.o("c");
    }

    @Test
    public void testGetResources_Overlapped()
            throws IOException {
        MountableResourceProvider mounts = create(false, false);
        GetResourcesTester d = new GetResourcesTester(mounts);
        d.o("a/a", "aa3", "a-1");
    }

}
