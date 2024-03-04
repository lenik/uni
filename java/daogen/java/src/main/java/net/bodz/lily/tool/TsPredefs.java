package net.bodz.lily.tool;

import java.util.Collection;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.type.IndexedTypes;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.io.Stdio;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.predef.Predef;
import net.bodz.bas.t.predef.PredefMetadata;

/**
 * TypeScript Predef-consts generator
 *
 * @label predefs
 */
@ProgramName("predefs")
public class TsPredefs
        extends BasicCLI {

    ITreeOut out;

    /**
     * Generate ECMAScript Module which exports Predefs as default. By default generate JSON.
     *
     * @option -m
     */
    boolean esModule;

    /**
     * In the default flatten mode, build byField/byKey just like byName. Don't use assignments.
     *
     * When reuse is enabled, duplicated defs are replaced by assignments.
     *
     * @option -r
     */
    boolean reuse = false;

    boolean isFlatten() {
        return ! reuse;
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        out = Stdio.cout.indented();

        if (esModule)
            out.printf("export const Predefs = ");

        out.println("{");
        out.enter();

        int classIndex = 0;
        for (Class<?> clazz : IndexedTypes.list(Predef.class, true)) {
            if (classIndex++ > 0)
                out.print(", \n");

            PredefMetadata<?, ?> metadata = PredefMetadata._forClass(clazz);
            out.printf("%s: ", qqJava(metadata.getItemClass().getCanonicalName()));

            buildPredefClass(out, metadata);
        } // for class
        if (classIndex != 0)
            out.println();
        out.leave();
        out.println("};");

        if (esModule) {
            out.println("export default Predefs;");
        }

        if (reuse) {
            out.println();
            // assignments ...
        }
    }

    void buildPredefClass(ITreeOut out, PredefMetadata<?, ?> metadata) {
        out.println("{");
        out.enter();
        {
            // out.printf("\"icon\": ")

            out.print("\"byName\": ");
            buildIndex(out, metadata.getLocalValues(), TYPE_BY_NAME);

            if (isFlatten()) {
                out.print("\"byFieldName\": ");
                buildIndex(out, metadata.getLocalValues(), TYPE_BY_FIELD_NAME);
                out.print("\"byKey\": ");
                buildIndex(out, metadata.getLocalValues(), TYPE_BY_KEY);
            }

            out.printf("\"count\": %d\n", metadata.getLocalValues().size());
        }
        out.leave();
        out.print("}");
    }

    static final int TYPE_BY_NAME = 1;
    static final int TYPE_BY_FIELD_NAME = 2;
    static final int TYPE_BY_KEY = 3;

    void buildIndex(ITreeOut out, Collection<? extends Predef<?, ?>> values, int type) {
        out.println("{");
        out.enter();
        int itemIndex = 0;
        for (Predef<?, ?> localValue : values) {
            if (itemIndex++ > 0)
                out.print(", \n");

            String key;
            switch (type) {
            case TYPE_BY_NAME:
                key = localValue.getName();
                break;
            case TYPE_BY_FIELD_NAME:
                key = localValue.getFieldName();
                break;
            case TYPE_BY_KEY:
                key = localValue.getKey().toString();
                break;
            default:
                throw new UnexpectedException();
            }

            out.printf("%s: ", qqJava(key));
            buildPredef(out, localValue);
        } // for item
        if (itemIndex != 0)
            out.println();
        out.leave();
        out.println("},"); // name-map
    }

    void buildPredef(ITreeOut out, Predef<?, ?> predef) {
        out.println("{");
        out.enter();
        {
            out.printf("name: %s,\n", qqJava(predef.getName()));
            out.printf("fieldName: %s,\n", qqJava(predef.getFieldName()));

            Object key = predef.getKey();
            if (key instanceof String || key instanceof Character)
                out.printf("key: %s\n", qqJava(key.toString()));
            else
                out.printf("key: %s\n", key.toString());

            out.leave();
            out.print("}");
        }
    }

    static String qqJava(String s) {
        return StringQuote.qqJavaString(s);
    }

    public static void main(String[] args)
            throws Exception {
        new TsPredefs().execute(args);
    }

}
