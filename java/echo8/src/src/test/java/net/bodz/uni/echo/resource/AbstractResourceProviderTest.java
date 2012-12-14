package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;

import net.bodz.bas.c.java.net.URLData;

public class AbstractResourceProviderTest
        extends Assert {

    public static class GetResourceTester {
        IResourceProvider provider;

        public GetResourceTester(IResourceProvider provider) {
            this.provider = provider;
        }

        void o(String path, String expected)
                throws IOException {
            URL resource = provider.getResource(path);
            if (expected == null) {
                assertNull(resource);
                return;
            }

            String actual = URLData.readTextContents(resource);
            assertEquals(expected, actual);
        }
    }

    public static class GetResourcesTester {
        IResourceProvider provider;

        public GetResourcesTester(IResourceProvider provider) {
            this.provider = provider;
        }

        void o(String path, String... expectedList)
                throws IOException {
            List<URL> resources = provider.getResources(path);

            assertEquals(expectedList.length, resources.size());

            for (int i = 0; i < expectedList.length; i++) {
                URL resource = resources.get(i);
                String actual = URLData.readTextContents(resource);
                String expected = expectedList[i];
                assertEquals(expected, actual);
            }
        }
    }

}
