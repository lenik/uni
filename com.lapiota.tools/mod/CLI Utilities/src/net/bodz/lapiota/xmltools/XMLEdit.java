package net.bodz.lapiota.xmltools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.bodz.bas.annotations.Doc;
import net.bodz.bas.annotations.Version;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.cli.Option;
import net.bodz.bas.cli.RunInfo;
import net.bodz.bas.cli.util.RcsKeywords;
import net.bodz.bas.io.CharOut;
import net.bodz.bas.lang.ControlBreak;
import net.bodz.bas.lang.err.ParseException;
import net.bodz.bas.lang.err.UnexpectedException;
import net.bodz.bas.types.Pair;
import net.bodz.bas.types.TypeParsers;
import net.bodz.bas.types._TypeParser;
import net.bodz.bas.types.util.Comparators;
import net.bodz.lapiota.util.StringUtil;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;
import net.bodz.lapiota.wrappers.BasicCLI;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

@Doc("Simple XML document batch editor")
@Version( { 0, 0 })
@RcsKeywords(id = "$Id$")
@RunInfo(lib = { "dom4j", "jaxen" })
public class XMLEdit extends BasicCLI {

    @Option(alias = "E", vnam = "FORMAT", doc = "set encoding of input")
    protected Charset      inputEncoding  = Charset.defaultCharset();

    @Option(alias = "e", vnam = "ENCODING", doc = "set encoding of output")
    protected Charset      outputEncoding = Charset.defaultCharset();

    @Option(alias = "o", vnam = "FILE", doc = "output to this file (default stdout)")
    protected File         outputFile;

    @Option(alias = "O", vnam = "pretty|compact", parser = OutputFormatParser.class)
    protected OutputFormat outputFormat   = new OutputFormat();

    private boolean        escaping       = true;

    @Option(alias = "x", doc = "switch escaping mode")
    protected void switchEscaping() {
        escaping = !escaping;
    }

    class XpathParser extends _TypeParser {
        @Override
        public XPath parse(String xpath) throws ParseException {
            xpath = StringUtil.unescape(escaping, xpath);
            if (document != null)
                return document.createXPath(xpath);
            return docfac.createXPath(xpath);
        }
    }

    private XpathParser     xpathParser = new XpathParser();

    private DocumentFactory docfac;
    private File            docfile;
    private Document        document;

    public XMLEdit() {
        docfac = DocumentFactory.getInstance();

        TypeParsers.register(XPath.class, new _TypeParser() {
            @Override
            public Object parse(String text) throws ParseException {
                return xpathParser.parse(text);
            }
        });
    }

    protected Document getDocument() {
        if (document == null)
            throw new IllegalStateException("no document specified");
        return document;
    }

    @Option(name = "file", alias = "f", vnam = "FILE")
    protected void setDocument(File file) throws DocumentException, IOException {
        save();
        docfile = file;
        SAXReader reader = new SAXReader();
        document = reader.read(file);
    }

    @Option(name = "xml", alias = "F", vnam = "XMLDOC")
    protected void setDocument(String xmldoc) throws DocumentException,
            IOException {
        save();
        docfile = null;
        SAXReader reader = new SAXReader();
        document = reader.read(new StringReader(xmldoc));
    }

    protected List<Node> selection;

    @SuppressWarnings("unchecked")
    @Option(alias = "s", vnam = "XPATH")
    protected void selectXpath(XPath xpath) {
        selection = xpath.selectNodes(getDocument());
    }

    @SuppressWarnings("unchecked")
    @Option(alias = "S", vnam = "XMLFRAG")
    protected void selectXML(String xmldoc) throws DocumentException {
        xmldoc = StringUtil.unescape(escaping, xmldoc);
        xmldoc = "<root>" + xmldoc + "</root>";
        SAXReader reader = new SAXReader();
        Document argdoc = reader.read(new StringReader(xmldoc));
        selection = argdoc.selectNodes("/root/*");
    }

    protected List<Node> getSelection() {
        if (selection == null)
            try {
                selectXpath(xpathParser.parse("//*"));
            } catch (ParseException e) {
                throw new UnexpectedException(e.getMessage(), e);
            }
        return selection;
    }

    @Option(alias = "a", vnam = "NAME=VALUE", doc = "add/remove attribute")
    protected void setAttribute(String keyval) {
        String name = keyval;
        String value = "1";
        int eq = name.indexOf('=');
        if (eq >= 0) {
            value = StringUtil.unescape(escaping, name.substring(eq + 1));
            name = name.substring(0, eq);
        }
        for (Node sel : getSelection()) {
            Element elm = (Element) sel;
            elm.addAttribute(name, value);
        }
    }

    @Option(alias = "A", vnam = "NAME", doc = "remove attribute")
    protected void unsetAttribute(String name) {
        for (Node sel : getSelection()) {
            Element elm = (Element) sel;
            Attribute attr = elm.attribute(name);
            if (attr != null)
                attr.detach();
        }
    }

