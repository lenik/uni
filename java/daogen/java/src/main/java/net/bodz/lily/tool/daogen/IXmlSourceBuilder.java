package net.bodz.lily.tool.daogen;

import net.bodz.bas.codegen.XmlSourceBuffer;
import net.bodz.bas.io.ITreeOut;

public interface IXmlSourceBuilder<model_t> {

    default void buildXml(ITreeOut out, model_t model) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        XmlSourceBuffer buf = new XmlSourceBuffer(out);
        buildXmlBody(buf, model);
        out.flush();
    }

    void buildXmlBody(XmlSourceBuffer out, model_t model);

}
