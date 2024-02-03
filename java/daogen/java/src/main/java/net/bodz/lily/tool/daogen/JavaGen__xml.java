package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.ClassPathInfo;
import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.io.ITreeOut;
import net.bodz.bas.t.catalog.ITableMetadata;

public abstract class JavaGen__xml
        extends JavaGenFileType {

    public JavaGen__xml(JavaGenProject project, ClassPathInfo name) {
        super(project, name);
    }

    @Override
    protected String getExtension() {
        return XML;
    }

    @Override
    public void build(ITreeOut out, ITableMetadata model) {
        buildXml(out, model);
    }

    protected final void buildXml(ITreeOut out, ITableMetadata model) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        XmlSourceBuffer buf = new XmlSourceBuffer(out);
        buildXmlBody(buf, model);
        out.flush();
    }

    protected abstract void buildXmlBody(XmlSourceBuffer out, ITableMetadata model);

}
