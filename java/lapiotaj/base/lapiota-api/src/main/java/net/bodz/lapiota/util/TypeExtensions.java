package net.bodz.lapiota.util;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.OutOfDomainException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.rtx.IOptions;
import net.bodz.bas.typer.std.AbstractParser;

public class TypeExtensions {

    public static class FileParser2
            extends AbstractParser<File> {

        @Override
        public File parse(String path, IOptions options)
                throws ParseException {
            if (path.startsWith("?"))
                return ModulesRoot.DEFAULT.findexp(path.substring(1));
            return new File(path);
        }

    }

    public static class XPathParser
            extends AbstractParser<XPath> {

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

    public static class OutputFormatParser
            extends AbstractParser<OutputFormat> {

        @Override
        public OutputFormat parse(String fmt, IOptions options)
                throws ParseException {
            if ("normal".equals(fmt))
                return new OutputFormat();
            try {
                int indent = Integer.parseInt(fmt);
                String tab = Strings.repeat(indent, ' ');
                return new OutputFormat(tab);
            } catch (NumberFormatException e) {
            }
            if ("pretty".equals(fmt))
                return OutputFormat.createPrettyPrint();
            if ("compact".equals(fmt))
                return OutputFormat.createCompactFormat();
            throw new OutOfDomainException(fmt);
        }

    }

}
