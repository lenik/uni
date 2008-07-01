package net.bodz.lapiota.util;

import net.bodz.bas.cli.TypeParser;
import net.bodz.bas.lang.err.ParseException;

import org.dom4j.DocumentFactory;
import org.dom4j.XPath;

public class TypeExtensions {

    public static class XPathParser implements TypeParser<XPath> {

        @Override
        public XPath parse(String xpath) throws ParseException {
            DocumentFactory docfac = DocumentFactory.getInstance();
            return docfac.createXPath(xpath);
        }

    }

}
