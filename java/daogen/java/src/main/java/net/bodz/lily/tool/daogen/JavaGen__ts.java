package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.esm.EsmImports;
import net.bodz.bas.esm.EsmPackageMap;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;
import net.bodz.lily.tool.daogen.dir.web.TsTypeResolver;
import net.bodz.lily.tool.daogen.dir.web.TsUtils;

public abstract class JavaGen__ts
        extends JavaGenFileType {

    protected TsTypeResolver tsTypes;

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

        QualifiedName qName = pathInfo.getQName();
        EsmImports imports = EsmImports.forLocal(qName);

        EsmPackageMap packageMap = TsUtils.getPackageMap(project.web.baseDir);
        TypeScriptWriter tsOut = new TypeScriptWriter(qName, buf.indented(), imports, packageMap);
        tsTypes = new TsTypeResolver(tsOut);

        buildTsBody(tsOut, model);

        int lines = tsOut.im.dump(out, null);
        if (lines > 0)
            out.println();

        out.print(buf);
        out.flush();
    }

    protected abstract void buildTsBody(TypeScriptWriter out, ITableMetadata model);

}
