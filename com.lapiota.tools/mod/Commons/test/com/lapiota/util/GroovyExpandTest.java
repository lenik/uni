package com.lapiota.util;

import static net.bodz.bas.test.TestDefs.END;
import static net.bodz.bas.test.TestDefs.EQ;
import net.bodz.bas.test.TestDefs;
import net.bodz.bas.test._TestEval;
import net.bodz.bas.types.HashTextMap;
import net.bodz.bas.types.TextMap;

import org.junit.Test;

import com.lapiota.util.GroovyExpand;

public class GroovyExpandTest {

    @Test
    public void testProcess() {
        final TextMap<Object> vars = new HashTextMap<Object>();
        vars.put("name", "lenik"); //$NON-NLS-1$ //$NON-NLS-2$
        vars.put("age", 13); //$NON-NLS-1$
        vars.put("where", "there"); //$NON-NLS-1$ //$NON-NLS-2$

        final GroovyExpand ge = new GroovyExpand(vars);

        TestDefs.tests(new _TestEval<String>() {
            public Object eval(String input) throws Throwable {
                if (isBreakpoint())
                    System.err.println(input);
                return ge.compileAndEvaluate(input);
            }
        }, //
                EQ("abc", "abc"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("hello <%= name %>", "hello lenik"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("<%=age++%><<%=age++%>", "13<14"), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("now, <% out.print(age); %>. ", "now, 15. "), // //$NON-NLS-1$ //$NON-NLS-2$
                EQ("<% for (int i=0;i<10;i++) {%><%=i%>,<%}%>", //$NON-NLS-1$
                        "0,1,2,3,4,5,6,7,8,9,"), // //$NON-NLS-1$
                END);
    }

}
