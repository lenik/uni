package net.bodz.uni.echo.resource;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.bodz.bas.c.java.util.TextMaps;

public class UnionResourceProviderTest
        extends AbstractResourceProviderTest {

    UnionResourceProvider unsorted;
    UnionResourceProvider sorted;

    static MappedResourceProvider createMap(String... args) {
        return MappedResourceProvider.createFromMap(TextMaps.create(args));
    }

    @Before
    public void init() {
        unsorted = new UnionResourceProvider(false);
        sorted = new UnionResourceProvider(true);

        MappedResourceProvider map1 = createMap("a", "a-1", "b", "b-1");
        MappedResourceProvider map2 = createMap("a", "a-2", "c", "c-2");

        map1.setPriority(3);
        map2.setPriority(1);

        unsorted.add(map1);
        unsorted.add(map2);

        sorted.add(map1);
        sorted.add(map2);
    }

    @Test
    public void testGetResourceUnsorted()
            throws IOException {
        GetResourceTester d = new GetResourceTester(unsorted);
        d.o("a", "a-1");
        d.o("b", "b-1");
        d.o("c", "c-2");
    }

    @Test
    public void testGetResourceSorted()
            throws IOException {
        GetResourceTester d = new GetResourceTester(sorted);
        d.o("a", "a-2");
        d.o("b", "b-1");
        d.o("c", "c-2");
    }

}
