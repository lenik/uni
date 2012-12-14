package net.bodz.uni.echo.resource;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.bodz.bas.c.java.util.TextMaps;

public class MappedResourceProviderTest
        extends AbstractResourceProviderTest {

    MappedResourceProvider provider;

    static MappedResourceProvider create(String... args) {
        return MappedResourceProvider.createFromMap(TextMaps.create(args));
    }

    @Before
    public void init() {
        provider = create(//
                "a", "1", //
                "a/b", "1/2");
    }

    @Test
    public void testGetResource()
            throws IOException {
        GetResourceTester d = new GetResourceTester(provider);
        d.o("a", "1");
        d.o("a/b", "1/2");
        d.o("a/xxx", null);
        d.o("xxx", null);
    }

}
