package net.bodz.bas.fmt.excel.ss;

import javax.xml.stream.XMLStreamException;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;

public class SsGroup
        implements
            IXmlForm {

    String StyleID;
    String AutoFitWidth;
    String Width;

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
    }

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
    }

}
