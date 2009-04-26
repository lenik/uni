package net.bodz.lapiota.xmlfs;

import static net.bodz.bas.test.TestDefs.EQ;
import net.bodz.bas.test.TestDefs;
import net.bodz.bas.test.TestEval;

import org.junit.Test;

public class XmlfsTest {

    @Test
    public void testGetXName() {
        TestDefs.tests("getXName", new TestEval<String>() { //$NON-NLS-1$
                    public Object eval(String input) throws Throwable {
                        return Xmlfs.getXName(input);
                    }
                }, //
                EQ("hello", "hello"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("a_b", "a__b"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("hi space", "hi_0020space") // //$NON-NLS-1$ //$NON-NLS-2$
                );
    }
}
