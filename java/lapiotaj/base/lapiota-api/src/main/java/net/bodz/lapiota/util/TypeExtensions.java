package net.bodz.lapiota.util;

import java.io.File;

import javax.xml.xpath.XPath;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.OutOfDomainException;
import net.bodz.bas.snm.abc.ModulesRoot;

public class TypeExtensions {

    public static class FileParser2
            extends FileParser {

        @Override
        public File parse(String path)
                throws ParseException {
            if (path.startsWith("?"))
                return ModulesRoot.DEFAULT.findexp(path.substring(1));
            return super.parse(path);
        }

    }

    public static class XPathParser
            implements TypeParser {

        @Override
        public XPath parse(String xpath)
                throws ParseException {
            DocumentFactory docfac = DocumentFactory.getInstance();
            return docfac.createXPath(xpath);
        }

    }

    public static class OutputFormatParser
            implements TypeParser {

        @Override
        public OutputFormat parse(String fmt)
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
