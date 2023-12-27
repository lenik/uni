package net.bodz.bas.fmt.excel;

import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Element;

import net.bodz.bas.err.LoaderException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.fmt.xml.IXmlForm;
import net.bodz.bas.fmt.xml.IXmlOutput;
import net.bodz.bas.fmt.xml.xq.IElement;

public class XDocumentProperties
        implements
            IXmlForm {

    String author;
    String lastAuthor;
    String version;

    @Override
    public void writeObject(IXmlOutput out)
            throws XMLStreamException {
        out.beginElement("DocumentProperties");
        out.element("Author", author);
        out.element("LastAuthor", lastAuthor);
        out.element("Version", version);
        out.endElement();
    }

    @Override
    public void readObject(IElement element)
            throws ParseException, LoaderException {
        for (Element child : dom.childElements(element)) {
            switch (child.getTagName()) {
            case "Author":
                author = child.getTextContent();
                break;
            case "LastAuthor":
                lastAuthor = child.getTextContent();
                break;
            case "Version":
                version = child.getTextContent();
                break;
            }
        }
    }

}
