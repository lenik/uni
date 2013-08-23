package net.bodz.lapiota.program.xmlfs;

import org.junit.Assert;
import org.junit.Test;

public class XmlfsTest
        extends Assert {

    @Test
    public void testGetXName() {
        class D {
            void o(String actual, String expected) {
                assertEquals(expected, actual);
            }
        }
        D d = new D();
        d.o("hello", "hello");
        d.o("a_b", "a__b");
        d.o("hi space", "hi_0020space");
    }
}

class XmlfsTag {
    public Object eval(String input)
            throws Throwable {
        return Xmlfs.getXName(input);
    }
}