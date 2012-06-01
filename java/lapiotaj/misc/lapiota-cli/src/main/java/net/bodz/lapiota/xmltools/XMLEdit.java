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

import net.bodz.bas.cli.BasicCLI;
import net.bodz.bas.cli.CLIException;
import net.bodz.bas.err.ParseException;
import net.bodz.bas.err.UnexpectedException;
import net.bodz.bas.loader.boot.BootInfo;
import net.bodz.bas.meta.build.RcsKeywords;
import net.bodz.bas.meta.build.Version;
import net.bodz.bas.sio.IPrintOut;
import net.bodz.bas.traits.IParser;
import net.bodz.bas.util.Pair;
import net.bodz.lapiota.nls.CLINLS;
import net.bodz.lapiota.util.StringUtil;
import net.bodz.lapiota.util.TypeExtensions.OutputFormatParser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.objectweb.asm.Attribute;

/**
 * Simple XML document batch editor
 */
@BootInfo(syslibs = { "dom4j", "jaxen" })
@RcsKeywords(id = "$Id$")
@Version({ 0, 0 })
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
    @ParseBy(OutputFormatParser.class)
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

    class XpathParser
            implements IParser<XPath> {
        @Override
        public XPath parse(String xpath)
                throws ParseException {
            xpath = StringUtil.unescape(escaping, xpath);
            if (document != null)
                return document.createXPath(xpath);
            return docfac.createXPath(xpath);
        }
    }

    private XpathParser xpathParser = new XpathParser();

    private DocumentFactory docfac;
    private File docfile;
    private Document document;

    public XMLEdit() {
        docfac = DocumentFactory.getInstance();

        TypeParsers.register(XPath.class, new IParser<XPath>() {
            @Override
            public Object parse(String text)
                    throws ParseException {
                return xpathParser.parse(text);
            }
        });
    }

    protected Document getDocument() {
        if (document == null)
            throw new IllegalStateException(CLINLS.getString("XMLEdit.noDocument")); //$NON-NLS-1$
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
    @SuppressWarnings("unchecked")
    protected void selectXpath(XPath xpath) {
        selection = xpath.selectNodes(getDocument());
    }

    /**
     * @option -S =XMLFRAG
     */
    @SuppressWarnings("unchecked")
    protected void selectXML(String xmldoc)
            throws DocumentException {
        xmldoc = StringUtil.unescape(escaping, xmldoc);
        xmldoc = "<root>" + xmldoc + "</root>"; //$NON-NLS-1$ //$NON-NLS-2$
        SAXReader reader = new SAXReader();
        Document argdoc = reader.read(new StringReader(xmldoc));
        selection = argdoc.selectNodes("/root/*"); //$NON-NLS-1$
    }

    protected List<Node> getSelection() {
        if (selection == null)
            try {
                selectXpath(xpathParser.parse("//*")); //$NON-NLS-1$
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
        String value = "1"; //$NON-NLS-1$
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
    @SuppressWarnings("unchecked")
    protected void insertBefore(XPath xpath)
            throws DocumentException {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element sibling = (Element) arg;
            if (sibling.isRootElement())
                throw new DocumentException(CLINLS.getString("XMLEdit.outOfRoot")); //$NON-NLS-1$
            Element parent = sibling.getParent();
            List siblings = parent.elements();
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
    @SuppressWarnings("unchecked")
    protected void insertAfter(XPath xpath)
            throws DocumentException {
        List<Node> argnodes = xpath.selectNodes(getDocument());
        for (Node arg : argnodes) {
            Element sibling = (Element) arg;
            if (sibling.isRootElement())
                throw new DocumentException(CLINLS.getString("XMLEdit.outOfRoot")); //$NON-NLS-1$
            Element parent = sibling.getParent();
            List siblings = parent.elements();
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
                orderBy = xpathParser.parse("text()"); //$NON-NLS-1$
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

        Collections.sort(pairs, Comparators.PAIRK);

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
    protected void _help(IPrintOut out)
            throws CLIException {
        super._help(out);
        out.println();
        StringUtil.helpEscapes(out);
        out.flush();
    }

    @Override
    protected void doMain(String[] args)
            throws Exception {
        save();
    }

    public static void main(String[] args)
            throws Exception {
        new XMLEdit().run(args);
    }

}