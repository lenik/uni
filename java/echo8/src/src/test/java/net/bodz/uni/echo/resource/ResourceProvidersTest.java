package net.bodz.uni.echo.resource;

import java.io.IOException;

import org.junit.Test;

public class ResourceProvidersTest
        extends AbstractResourceProviderTest {

    @Test
    public void testScanClassResources()
            throws IOException {
        IResourceProvider resources = ResourceProviders.scanClassResources(Foo.class, true);
        GetResourceTester d = new GetResourceTester(resources);
        d.o("foo", "foo data\n");
        d.o("Bar/bar", "bar data\n");
    }

    @Test
    public void testScanInheritedClassResources()
            throws IOException {
        IResourceProvider resources = ResourceProviders.scanInheritedClassResources(Bar.class, true);
        GetResourceTester d = new GetResourceTester(resources);
        d.o("foo", "foo data\n");
        d.o("Bar/bar", "bar overrided\n");
    }

}

class Foo {
}

class Bar
        extends Foo {
}
