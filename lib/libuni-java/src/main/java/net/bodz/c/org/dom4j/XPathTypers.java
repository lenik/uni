package net.bodz.c.org.dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.XPath;

import net.bodz.bas.c.string.StringUtil;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractCommonTypers;
import net.bodz.bas.typer.std.IParser;

public class XPathTypers
        extends AbstractCommonTypers<XPath> {

    public XPathTypers() {
        super(XPath.class);
    }

    @Override
    protected Object queryInt(int typerIndex) {
        switch (typerIndex) {
        case IParser.typerIndex:
            return this;
        default:
            return null;
        }
    }

    @Override
    public XPath parse(String xpathExpr)
            throws ParseException {
        DocumentFactory factory = DocumentFactory.getInstance();
        return factory.createXPath(xpathExpr);
    }

    @Override
    public XPath parse(String xpathExpr, IOptions options)
            throws ParseException {
        Document document = (Document) options.getOption(Document.class);
        Boolean escaped = options.get("escaped");
        if (escaped == Boolean.TRUE)
            xpathExpr = StringUtil.unescape(xpathExpr);
        if (document == null) {
            DocumentFactory factory = DocumentFactory.getInstance();
            return factory.createXPath(xpathExpr);
        } else
            return document.createXPath(xpathExpr);
    }

}
