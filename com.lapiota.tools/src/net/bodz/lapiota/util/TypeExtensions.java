package net.bodz.lapiota.util;

import java.io.File;

import net.bodz.bas.cli.TypeParser;
import net.bodz.bas.cli.TypeParsers.FileParser;
import net.bodz.bas.lang.err.ParseException;

import org.dom4j.DocumentFactory;
import org.dom4j.XPath;

public class TypeExtensions {

    public static class FileParser2 extends FileParser {

        @Override
        public File parse(String path) throws ParseException {
            if (path.startsWith("?"))
                return Lapiota.find(path.substring(1));
            return super.parse(path);
        }

    }

    public static class XPathParser implements TypeParser<XPath> {

        @Override
        public XPath parse(String xpath) throws ParseException {
            DocumentFactory docfac = DocumentFactory.getInstance();
            return docfac.createXPath(xpath);
        }

    }

}
