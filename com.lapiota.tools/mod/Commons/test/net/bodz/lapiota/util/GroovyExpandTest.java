package net.bodz.lapiota.util;

import static net.bodz.bas.test.TestDefs.END;
import static net.bodz.bas.test.TestDefs.EQ;
import net.bodz.bas.test.TestDefs;
import net.bodz.bas.test._TestEval;
import net.bodz.bas.types.TextMap;
import net.bodz.bas.types.TextMap.HashTextMap;

import org.junit.Test;

public class GroovyExpandTest {

    @Test
    public void testProcess() {
        final TextMap<Object> vars = new HashTextMap<Object>();
        vars.put("name", "lenik");
        vars.put("age", 13);
        vars.put("where", "there");

        final GroovyExpand ge = new GroovyExpand(vars);

        TestDefs.tests(new _TestEval<String>() {
            public Object eval(String input) throws Throwable {
                if (isBreakpoint())
                    System.err.println(input);
                return ge.compileAndEvaluate(input);
            }
        }, //
                EQ("abc", "abc"), //
                EQ("hello <%= name %>", "hello lenik"), //
                EQ("<%=age++%><<%=age++%>", "13<14"), //
                EQ("now, <% out.print(age); %>. ", "now, 15. "), //
                EQ("<% for (int i=0;i<10;i++) {%><%=i%>,<%}%>",
                        "0,1,2,3,4,5,6,7,8,9,"), //
                END);
    }

}
