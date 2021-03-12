package net.bodz.uni.xml;

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

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.bodz.bas.c.string.StringUtil;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.io.IPrintOut;
import net.bodz.bas.meta.build.MainVersion;
import net.bodz.bas.meta.build.ProgramName;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.program.skel.BasicCLI;
import net.bodz.bas.t.order.EntryKeyComparator;
import net.bodz.bas.t.pojo.Pair;
import net.bodz.bas.typer.Typers;
import net.bodz.bas.typer.std.IParser;

/**
 * Simple XML document batch editor
 */
@MainVersion({ 0, 0 })
@ProgramName("xmledit")
@RcsKeywords(id = "$Id$")
public class XMLEdit
        extends BasicCLI {

    /**
     * Set encoding of input
     *
     * @option -E =FORMAT
     */
    protected Charset inputEncoding = Charset.defaultCharset();

    /**
     * Set encoding of output
     *
     * @option -e =ENCODING
     */
    protected Charset outputEncoding = Charset.defaultCharset();

    /**
     * output to this file (default stdout)
     *
     * @option -o =FILE
     */
    protected File outputFile;

    /**
     *
     * @option -O =pretty|compact
     */
    protected OutputFormat outputFormat = new OutputFormat();

    private boolean escaping = true;

    /**
     * switch escaping mode
     *
     * @option -x
     */
    protected void switchEscaping() {
        escaping = !escaping;
    }

    private File docfile;
    private Document document;
    private IParser<XPath> xpathParser = Typers.getTyper(XPath.class, IParser.class);

    public XMLEdit() {
    }

    protected Document getDocument() {
        if (document == null)
            throw new IllegalStateException(tr._("no document specified"));
        return document;
    }

    /**
     * @option --file -f =FILE
     */
    protected void setDocument(File file)
            throws DocumentException, IOException {
        save();
        docfile = file;
        SAXReader reader = new SAXReader();
        document = reader.read(file);
    }

    /**
     * @option --xml -F =XMLDOC
     */
    protected void setDocument(String xmldoc)
            throws DocumentException, IOException {
        save();
        docfile = null;
        SAXReader reader = new SAXReader();
        document = reader.read(new StringReader(xmldoc));
    }

    protected List<Node> selection;

    /**
     * @option -s =XPATH
     */
    protected void selectXpath(XPath xpath) {
        selection = xpath.selectNodes(getDocument());
    }

    /**
     * @option -S =XMLFRAG
     */
    protected void selectXML(String xmldoc)
            throws DocumentException {
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

    /**
     * Add/remove attribute
     *
     * @option -a =NAME=VALUE
     */
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

    /**
     * Remove attribute
     *
     * @option -A =NAME
     */
    protected void unsetAttribute(String name) {
        for (Node sel : getSelection()) {
            Element elm = (Element) sel;
            Attribute attr = elm.attribute(name);
            if (attr != null)
                attr.detach();
        }
    }

    /**
     * Copy selection before each node of xpath
     *
     * @option -i =XPATH
     */
    protected void insertBefore(XPath xpath)
            throws DocumentException {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element sibling = (Element) arg;
            if (sibling.isRootElement())
                throw new DocumentException(tr._("out of the root"));
            Element parent = sibling.getParent();
            List<Element> siblings = parent.elements();
            int index = siblings.indexOf(sibling);
            for (Node child : selection) {
                Element childelm = (Element) child;
                siblings.add(index, childelm.createCopy());
            }
        }
    }

    /**
     * Copy selection after each node of xpath
     *
     * @option -I =XPATH
     */
    protected void insertAfter(XPath xpath)
            throws DocumentException {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element sibling = (Element) arg;
            if (sibling.isRootElement())
                throw new DocumentException(tr._("out of the root"));
            Element parent = sibling.getParent();
            List<Element> siblings = parent.elements();
            int index = siblings.indexOf(sibling);
            for (Node child : selection) {
                Element childelm = (Element) child;
                siblings.add(++index, childelm.createCopy());
            }
        }
    }

    /**
     * Copy selection as child to each node of xpath
     *
     * @option -c =XPATH
     */
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

    /**
     * Delete each node of xpath
     *
     * @option -d =XPATH
     */
    protected void delete(XPath xpath) {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes)
            arg.detach();
    }

    /**
     * The selected text is compared between nodes to sort
     *
     * @option -T =XPATH
     */
    protected XPath orderBy;

    /**
     * Sort nodes group by siblings
     *
     * @option -t =XPATH
     */
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

    protected void sortNodes(Element parent, List<Node> nodes) {
        if (orderBy == null)
            try {
                orderBy = xpathParser.parse("text()");
            } catch (ParseException e) {
                throw new UnexpectedException(e.getMessage(), e);
            }

        List<Pair<String, Node>> pairs = new ArrayList<Pair<String, Node>>(nodes.size());
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

        Collections.sort(pairs, EntryKeyComparator.<String> getDefaultInstance());

        List<Node> siblings = parent.elements();
        for (Pair<String, Node> pair : pairs) {
            Node node = pair.second;
            node.detach();
            siblings.add(node);
        }
    }

    /**
     * @option hidden
     */
    protected void save()
            throws IOException {
        if (document == null)
            return;

        OutputStream out = null;
        FileOutputStream fileOut = null;

        if (outputFile != null)
            fileOut = new FileOutputStream(outputFile);
        else if (docfile != null)
            fileOut = new FileOutputStream(docfile);

        out = fileOut == null ? System.out : fileOut;

        try {
            String xmlenc = document.getXMLEncoding();
            if (xmlenc != null)
                outputFormat.setEncoding(xmlenc);

            XMLWriter writer = new XMLWriter(out, outputFormat);
            writer.write(document);
        } finally {
            if (fileOut != null)
                out.close();
        }
    }

    @Override
    protected void showHelpPage(IPrintOut out) {
        super.showHelpPage(out);
        out.println();
        StringUtil.helpEscapes(out);
        out.flush();
    }

    @Override
    protected void mainImpl(String... args)
            throws Exception {
        save();
    }

    public static void main(String[] args)
            throws Exception {
        new XMLEdit().execute(args);
    }

}
