package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.esm.EsmImports;
import net.bodz.bas.esm.EsmPackageMap;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.dir.web.TsUtils;

public abstract class JavaGen__vue
        extends JavaGenFileType {

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

    protected final void buildVue(ITreeOut out, ITableMetadata model) {
        EsmImports imports = new EsmImports(null);
        EsmPackageMap packageMap = TsUtils.getPackageMap();

        BCharOut buf = new BCharOut();
        TypeScriptWriter tsOut = new TypeScriptWriter(pathInfo.getQName(), buf.indented(), //
                imports, packageMap);

        TypeScriptWriter templateOut = tsOut.buffer();
        buildTemplate(templateOut, model);
        String template = templateOut.toString();

        buildScript1(tsOut, model);
        tsOut.println("</script>");
        tsOut.println();

        tsOut.println("<script setup lang=\"ts\">");
        buildSetupScript(tsOut, model);
        tsOut.println("</script>");
        tsOut.println();

        tsOut.print(template);

        TypeScriptWriter buf2 = tsOut.buffer();
        buildScript2(buf2, model);
        String script2 = buf2.toString();
        if (! script2.isEmpty()) {
            tsOut.println("<script  lang=\"ts\">");
            tsOut.print(script2);
            tsOut.println("</script>");
            tsOut.println();
        }

        // merge all buffer
        out.println("<script lang=\"ts\">");
        int lines = tsOut.im.dump(out);
        if (lines > 0)
            out.println();

        out.print(buf.toString());
        out.flush();
    }

    protected abstract void buildScript1(TypeScriptWriter out, ITableMetadata model);

    protected abstract void buildSetupScript(TypeScriptWriter out, ITableMetadata model);

    protected abstract void buildScript2(TypeScriptWriter out, ITableMetadata model);

    protected abstract void buildTemplate(TypeScriptWriter out, ITableMetadata model);

}
