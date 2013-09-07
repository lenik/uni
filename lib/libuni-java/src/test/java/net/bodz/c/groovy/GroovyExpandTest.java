package net.bodz.c.groovy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.bodz.bas.c.java.util.HashTextMap;
import net.bodz.bas.c.java.util.TextMap;
import net.bodz.c.groovy.GroovyExpand;

public class GroovyExpandTest {

    @Test
    public void testProcess() {
        final TextMap<Object> vars = new HashTextMap<Object>();
        vars.put("name", "lenik");
        vars.put("age", 13);
        vars.put("where", "there");

        final GroovyExpand ge = new GroovyExpand(vars);

        class D {
            public void o(String input, String expected) {
                String actual = ge.compileAndEvaluate(input);
                assertEquals(expected, actual);
            }
        }
        D d = new D();
        d.o("abc", "abc"); //
        d.o("hello <%= name %>", "hello lenik"); //
        d.o("<%=age++%><<%=age++%>", "13<14"); //
        d.o("now, <% out.print(age); %>. ", "now, 15. "); //
        d.o("<% for (int i=0;i<10;i++) {%><%=i%>,<%}%>", "0,1,2,3,4,5,6,7,8,9,"); //
    }

}
