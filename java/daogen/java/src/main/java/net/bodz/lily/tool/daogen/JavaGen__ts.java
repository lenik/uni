package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.lily.tool.daogen.dir.web.TsUtils;

public abstract class JavaGen__ts
        extends JavaGenFileType {

    public JavaGen__ts(JavaGenProject project, ClassPathInfo name) {
        super(project, name);
    }

    @Override
    protected String getExtension() {
        return TS;
    }

    @Override
    public void build(ITreeOut out, ITableMetadata model) {
        buildTs(out, model);
    }

    protected final void buildTs(ITreeOut out, ITableMetadata model) {
        BCharOut buf = new BCharOut();
        TypeScriptWriter tsOut = new TypeScriptWriter(pathInfo.getQName(), buf.indented(), TsUtils.getPackageMap());
        buildTsBody(tsOut, model);

        int lines = tsOut.im.dump(out);
        if (lines > 0)
            out.println();

        out.print(buf.toString());
        out.flush();
    }

    protected abstract void buildTsBody(TypeScriptWriter out, ITableMetadata model);

}
