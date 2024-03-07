package net.bodz.lily.tool.daogen;

import net.bodz.bas.c.string.StringQuote;
import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.esm.EsmImports;
import net.bodz.bas.esm.EsmPackageMap;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.dir.web.TsConfig;
import net.bodz.lily.tool.daogen.util.ITsImporterAware;

public abstract class JavaGen__vue
        extends JavaGenFileType
        implements
            ITsImporterAware {

    TypeScriptWriter tsOut;

    public JavaGen__vue(JavaGenProject project, ClassPathInfo name) {
        super(project, name);
    }

    @Override
    protected String getExtension() {
        return VUE;
    }

    @Override
    public void build(ITreeOut out, ITableMetadata model) {
        buildVue(out, model);
    }

    protected abstract String getTitle(ITableMetadata model);

    protected boolean templateFirst() {
        return false;
    }

    @Override
    public ITsImporter getTsImporter() {
        return tsOut;
    }

    protected final void buildVue(ITreeOut out, ITableMetadata model) {
        EsmPackageMap packageMap = TsConfig.getPackageMap(project.web.baseDir);
        EsmImports imports = new EsmImports(null);
        tsOut = new TypeScriptWriter(pathInfo.getQName(), out.indented(), //
                imports, packageMap);

        TypeScriptWriter buf;

        buf = tsOut.buffer();
        buildScript1(buf, model);
        String script1 = buf.toString();

        String setupScript;
        String template;
        if (templateFirst()) {
            buf = tsOut.buffer();
            buildTemplate(buf, model);
            template = buf.toString();

            buf = tsOut.buffer();
            buildSetupScript(buf, model);
            setupScript = buf.toString();
        } else {
            buf = tsOut.buffer();
            buildSetupScript(buf, model);
            setupScript = buf.toString();

            buf = tsOut.buffer();
            buildTemplate(buf, model);
            template = buf.toString();
        }

        buf = tsOut.buffer();
        buildScript2(buf, model);
        String script2 = buf.toString();

        out.println("<script lang=\"ts\">");
        if (tsOut.im.dump(out, false) > 0)
            out.println();

        String title = getTitle(model);
        if (title != null)
            tsOut.printf("export const title = %s;\n", //
                    StringQuote.qqJavaString(title));

        if (! script1.isEmpty())
            tsOut.println(script1);
        tsOut.println("</script>");
        tsOut.println();

        tsOut.println("<script setup lang=\"ts\">");
        if (tsOut.im.dump(out, true) > 0)
            out.println();

        if (! setupScript.isEmpty())
            tsOut.println(setupScript);
        tsOut.println("</script>");
        tsOut.println();

        tsOut.print(template);

        if (! script2.isEmpty()) {
            tsOut.println("<script  lang=\"ts\">");
            tsOut.print(script2);
            tsOut.println("</script>");
            tsOut.println();
        }
        out.flush();
    }

    protected abstract void buildScript1(TypeScriptWriter out, ITableMetadata model);

    protected abstract void buildSetupScript(TypeScriptWriter out, ITableMetadata model);

    protected abstract void buildScript2(TypeScriptWriter out, ITableMetadata model);

    protected abstract void buildTemplate(TypeScriptWriter out, ITableMetadata model);

}
