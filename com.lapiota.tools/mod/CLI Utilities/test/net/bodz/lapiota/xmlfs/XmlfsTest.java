package net.bodz.lapiota.xmlfs;

import static net.bodz.bas.test.TestDefs.EQ;
import net.bodz.bas.test.TestDefs;
import net.bodz.bas.test.TestEval;

import org.junit.Test;

public class XmlfsTest {

    @Test
    public void testGetXName() {
        TestDefs.tests("getXName", new TestEval<String>() {
            public Object eval(String input) throws Throwable {
                return Xmlfs.getXName(input);
            }
        }, //
                EQ("hello", "hello"), //
                EQ("a_b", "a__b"), //
                EQ("hi space", "hi_0020space") //
                );
    }
}
