package net.bodz.bas.fmt.excel;

import javax.xml.stream.XMLStreamException;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.excel.ss.SsGroup;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;

public class XColumn
        implements
            IXmlForm {

    SsGroup ss = new SsGroup();

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.element("Column", "");
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
    }

}
