package net.bodz.uni.echo.resource;

import java.io.IOException;

import org.junit.Test;

public class ClassLoaderResourceProviderTest
        extends AbstractResourceProviderTest {

    ClassLoader classLoader = ClassLoaderResourceProviderTest.class.getClassLoader();
    String packageDir = "net/bodz/uni/echo/resource";

    @Test
    public void testGetResource()
            throws IOException {
        ClassLoaderResourceProvider provider = new ClassLoaderResourceProvider(classLoader);
        GetResourceTester d = new GetResourceTester(provider);
        d.o(packageDir + "/File1.txt", "file 1\n");
        d.o(packageDir + "/File2.txt", "file 2\n");
        d.o(packageDir + "/File-xxx", null);
    }

    @Test
    public void testGetResourceWithPrefix()
            throws IOException {
        ClassLoaderResourceProvider provider = new ClassLoaderResourceProvider(classLoader, packageDir + "/");
        GetResourceTester d = new GetResourceTester(provider);
        d.o("File1.txt", "file 1\n");
        d.o("File2.txt", "file 2\n");
        d.o("File-xxx", null);
    }

    @Test
    public void testGetResources()
            throws IOException {
        ClassLoaderResourceProvider provider = new ClassLoaderResourceProvider(classLoader);
        GetResourcesTester d = new GetResourcesTester(provider);
        d.o(packageDir + "/File1.txt", "file 1\n");
        d.o(packageDir + "/File2.txt", "file 2\n");
        d.o(packageDir + "/File-xxx");
    }

}
