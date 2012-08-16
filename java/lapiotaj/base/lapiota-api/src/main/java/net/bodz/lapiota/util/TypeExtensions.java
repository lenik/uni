package net.bodz.lapiota.util;

import java.io.File;

import net.bodz.bas.c.string.Strings;
import net.bodz.bas.err.OutOfDomainException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.snm.abc.ModulesRoot;
import net.bodz.bas.traits.AbstractParser;

import org.dom4j.DocumentFactory;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;

public class TypeExtensions {

    public static class FileParser2
            extends AbstractParser<File> {

        @Override
        public File parse(String path)
                throws ParseException {
            if (path.startsWith("?"))
                return ModulesRoot.DEFAULT.findexp(path.substring(1));
            return new File(path);
        }

    }

    public static class XPathParser
            extends AbstractParser<XPath> {

        @Override
        public XPath parse(String xpath)
                throws ParseException {
            DocumentFactory factory = DocumentFactory.getInstance();
            return factory.createXPath(xpath);
        }

    }

    public static class OutputFormatParser
            extends AbstractParser<OutputFormat> {

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
