package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.esm.EsmDomainMap;
import net.bodz.bas.esm.EsmImports;
import net.bodz.bas.esm.ITsImporter;
import net.bodz.bas.esm.TypeScriptWriter;
import net.bodz.bas.esm.util.ITsImporterAware;
import net.bodz.bas.esm.util.TsConfig;
import net.bodz.bas.io.BCharOut;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;
import net.bodz.bas.t.tuple.QualifiedName;

public abstract class DaoGen__ts
        extends DaoGen__Base
        implements
            ITsImporterAware {

    TypeScriptWriter tsOut;

    public DaoGen__ts(DaoGenProject project, ClassPathInfo name) {
        super(project, name);
    }

    @Override
    protected String getExtension() {
        return TS;
    }

    @Override
    public boolean build(ITreeOut out, ITableMetadata model) {
        return buildTs(out, model);
    }

    @Override
    public ITsImporter getTsImporter() {
        return tsOut;
    }

    @Override
    public QualifiedName getThisType() {
        return pathInfo.getQName();
    }

    protected final boolean buildTs(ITreeOut out, ITableMetadata model) {
        BCharOut buf = new BCharOut();

        QualifiedName qName = pathInfo.getQName();
        EsmImports imports = EsmImports.forLocal(qName);

        EsmDomainMap domainMap = TsConfig.buildDomainMap(project.web.baseDir);
        tsOut = new TypeScriptWriter(qName, buf.indented(), imports, domainMap);

        buildTsBody(tsOut, model);

        int lines = tsOut.im.dump(out, null);
        if (lines > 0)
            out.println();

        out.print(buf);
        out.flush();
        return true;
    }

    protected abstract void buildTsBody(TypeScriptWriter out, ITableMetadata model);

}
