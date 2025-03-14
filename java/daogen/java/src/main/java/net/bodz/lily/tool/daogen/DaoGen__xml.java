package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;

public abstract class DaoGen__xml
        extends DaoGen__Base {

    public DaoGen__xml(DaoGenProject project, ClassPathInfo name) {
        super(project, name);
    }

    @Override
    protected String getExtension() {
        return XML;
    }

    @Override
    public boolean build(ITreeOut out, ITableMetadata model) {
        return buildXml(out, model);
    }

    protected final boolean buildXml(ITreeOut out, ITableMetadata model) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        XmlSourceBuffer buf = new XmlSourceBuffer(out);
        buildXmlBody(buf, model);
        out.flush();
        return true;
    }

    protected abstract void buildXmlBody(XmlSourceBuffer out, ITableMetadata model);

}