    @SuppressWarnings("unchecked")
    @Option(alias = "i", vnam = "XPATH", doc = "copy selection before each node of xpath")
    protected void insertBefore(XPath xpath) throws DocumentException {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element sibling = (Element) arg;
            if (sibling.isRootElement())
                throw new DocumentException("out of the root");
            Element parent = sibling.getParent();
            List siblings = parent.elements();
            int index = siblings.indexOf(sibling);
            for (Node child : selection) {
                Element childelm = (Element) child;
                siblings.add(index, childelm.createCopy());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Option(alias = "I", vnam = "XPATH", doc = "copy selection after each node of xpath")
    protected void insertAfter(XPath xpath) throws DocumentException {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element sibling = (Element) arg;
            if (sibling.isRootElement())
                throw new DocumentException("out of the root");
            Element parent = sibling.getParent();
            List siblings = parent.elements();
            int index = siblings.indexOf(sibling);
            for (Node child : selection) {
                Element childelm = (Element) child;
                siblings.add(++index, childelm.createCopy());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Option(alias = "c", vnam = "XPATH", doc = "copy selection as child to each node of xpath")
    protected void insertChild(XPath xpath) {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element parent = (Element) arg;
            for (Node child : selection) {
                Element childelm = (Element) child;
                parent.add(childelm.createCopy());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Option(alias = "d", vnam = "XPATH", doc = "delete each node of xpath")
    protected void delete(XPath xpath) {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes)
            arg.detach();
    }

    @Option(alias = "T", vnam = "XPATH", doc = "the selected text is compared between nodes to sort")
    protected XPath orderBy;

    @SuppressWarnings("unchecked")
    @Option(alias = "t", vnam = "XPATH", doc = "sort nodes group by siblings")
    protected void sort(XPath xpath) {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        if (argnodes.size() <= 1)
            return;
        Collections.sort(argnodes, new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                if (a == b)
                    return 0;
                if (a == null)
                    return -1;
                if (b == null)
                    return 1;
                Element ap = a.getParent();
                Element bp = b.getParent();
                if (ap == bp)
                    return 0;
                if (ap == null)
                    return -1;
                if (bp == null)
                    return 1;
                return ap.getPath().compareTo(bp.getPath());
            }
        });
        List<Node> group = new ArrayList<Node>();
        Element lastParent = argnodes.get(0).getParent();
        for (Node node : argnodes) {
            group.add(node);
            if (node.getParent() != lastParent) {
                sortNodes(node.getParent(), group);
                group.clear();
                lastParent = node.getParent();
            }
        }
        if (group.size() > 0)
            sortNodes(lastParent, group);
    }

    @SuppressWarnings("unchecked")
    protected void sortNodes(Element parent, List<Node> nodes) {
        if (orderBy == null)
            try {
                orderBy = xpathParser.parse("text()");
            } catch (ParseException e) {
                throw new UnexpectedException(e.getMessage(), e);
            }

        List<Pair<String, Node>> pairs = new ArrayList<Pair<String, Node>>(
                nodes.size());
        StringBuffer ordval = new StringBuffer();
        for (Node node : nodes) {
            ordval.setLength(0);
            for (Object oval : orderBy.selectNodes(node))
                if (oval instanceof Node)
                    ordval.append(((Node) oval).getText());
                else
                    ordval.append(oval);
            String ordv = ordval.toString();
            Pair<String, Node> pair = new Pair<String, Node>(ordv, node);
            pairs.add(pair);
        }

        Collections.sort(pairs, Comparators.PAIR1);

        List<Node> siblings = parent.elements();
        for (Pair<String, Node> pair : pairs) {
            Node node = pair.second;
            node.detach();
            siblings.add(node);
        }
    }

    @Option(hidden = true)
    protected void save() throws IOException {
        if (document == null)
            return;

        OutputStream out = null;
        if (outputFile != null)
            out = new FileOutputStream(outputFile);
        else if (docfile != null)
            out = new FileOutputStream(docfile);
        else
            out = System.out;

        String xmlenc = document.getXMLEncoding();
        if (xmlenc != null)
            outputFormat.setEncoding(xmlenc);

        XMLWriter writer = new XMLWriter(out, outputFormat);
        writer.write(document);
        if (out != System.out)
            out.close();
    }

    @Override
    protected void _help(CharOut out) throws CLIException {
        try {
            super._help(out);
        } catch (ControlBreak b) {
            out.println();
            StringUtil.helpEscapes(out);
            throw b;
        }
    }

    @Override
    protected void _main(String[] args) throws Throwable {
        save();
    }

    public static void main(String[] args) throws Throwable {
        new XMLEdit().run(args);
    }

}
