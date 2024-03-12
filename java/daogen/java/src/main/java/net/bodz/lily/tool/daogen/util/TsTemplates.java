package net.bodz.lily.tool.daogen.util;

import net.bodz.bas.esm.TypeScriptWriter;

public class TsTemplates {

    public static void lazyProp_NEW(TypeScriptWriter out, String cacheVar, String propName, String instanceType) {
        out.printf("static %s: %s;\n", cacheVar, instanceType);
        out.printf("static get %s() {\n", propName);
        out.enter();
        {
            out.printf("if (this.%s == null)\n", cacheVar);
            out.enter();
            out.printf("this.%s = new %s();\n", cacheVar, instanceType);
            out.leave();

            out.printf("return this.%s;\n", cacheVar);
            out.leaveln("}");
        }
    }

    public static void lazyProp_INSTANCE(TypeScriptWriter out, String cacheVar, String propName, String instanceType) {
        out.printf("static %s: %s;\n", cacheVar, instanceType);
        out.printf("static get %s() {\n", propName);
        out.enter();
        {
            out.printf("if (this.%s == null)\n", cacheVar);
            out.enter();
            out.printf("this.%s = %s.INSTANCE;\n", cacheVar, instanceType);
            out.leave();

            out.printf("return this.%s;\n", cacheVar);
            out.leaveln("}");
        }
    }

}
