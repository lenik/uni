package net.bodz.lily.tool;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.bodz.bas.c.java.nio.file.FileFn;
import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.c.type.IndexedTypes;
import net.bodz.bas.err.IllegalUsageException;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.esm.skel01.Skel01Modules;
import net.bodz.bas.esm.util.TsTemplates;
import net.bodz.bas.esm.util.TsTypeResolver;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.io.res.ResFn;
import net.bodz.bas.log.Logger;
import net.bodz.bas.log.LoggerFactory;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.predef.Predef;
import net.bodz.bas.t.predef.PredefMetadata;
import net.bodz.bas.t.tuple.QualifiedName;

/**
 * TypeScript Predef-consts generator
 *
 * @label predefs
 */
@ProgramName("predefs")
public class TsPredefs
        extends BasicCLI {

    static final Logger logger = LoggerFactory.getLogger(TsPredefs.class);

    /**
     * Output directory.
     *
     * @option -O
     */
    Path outDir;

    /**
     * Only types with-in the specific packages are included.
     *
     * @option -p
     */
    List<String> packageNames = new ArrayList<>();

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        if (outDir == null)
            throw new IllegalUsageException("outdir isn't specified.");

        if (args.length != 0) {
            for (String arg : args) {
                Class<?> clazz = Class.forName(arg);
                generate(clazz);
            }
        } else {
            for (Class<?> clazz : IndexedTypes.list(Predef.class, true)) {
                QualifiedName qName = QualifiedName.of(clazz);

                if (!packageNames.isEmpty()) {
                    boolean found = false;
                    for (String pkg : packageNames) {
                        if (qName.within(pkg)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        continue;
                }
                generate(clazz);
            }
        }
    }

    void generate(Class<?> clazz)
            throws IOException {
        QualifiedName qName = QualifiedName.of(clazz);
        Path tsFile = qName.toPath(outDir, "ts");

        logger.info("Generate " + tsFile);

        BCharOut buf = new BCharOut();
        TypeScriptWriter tsw = new TypeScriptWriter(qName, buf.indented());

        PredefMetadata<?, ?> metadata = PredefMetadata._forClass(clazz);
        buildClass(tsw, metadata);
        tsw.println();
        buildTypeInfo(tsw, metadata);

        tsw.printf("export default %s;\n", clazz.getSimpleName());

        Path tsDir = tsFile.getParent();
        FileFn.mkdirs(tsDir);

        IPrintOut out = ResFn.path(tsFile).newPrintOut();
        tsw.im.dump(out, false);
        out.println();
        out.print(buf);
        out.close();
    }

    void buildClass(TypeScriptWriter out, PredefMetadata<?, ?> metadata) {
        QualifiedName itemType = QualifiedName.of(metadata.getItemClass());
        QualifiedName typeInfoType = itemType.append("TypeInfo");
        Class<?> keyType = metadata.getKeyType();
        String tsKeyType = new TsTypeResolver(out).resolveClass(keyType);

        out.printf("export class %s extends %s<%s> {\n", //
                itemType.name, //
                out.importName(Skel01Modules.core.Predef), //
                tsKeyType);
        {
            out.enter();
            out.println();
            TsTemplates.lazyProp_NEW(out, "_typeInfo", "TYPE", typeInfoType.name);

            out.println();
            out.printf("constructor(key: %s, name: string, label?: string, icon?: string, description?: string) {\n",
                    tsKeyType);
            {
                out.enter();
                out.println("super(key, name, label, icon, description);");
                out.leave();
            }
            out.println("}");

            for (Predef<?, ?> item : metadata.getLocalValues()) {
                out.println();

                Object key = item.getKey();
                String tsKey = key.toString();

                switch (tsKeyType) {
                case "string":
                    tsKey = StringQuote.qqJavaString(tsKey);
                    break;
                case "char":
                    tsKey = StringQuote.qJavaString(tsKey);
                    break;
                }

                out.printf("static %s = new %s(%s, %s);", //
                        item.getFieldName(), //
                        itemType.name, //
                        tsKey, //
                        StringQuote.qqJavaString(item.getName()));
            } // for item
            out.println();
            out.leave();
        } // class
        out.println("}");
    }

    void buildTypeInfo(TypeScriptWriter out, PredefMetadata<?, ?> metadata) {
        QualifiedName itemType = QualifiedName.of(metadata.getItemClass());
        QualifiedName typeInfoType = itemType.append("TypeInfo");
        Class<?> keyType = metadata.getKeyType();
        String tsKeyType = new TsTypeResolver(out).resolveClass(keyType);

        out.printf("export class %s extends %s<%s, %s> {", //
                typeInfoType.name, //
                out.importName(Skel01Modules.core.PredefType), //
                itemType.name, //
                tsKeyType);
        {
            out.enter();
            out.println();
            out.println("constructor() {");
            {
                out.enter();
                out.printf("super(%s);\n", itemType.name);
                out.leave();
            }
            out.println("}");

            out.println();
            out.leave();
        }
        out.println("}");
    }

    public static void main(String[] args)
            throws Exception {
        new TsPredefs().execute(args);
    }

}
