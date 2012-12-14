package net.bodz.uni.echo.resource;

import java.io.IOException;

import org.junit.Test;

public class ClassResourceProviderTest
        extends AbstractResourceProviderTest {

    ClassResourceProvider provider = new ClassResourceProvider(ClassResourceProviderTest.class);

    @Test
    public void testGetResource()
            throws IOException {
        GetResourceTester d = new GetResourceTester(provider);
        d.o("File1.txt", "file 1\n");
        d.o("File2.txt", "file 2\n");
        d.o("File-xxx", null);
    }

    @Test
    public void testGetResources()
            throws IOException {
        GetResourcesTester d = new GetResourcesTester(provider);
        d.o("File1.txt", "file 1\n");
        d.o("File2.txt", "file 2\n");
        d.o("File-xxx");
    }

}
