package net.bodz.uni.echo.resource;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import net.bodz.bas.c.java.util.TextMaps;

public class DerivedResourceProviderTest
        extends AbstractResourceProviderTest {

    static MappedResourceProvider createMap(String... args) {
        return ResourceProviders.createFromMap(TextMaps.create(args));
    }

    MappedResourceProvider srcMap = createMap("a.php", "a-1", "b.php", "b-1", "b.html", "b-2", "c.html", "c-2");

    @Test
    public void testGetResource_originalFirst()
            throws IOException {
        DerivedResourceProvider derived = new DerivedResourceProvider(srcMap);
        derived.setOverlapMode(DerivedResourceOverlapMode.originalFirst);
        derived.addDerivedExtension(".html", ".php");

        URL a = derived.getResource("a.html");
        assertNotNull(a);

        URL b = derived.getResource("b.html");
        assertNotNull(b);

        URL c = derived.getResource("c.html");
        assertNotNull(c);

        URL xxx = derived.getResource("xxx.html");
        assertNull(xxx);
    }

    @Test
    public void testGetResource_DerivedOnly()
            throws IOException {
        DerivedResourceProvider derived = new DerivedResourceProvider(srcMap);
        derived.setOverlapMode(DerivedResourceOverlapMode.derivedOnly);
        derived.addDerivedExtension(".html", ".php");

        URL a = derived.getResource("a.html");
        assertNotNull(a);

        URL b = derived.getResource("b.html");
        assertNotNull(b);

        URL c = derived.getResource("c.html");
        assertNull(c);

        URL xxx = derived.getResource("xxx.html");
        assertNull(xxx);
    }

    @Test
    public void testGetResource_DerivedFirst()
            throws IOException {
        DerivedResourceProvider derived = new DerivedResourceProvider(srcMap);
        derived.setOverlapMode(DerivedResourceOverlapMode.derivedFirst);
        derived.addDerivedExtension(".html", ".php");

        URL a = derived.getResource("a.html");
        assertNotNull(a);

        URL b = derived.getResource("b.html");
        assertNotNull(b);

        URL c = derived.getResource("c.html");
        assertNotNull(c);

        URL xxx = derived.getResource("xxx.html");
        assertNull(xxx);
    }

}
